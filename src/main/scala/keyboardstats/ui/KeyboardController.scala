package keyboardstats.ui

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.jfoenix.controls.JFXNodesList
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import javafx.beans.value
import javafx.beans.value.{ChangeListener, ObservableValue}
import keyboardstats.service.{InputGatherer, KeyboardLayoutService, Statistics}
import keyboardstats.ui.Defaults.{DEFAULT_KEYBOARD_LAYOUT, DEFAULT_EXPORT_PATH}
import keyboardstats.ui.Implicits._
import keyboardstats.util.i18n._
import keyboardstats.util.{HeatmapGenerator, SVGUtility}
import scalafx.application.Platform
import scalafx.scene.control.{Label, ListView, TreeTableView}
import scalafx.scene.layout.Pane
import scalafx.scene.web.{WebEngine, WebView}
import scalafxml.core.macros.sfxml

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by: 
  *
  * @author Raphael
  * @version 07.08.2018
  */
@sfxml class KeyboardController(kbWebView: WebView, lvRecordedApps: ListView[String],
                                statsToday: Label, statsMonth: Label, statsYear: Label, statsAllTime: Label,
                                nodeListContainer: Pane,  kbDataTable: TreeTableView[KeyDataProperty]) {

  private val backgroundTasks = new ScheduledThreadPoolExecutor(1)
  private val transformer = new mutable.HashMap[String, HeatmapGenerator]()
  transformer.put("item.all".localize, new HeatmapGenerator(KeyboardLayoutService.layouts(DEFAULT_KEYBOARD_LAYOUT), Statistics.all()))

  private val selected = new AtomicReference(transformer("item.all".localize))
  private val kbModel = new AtomicReference(new KeyboardTableModel(kbDataTable, Statistics.all()))

  // Init Node list with FAB Buttons
  private val jfxNodeList = new JFXNodesList()
  jfxNodeList.setSpacing(10)
  jfxNodeList.rotate = 180
  jfxNodeList.getStylesheets.add(stylesheet("fab"))
  jfxNodeList.addAnimatedNode(FAB(MaterialIcon.SAVE, "menu.save_btn".localize))
  jfxNodeList.addAnimatedNode(SmallFAB(MaterialIcon.IMAGE, "tooltip.export_as_png".localize, () => { Future {
    SVGUtility.saveAs(DEFAULT_EXPORT_PATH, "png", selected.get().transform().head, 776*2, 236*2)}
    Platform.runLater(() => jfxNodeList.animateList(false))
  }))
  jfxNodeList.addAnimatedNode(jfxButton("JSON", "tooltip.save_to_disk".localize, "fab-small", () => { Future {
    Statistics.syncToDisk()()
    Platform.runLater(() => jfxNodeList.animateList(false))
  }}))
  nodeListContainer.children.add(jfxNodeList)

  // Init WebView
  kbWebView.contextMenuEnabled = false
  kbWebView.engine.userStyleSheetLocation = stylesheet("svgViewer")
  kbWebView.engine.loadContent(KeyboardLayoutService.layoutToString(DEFAULT_KEYBOARD_LAYOUT))
  kbWebView.width.addListener((_,_,size) => tryResize("svg2", "width", size.doubleValue() - 28.0))
  kbWebView.height.addListener((_,_,size) => tryResize("svg2", "height", size.doubleValue()))

  private val layoutChangeListener = new ChangeListener[String] {
    override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
      kbWebView.engine.loadContent(KeyboardLayoutService.layoutToString(newValue))
      kbWebView.engine.doAfterLoadOnce(() => selected.get().transform(kbWebView.engine))
    }
  }
  DEFAULT_KEYBOARD_LAYOUT.addListener(layoutChangeListener)


  // Configure ListView selection Listener
  lvRecordedApps.items.get().addAll("item.all".localize)
  lvRecordedApps.getSelectionModel.selectFirst()
  lvRecordedApps.getSelectionModel.selectedItemProperty().addListener((_, _ ,selectedItem) => {
    if (!transformer.contains(selectedItem))
      transformer.put(selectedItem, new HeatmapGenerator(KeyboardLayoutService.layouts(DEFAULT_KEYBOARD_LAYOUT), Statistics.app(selectedItem)))

    // Set selected item for async refresh
    selected.set(transformer(selectedItem))
    kbModel.set(new KeyboardTableModel(kbDataTable, if(selectedItem == "item.all".localize) Statistics.all() else Statistics.app(selectedItem)))

    // Re-load keymap from svg & render new content
    Platform.runLater(() => {
      kbWebView.engine.loadContent(KeyboardLayoutService.layoutToString(DEFAULT_KEYBOARD_LAYOUT))
      kbWebView.engine.doAfterLoadOnce(() => selected.get().transform(kbWebView.engine))
    })
  })

  // Register global listener
  InputGatherer.listeners.add((_, keyCode, app) => update(keyCode, app))

  def shutdownBackgroundTasks(): Unit = {
    appListRefresher.cancel(true)
    syncStats.cancel(false)

    backgroundTasks.shutdown()
    DEFAULT_KEYBOARD_LAYOUT.removeListener(layoutChangeListener)
  }

  def update(keyCode: Int = -1, app: String = "item.all".localize): Unit = Platform.runLater(() => {
    selected.get().transform(kbWebView.engine)

    val selectedEntry = lvRecordedApps.selectionModel.value.getSelectedItems.get(0)
    if (keyCode > -1 && (app == selectedEntry || selectedEntry == "item.all".localize))
      kbModel.get().refresh(keyCode)

    statsToday.text = Statistics.getTodayKeys.toString
  })

  def updateAfterLoaded(): Unit = Platform.runLater(() => kbWebView.engine.doAfterLoadOnce(() => update()))

  // Only refresh ListView after some time
  private val appListRefresher = backgroundTasks.scheduleAtFixedRate(() => try {
    Platform.runLater(() => {
      Statistics.getToday.map(_.keys).foreach(_.foreach(app => {
        if(!lvRecordedApps.items.get().contains(app))
          lvRecordedApps.items.get().add(app)
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
}
