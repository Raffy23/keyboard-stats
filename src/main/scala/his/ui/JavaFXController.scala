package his.ui

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import com.jfoenix.controls.{JFXNodesList, JFXTreeTableView}
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import his.service.{InputGatherer, KeyboardLayoutService, Statistics}
import his.ui.Defaults._
import his.ui.Implicits._
import his.util.i18n._
import his.util.{HeatmapGenerator, SVGUtility}
import javafx.beans.value
import javafx.beans.value.ChangeListener
import scalafx.application.Platform
import scalafx.scene.control._
import scalafx.scene.layout.Pane
import scalafx.scene.web.{WebEngine, WebView}
import scalafxml.core.macros.sfxml

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

/**
  * Created by: 
  *
  * @author Raphael
  * @version 13.07.2018
  */
@sfxml class JavaFXController(kbWebView: WebView, lvRecordedApps: ListView[String], tabKeyboard: Tab,
                              tabSettings: Tab, statsToday: Label, statsMonth: Label, statsYear: Label,
                              statsAllTime: Label, lvIgnoreApps: ListView[String], textIgnoreApp: TextField,
                              nodeListContainer: Pane, btnIgnoreApp: Button, kbDataTable: TreeTableView[KeyDataProperty]) {

  private val backgroundTasks = new ScheduledThreadPoolExecutor(1)
  private val transformer = new mutable.HashMap[String, HeatmapGenerator]()
  transformer.put("item.all".localize, new HeatmapGenerator(KeyboardLayoutService.layouts(DEFAULT_KEYBOARD_LAYOUT), Statistics.all()))

  private val selected = new AtomicReference(transformer("item.all".localize))
  private val kbModel = new AtomicReference(new KeyboardTableModel(kbDataTable, Statistics.all()))

  // Init Tabs
  initTab(tabKeyboard, MaterialIcon.KEYBOARD, "tooltip.keyboard".localize)
  initTab(tabSettings, MaterialIcon.SETTINGS, "tooltip.settings".localize)

  private val jfxNodeList = new JFXNodesList()
  jfxNodeList.setSpacing(10)
  jfxNodeList.rotate = 180
  jfxNodeList.getStylesheets.add(stylesheet("fab"))
  jfxNodeList.addAnimatedNode(FAB(MaterialIcon.SAVE, "menu.save_btn".localize))
  jfxNodeList.addAnimatedNode(SmallFAB(MaterialIcon.IMAGE, "tooltip.export_as_png".localize, () => { Future{
    SVGUtility.saveAs(DEFAULT_SAVE_PATH, "png", selected.get().transform().head, 776*2, 236*2)}
  }))
  jfxNodeList.addAnimatedNode(jfxButton("JSON", "tooltip.save_to_disk".localize, "fab-small", () => Statistics.syncToDisk()()))
  nodeListContainer.children.add(jfxNodeList)

  // Init WebView
  kbWebView.contextMenuEnabled = false
  kbWebView.engine.userStyleSheetLocation = stylesheet("svgViewer")
  kbWebView.engine.loadContent(KeyboardLayoutService.layoutToString(DEFAULT_KEYBOARD_LAYOUT))
  kbWebView.width.addListener((_,_,size) => Try(tryResize("svg2", "width", size.doubleValue() - 28.0)).recover{ case ex: Exception => ex.printStackTrace() })
  kbWebView.height.addListener((_,_,size) => Try(tryResize("svg2", "height", size.doubleValue())).recover{ case ex: Exception => ex.printStackTrace() })

  // Configure ListView selection Listener
  lvRecordedApps.items.get().addAll("item.all".localize)
  lvRecordedApps.getSelectionModel.selectFirst()
  lvRecordedApps.getSelectionModel.selectedItemProperty().addListener((_, _ ,selectedItem) => {
    if (!transformer.contains(selectedItem))
      transformer.put(selectedItem, new HeatmapGenerator(KeyboardLayoutService.layouts(DEFAULT_KEYBOARD_LAYOUT), Statistics.app(selectedItem)))

    // Set selected item for async refresh
    selected.set(transformer(selectedItem))
    kbModel.set(new KeyboardTableModel(kbDataTable, Statistics.app(selectedItem)))

    // Re-load keymap from svg & render new content
    kbWebView.engine.loadContent(KeyboardLayoutService.layoutToString(DEFAULT_KEYBOARD_LAYOUT))
    kbWebView.engine.doAfterLoadOnce(() => selected.get().transform(kbWebView.engine))
  })

  lvIgnoreApps.items.get().addAll(InputGatherer.excludedApps)

  // Register global listener
  InputGatherer.listeners.add((_, keyCode, _) => update(keyCode))

  def update(keyCode: Int = -1): Unit = Platform.runLater(() => {
    selected.get().transform(kbWebView.engine)
    if (keyCode > -1) kbModel.get().refresh(keyCode)

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

  btnIgnoreApp.onAction = (_) => {
    if (textIgnoreApp.text.value.nonEmpty) {
      InputGatherer.excludedApps.add(textIgnoreApp.text.value)
      lvIgnoreApps.items.get().add(textIgnoreApp.text.value)
    }

    textIgnoreApp.text.setValue("")
  }

  implicit class CustomWebViewEngine(engine: WebEngine) {
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

  private def initTab(tab: Tab, icon: MaterialIcon, tooltip: String): Unit = {
    tab.graphic = icon.toIcon(38.0)
    tab.tooltip = new Tooltip(tooltip).setDelay(TOOLTIP_DELAY)
  }

  private def tryResize(elemId: String, attr: String, value: Double): Unit = {
    if (kbWebView.engine.document == null)
      return

    val elem = kbWebView.engine.document.getElementById(elemId)
    if (elem == null)
      return

    elem.setAttribute(attr, value.toInt.toString + "px")
  }

  def shutdownBackgroundTasks(): Unit = {
    appListRefresher.cancel(true)
    syncStats.cancel(false)

    backgroundTasks.shutdown()
  }

}