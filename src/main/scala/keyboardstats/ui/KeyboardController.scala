package keyboardstats.ui

import java.time.LocalDate
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.jfoenix.controls.{JFXComboBox, JFXDatePicker, JFXNodesList}
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import javafx.beans.value
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.FXML
import keyboardstats.service.{InputGatherer, KeyboardLayoutService, Statistics}
import keyboardstats.ui.Defaults.{DEFAULT_EXPORT_PATH, DEFAULT_KEYBOARD_LAYOUT}
import keyboardstats.ui.Implicits._
import keyboardstats.util.i18n._
import keyboardstats.util.{HeatmapGenerator, KeyEventListener, SVGUtility}
import scalafx.application.Platform
import scalafx.scene.control.TreeTableView
import scalafx.scene.layout.Pane
import scalafx.scene.web.{WebEngine, WebView}
import scalafxml.core.macros.sfxml

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by: 
  *
  * @author Raphael
  * @version 07.08.2018
  */
@sfxml class KeyboardController(kbWebView: WebView, nodeListContainer: Pane, kbDataTable: TreeTableView[KeyDataProperty],
                                @FXML cbRecApps: JFXComboBox[String], @FXML dateStart: JFXDatePicker,
                                @FXML dateEnd: JFXDatePicker) {

  private val SVG_ELEMENT = "svg2"

  private val backgroundTasks = new ScheduledThreadPoolExecutor(1)
  private val transformer = new AtomicReference(new HeatmapGenerator(KeyboardLayoutService.layouts(DEFAULT_KEYBOARD_LAYOUT), Statistics.all()))
  private val kbModel = new AtomicReference(new KeyboardTableModel(kbDataTable, Statistics.all()))

  // Init Node list with FAB Buttons
  private val jfxNodeList = new JFXNodesList()
  jfxNodeList.setSpacing(10)
  jfxNodeList.rotate = 180
  jfxNodeList.getStylesheets.add(stylesheet("fab"))
  jfxNodeList.addAnimatedNode(FAB(MaterialIcon.SAVE, "menu.save_btn".localize))
  jfxNodeList.addAnimatedNode(SmallFAB(MaterialIcon.IMAGE, "tooltip.export_as_png".localize, () => { Future {
    SVGUtility.saveAs(DEFAULT_EXPORT_PATH, "png", transformer.get().transform().head, 776*2, 236*2)}
    Platform.runLater(() => jfxNodeList.animateList(false))
  }))
  jfxNodeList.addAnimatedNode(jfxButton("JSON", "tooltip.save_to_disk".localize, "fab-small", () => { Future {
    Statistics.syncToDisk()()
    Platform.runLater(() => jfxNodeList.animateList(false))
  }}))
  nodeListContainer.children.add(jfxNodeList)

  // Setup DatePicker:
  dateStart.setValue(LocalDate.now())
  dateEnd.setValue(LocalDate.now())

  //TODO: Validation & onAction listener
  dateStart.setOnAction((_) => updateDataView())
  dateEnd.disable = true

  // Init WebView
  kbWebView.contextMenuEnabled = false
  kbWebView.engine.userStyleSheetLocation = stylesheet("svgViewer")
  kbWebView.engine.loadContent(KeyboardLayoutService.layoutToString(DEFAULT_KEYBOARD_LAYOUT))
  kbWebView.width.addListener((_,_,size) => tryResize(SVG_ELEMENT, "width", size.doubleValue() - 28.0))
  kbWebView.height.addListener((_,_,size) => tryResize(SVG_ELEMENT, "height", size.doubleValue()))

  private val layoutChangeListener = new ChangeListener[String] {
    override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
      kbWebView.engine.loadContent(KeyboardLayoutService.layoutToString(newValue))
      kbWebView.engine.doAfterLoadOnce(() => transformer.get().transform(kbWebView.engine))
    }
  }
  DEFAULT_KEYBOARD_LAYOUT.addListener(layoutChangeListener)

  // Configure ListView selection Listener
  cbRecApps.getItems.add("item.all".localize)
  cbRecApps.getSelectionModel.selectFirst()
  cbRecApps.getSelectionModel.selectedItemProperty().addListener((_, _ ,selectedItem) => updateDataView(selectedItem))

  // Register global listener
  private val keyChangeListener: KeyEventListener = (_, keyCode, app) => update(keyCode, app)
  InputGatherer.listeners.add(keyChangeListener)

  def shutdownBackgroundTasks(): Unit = {
    appListRefresher.cancel(true)
    syncStats.cancel(false)

    backgroundTasks.shutdown()

    DEFAULT_KEYBOARD_LAYOUT.removeListener(layoutChangeListener)
    InputGatherer.listeners.remove(keyChangeListener)
  }

  def update(keyCode: Int = -1, app: String = "item.all".localize): Unit = Platform.runLater(() => {
    transformer.get().transform(kbWebView.engine)

    val selectedEntry = cbRecApps.getSelectionModel.getSelectedItem
    if (keyCode > -1 && (app == selectedEntry || selectedEntry == "item.all".localize))
      kbModel.get().refresh(keyCode)
  })

  def updateAfterLoaded(): Unit = Platform.runLater(() => kbWebView.engine.doAfterLoadOnce(() => update()))

  // Only refresh ListView after some time
  private val appListRefresher = backgroundTasks.scheduleAtFixedRate(() => try {
    Platform.runLater(() => {
      Statistics.getToday.map(_.keys).foreach(_.foreach(app => {
        if(!cbRecApps.getItems.contains(app))
          cbRecApps.getItems.add(app)
      }))
    })
  } catch { case ex: Exception => ex.printStackTrace() }, 1000, 500, TimeUnit.MILLISECONDS)

  private val syncStats = backgroundTasks.scheduleAtFixedRate(() => {
    Statistics.syncToDisk()()
  }, 5, 5, TimeUnit.MINUTES)



  private implicit class CustomWebViewEngine(engine: WebEngine) {
    def doAfterLoadOnce(task: () => Unit): Unit = {
      val changeListener = new ChangeListener[Number] {
        override def changed(ov: value.ObservableValue[_ <: Number], old: Number, work: Number): Unit = {
          if (kbWebView.engine.getLoadWorker.getTotalWork == work.doubleValue())
            task()

          engine.getLoadWorker.workDoneProperty().removeListener(this)
        }
      }

      engine.getLoadWorker.workDoneProperty().addListener(changeListener)
    }
  }

  private def tryResize(elemId: String, attr: String, value: Double): Unit = {
    if (kbWebView.engine.document == null)
      return

    val elem = kbWebView.engine.document.getElementById(elemId)
    if (elem == null)
      return

    elem.setAttribute(attr, value.toInt.toString + "px")
  }

  private def updateDataView(selectedItem: String = cbRecApps.getSelectionModel.getSelectedItem): Unit = {
    val data = if(selectedItem == "item.all".localize) Statistics.all(dateStart.getValue)
    else                                    Statistics.app(selectedItem, dateStart.getValue)

    // Set selected item for async refresh
    transformer.set(new HeatmapGenerator(KeyboardLayoutService.layouts(DEFAULT_KEYBOARD_LAYOUT), data))
    kbModel.set(new KeyboardTableModel(kbDataTable,data))

    // Re-load keymap from svg & render new content
    Platform.runLater(() => {
      val width = kbWebView.engine.document.getElementById(SVG_ELEMENT).getAttribute("width")
      val height = kbWebView.engine.document.getElementById(SVG_ELEMENT).getAttribute("height")

      kbWebView.engine.loadContent(KeyboardLayoutService.layoutToString(DEFAULT_KEYBOARD_LAYOUT))
      kbWebView.engine.doAfterLoadOnce(() => {
        kbWebView.engine.document.getElementById(SVG_ELEMENT).setAttribute("width", width)
        kbWebView.engine.document.getElementById(SVG_ELEMENT).setAttribute("height", height)
        transformer.get().transform(kbWebView.engine)
      })
    })
  }
}
