package his

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, OutputStreamWriter}
import java.util.concurrent.{ConcurrentHashMap, ScheduledFuture, ScheduledThreadPoolExecutor, TimeUnit}

import com.jfoenix.controls.JFXTabPane
import de.jensd.fx.glyphs.fontawesome.{FontAwesomeIcon, FontAwesomeIconView}
import his.service.{InputGatherer, KeyboardLayoutService}
import his.util.{BufferedImageTranscoder, HeatmapGenerator}
import javafx.animation.{KeyFrame, Timeline}
import org.apache.batik.transcoder.TranscoderInput
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer
import scalafx.embed.swing.SwingFXUtils
import scalafx.geometry.Side
import scalafx.scene.control.TabPane.TabClosingPolicy
import scalafx.scene.control._
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{AnchorPane, BorderPane}
import scalafx.scene.{Node, Scene}
import scalafx.util.Duration

import scala.language.implicitConversions
import scala.xml.XML

/**
  * Created by: 
  *
  * @author Raphael
  * @version 10.07.2018
  */
object Main extends JFXApp {

  implicit class sfxJFXTabPane(jFXTabPane: JFXTabPane) extends Node(jFXTabPane) { }
  implicit class sfxFontAwesomeIconView(fontAwesomeIcon: FontAwesomeIconView) extends Node(fontAwesomeIcon) { }

  implicit class FontAwesomeIconConverter(icon: FontAwesomeIcon) {
    def toIcon: Node = new FontAwesomeIconView(icon)
  }

  implicit class HakyTooltipClass(tooltip: Tooltip) {
    def setDelay(duration: Duration): Tooltip = {
      try {
        val fieldBehavior = tooltip.delegate.getClass.getDeclaredField("BEHAVIOR")
        fieldBehavior.setAccessible(true)

        val objBehavior = fieldBehavior.get(tooltip.delegate)
        val fieldTimer = objBehavior.getClass.getDeclaredField("activationTimer")
        fieldTimer.setAccessible(true)

        val objTimer = fieldTimer.get(objBehavior).asInstanceOf[Timeline]

        objTimer.getKeyFrames.clear()
        objTimer.getKeyFrames.add(new KeyFrame(duration))
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }

      tooltip
    }
  }

  implicit def integerToDuration(int: Int): Duration = new Duration(int)


  val kbImageView = new ImageView()
  kbImageView.preserveRatio = true

  val appEntries = ObservableBuffer("All")
  var selectedItem = 0

  InputGatherer.globalKeyListener.start()

  stage = new PrimaryStage {
    title = "hInput - Heatmap"

    width = 800
    height = 600

    minWidth = 800
    minHeight = 600

    scene = new Scene(800, 600) {
      content = new AnchorPane {
        val tabPane = new TabPane

        tabPane.setSide(Side.Left)
        tabPane.setTabClosingPolicy(TabClosingPolicy.Unavailable)
        tabPane.setRotateGraphic(false)

        val keyboardTab = new Tab
        keyboardTab.graphic = FontAwesomeIcon.KEYBOARD_ALT.toIcon
        keyboardTab.tooltip = new Tooltip("Keyboard").setDelay(250)
        keyboardTab.closable = false

        keyboardTab.content = new AnchorPane {
          val selector: ListView[String] = new ListView[String] {
            items = appEntries
            editable = false
            selectionModel().selectedIndexProperty().addListener(
              (_, _, newValue) => {
                if (newValue.intValue() > 0)
                  transformer.putIfAbsent(newValue.intValue(), new HeatmapGenerator(KeyboardLayoutService.layouts("qwerty"), InputGatherer.apps(appEntries(newValue.intValue()))))

                selectedItem = newValue.intValue()
              }
            )
          }

          selector.setPrefWidth(100)

          AnchorPane.setTopAnchor(selector, 0)
          AnchorPane.setBottomAnchor(selector, 0)
          AnchorPane.setLeftAnchor(selector, 0)

          AnchorPane.setAnchors(kbImageView, 0, 5, 0, 105)

          children.addAll(selector, kbImageView)
        }

        val settingsTab = new Tab
        settingsTab.graphic = FontAwesomeIcon.GEARS.toIcon
        settingsTab.tooltip = new Tooltip("Settings").setDelay(250)
        settingsTab.closable = false

        settingsTab.content = new BorderPane {
          center = new Label("Keyboard Tab")
        }

        tabPane.getTabs.addAll(keyboardTab, settingsTab)

        AnchorPane.setAnchors(tabPane, 0, 0, 0, 0)
        children.add(tabPane)
      }
    }
  }

  stage.onCloseRequest = (_) => {
    shutdown()
  }

  val ex = new ScheduledThreadPoolExecutor(1)
  val transformer = new ConcurrentHashMap[Int, HeatmapGenerator]()
  transformer.put(0, new HeatmapGenerator(KeyboardLayoutService.layouts("qwerty"), InputGatherer.all))

  val kbRefresher: ScheduledFuture[_] = ex.scheduleAtFixedRate(() => try {
    val heatmap = transformer.get(selectedItem).transform()

    val byteOut = new ByteArrayOutputStream()
    val byteWriter = new OutputStreamWriter(byteOut)
    XML.write(byteWriter, heatmap.head, "UTF-8", xmlDecl = false, null)
    byteWriter.flush()
    byteWriter.close()

    //XML.save("./output.svg", heatmap.head)

    val t = new BufferedImageTranscoder(stage.width.value.toFloat - 155, stage.height.value.toFloat)
    val byteIn = new ByteArrayInputStream(byteOut.toByteArray)
    val in = new TranscoderInput(byteIn)

    t.transcode(in, null)

    Platform.runLater(() => {
      kbImageView.image = SwingFXUtils.toFXImage(t.getImage, null)

      kbImageView.fitWidth = stage.width.value - 155
      kbImageView.fitHeight = stage.height.value
    })

  } catch { case ex: Exception => ex.printStackTrace() }, 1, 750, TimeUnit.MILLISECONDS)

  val appListRefresher: ScheduledFuture[_] = ex.scheduleAtFixedRate(() => try {
    Platform.runLater(() => {
      InputGatherer.apps.keys.foreach(app => {
        if(!appEntries.contains(app))
          appEntries += app
      })
    })
  } catch { case ex: Exception => ex.printStackTrace() }, 1000, 500, TimeUnit.MILLISECONDS)


  def shutdown(): Unit = {
    kbRefresher.cancel(true)
    appListRefresher.cancel(true)
    ex.shutdown()

    InputGatherer.globalKeyListener.destroy()
  }


  /*
  // Wait for input to start processing
  println("Press ENTER to exit ...")
  System.in.read()


  // Write Output PNG / SVG Files
  val app = "idea64.exe"
  val output = new HeatmapGenerator(qwerty, apps(app)).transform()
  val byteOut = new ByteArrayOutputStream()
  val byteWriter = new OutputStreamWriter(byteOut)

  println("write xml to buffer")
  XML.write(byteWriter, output.head, "UTF-8", xmlDecl = false, null)
  byteWriter.flush()
  byteWriter.close()

  println("write xml to file")
  XML.save("./output.svg", output.head)


  val byteIn = new ByteArrayInputStream(byteOut.toByteArray)

  val t = new BufferedImageTranscoder(776*2, 236*2)
  val in = new TranscoderInput(byteIn)

  println("transcode to png ...")
  t.transcode(in, null)


  println("save image to file")
  val image = t.getImage
  ImageIO.write(image, "png", new File("./heatmap.png"))

  println("done")
  */
}