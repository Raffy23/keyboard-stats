package his.ui

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File, OutputStreamWriter}
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import his.service.{InputGatherer, KeyboardLayoutService}
import his.ui.Implicits._
import his.util.i18n._
import his.util.{BufferedImageTranscoder, HeatmapGenerator, SVGUtility}
import javax.imageio.ImageIO
import org.apache.batik.transcoder.TranscoderInput
import scalafx.application.Platform
import scalafx.scene.control.{Button, ListView, Tab, Tooltip}
import scalafx.scene.web.WebView
import scalafxml.core.macros.sfxml

import scala.collection.mutable
import scala.concurrent.Future
import scala.xml.XML
import scala.concurrent.ExecutionContext.Implicits.global
import Defaults._

/**
  * Created by: 
  *
  * @author Raphael
  * @version 13.07.2018
  */
@sfxml class JavaFXController(kbWebView: WebView, btnSaveSVG: Button,
                              lvRecordedApps: ListView[String], btnSavePNG: Button,
                              tabKeyboard: Tab, tabSettings: Tab) {

  private val backgroundTasks = new ScheduledThreadPoolExecutor(1)
  private val transformer = new mutable.HashMap[String, HeatmapGenerator]()
  transformer.put("item.all".localize, new HeatmapGenerator(KeyboardLayoutService.layouts(DEFAULT_KEYBOARD_LAYOUT), InputGatherer.all))

  private val selected = new AtomicReference[HeatmapGenerator](transformer("item.all".localize))

  // Init Tabs
  initTab(tabKeyboard, FontAwesomeIcon.KEYBOARD_ALT, "tooltip.keyboard".localize)
  initTab(tabSettings, FontAwesomeIcon.GEARS, "tooltip.settings".localize)

  // Init WebView
  kbWebView.contextMenuEnabled = false
  kbWebView.engine.loadContent(KeyboardLayoutService.layoutToString(DEFAULT_KEYBOARD_LAYOUT))

  // Configure ListView selection Listener
  lvRecordedApps.items.get().addAll("item.all".localize)
  lvRecordedApps.getSelectionModel.selectFirst()
  lvRecordedApps.getSelectionModel.selectedItemProperty().addListener((_, _ ,selectedItem) => {
    if (!transformer.contains(selectedItem))
      transformer.put(selectedItem, new HeatmapGenerator(KeyboardLayoutService.layouts(DEFAULT_KEYBOARD_LAYOUT), InputGatherer.apps(selectedItem)))

    // Set selected item for async refresh
    selected.set(transformer(selectedItem))

    // Re-load keymap from svg & render new content
    kbWebView.engine.loadContent(KeyboardLayoutService.layoutToString(DEFAULT_KEYBOARD_LAYOUT))
    kbWebView.engine.getLoadWorker.workDoneProperty().addListener((_,_,work) => {
      if (kbWebView.engine.getLoadWorker.getTotalWork == work.doubleValue())
        selected.get().transform(kbWebView.engine)
    })

  })

  // Register global listener
  InputGatherer.listeners.add((_, keyCode) => Platform.runLater(() => selected.get().transform(kbWebView.engine)))

  // Only refresh ListView after some time
  private val appListRefresher = backgroundTasks.scheduleAtFixedRate(() => try {
    Platform.runLater(() => {
      InputGatherer.apps.keys.foreach(app => {
        if(!lvRecordedApps.items.get().contains(app))
          lvRecordedApps.items.get().add(app)
      })
    })
  } catch { case ex: Exception => ex.printStackTrace() }, 1000, 500, TimeUnit.MILLISECONDS)

  // Button save Actions (execute in Future to avoid non responsive ui)
  btnSaveSVG.onAction = (_) => Future { XML.save(DEFAULT_SAVE_PATH + ".svg", selected.get().transform().head) }
  btnSavePNG.onAction = (_) => Future { SVGUtility.saveAs(DEFAULT_SAVE_PATH, "png", selected.get().transform().head, 776*2, 236*2) }

  private def initTab(tab: Tab, icon: FontAwesomeIcon, tooltip: String): Unit = {
    tab.graphic = icon.toIcon
    tab.tooltip = new Tooltip(tooltip).setDelay(TOOLTIP_DELAY)
  }

  def shutdownBackgroundTasks(): Unit = {
    appListRefresher.cancel(true)
    backgroundTasks.shutdown()
  }

}