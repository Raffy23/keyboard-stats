package his.ui

import java.time.LocalDate
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import his.service.{InputGatherer, KeyboardLayoutService, Statistics}
import his.ui.Defaults._
import his.ui.Implicits._
import his.util.i18n._
import his.util.{HeatmapGenerator, SVGUtility}
import javafx.beans.value
import javafx.beans.value.ChangeListener
import scalafx.application.Platform
import scalafx.scene.control._
import scalafx.scene.web.{WebEngine, WebView}
import scalafxml.core.macros.sfxml

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import scala.xml.XML

/**
  * Created by: 
  *
  * @author Raphael
  * @version 13.07.2018
  */
@sfxml class JavaFXController(kbWebView: WebView, btnSaveSVG: Button,
                              lvRecordedApps: ListView[String], btnSavePNG: Button,
                              tabKeyboard: Tab, tabSettings: Tab, statsToday: Label, statsMonth: Label,
                              statsYear: Label, statsAllTime: Label, btnSaveJSON: Button,
                              btn_loadFromJson: Button, lvIgnoreApps: ListView[String],
                              textIgnoreApp: TextField, btnIgnoreApp: Button) {

  private val backgroundTasks = new ScheduledThreadPoolExecutor(1)
  private val transformer = new mutable.HashMap[String, HeatmapGenerator]()
  transformer.put("item.all".localize, new HeatmapGenerator(KeyboardLayoutService.layouts(DEFAULT_KEYBOARD_LAYOUT), Statistics.all()))

  private val selected = new AtomicReference[HeatmapGenerator](transformer("item.all".localize))

  // Init Tabs
  initTab(tabKeyboard, FontAwesomeIcon.KEYBOARD_ALT, "tooltip.keyboard".localize)
  initTab(tabSettings, FontAwesomeIcon.GEARS, "tooltip.settings".localize)

  // Init WebView
  kbWebView.contextMenuEnabled = false
  kbWebView.engine.userStyleSheetLocation = getClass.getResource("/javafx/svgViewer.css").toExternalForm
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

    // Re-load keymap from svg & render new content
    kbWebView.engine.loadContent(KeyboardLayoutService.layoutToString(DEFAULT_KEYBOARD_LAYOUT))
    kbWebView.engine.doAfterLoadOnce(() => selected.get().transform(kbWebView.engine))
  })

  lvIgnoreApps.items.get().addAll(InputGatherer.excludedApps)

  // Register global listener
  InputGatherer.listeners.add((_, _, _) => update())

  def update(): Unit = Platform.runLater(() => {
    selected.get().transform(kbWebView.engine)
    statsToday.text = Statistics.getTodayKeys.toString
  })

  def updateAfterLoaded(): Unit = Platform.runLater(() => kbWebView.engine.doAfterLoadOnce(update))

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

  // Button save Actions (execute in Future to avoid non responsive ui)
  btnSaveSVG.onAction = (_) => Future { XML.save(DEFAULT_SAVE_PATH + ".svg", selected.get().transform().head) }
  btnSavePNG.onAction = (_) => Future { SVGUtility.saveAs(DEFAULT_SAVE_PATH, "png", selected.get().transform().head, 776*2, 236*2) }
  btnSaveJSON.onAction = (_) => Statistics.syncToDisk()()
  btn_loadFromJson.onAction = (_) => Statistics.loadFromDisk(LocalDate.now())().map(records => println(records)).recover{ case e: Exception => e.printStackTrace()}

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

  private def initTab(tab: Tab, icon: FontAwesomeIcon, tooltip: String): Unit = {
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
    println(attr + " to " + value)
  }

  def shutdownBackgroundTasks(): Unit = {
    appListRefresher.cancel(true)
    syncStats.cancel(false)

    backgroundTasks.shutdown()
  }

}