package keyboardstats.ui.component

import java.text.NumberFormat
import java.time.LocalDate

import keyboardstats.service.{InputGatherer, Statistics}
import keyboardstats.util.KeyEventListener
import keyboardstats.util.i18n._
import scalafx.application.Platform
import scalafx.scene.control.Label
import scalafxml.core.macros.sfxml

import scala.collection.mutable
import scala.language.implicitConversions

/**
  * Created by: 
  *
  * @author Raphael
  * @version 08.08.2018
  */
@sfxml class StatisticsController(today: Label, yesterday: Label, month: Label, year: Label, selected: Label) {
  import Statistics.{KeyCountExtractor, KeyCountListExtractor}


  private implicit val formatter: NumberFormat = NumberFormat.getInstance()
  private implicit def longToFormattedString(number: Long): String = formatter.format(number)

  private val todayCount  = new mutable.HashMap[String, Long]
  private var selectedCount = 0L
  private val listener: KeyEventListener = (_,_,_) => Platform.runLater(() => update(1L))

  private var appName = "item.all".localize
  private var start   = LocalDate.now()
  private var end     = LocalDate.now()



  InputGatherer.listeners.add(listener)

  todayCount(appName) = Statistics.getToday.getKeyCount
  today.text     = todayCount(appName)
  yesterday.text = Statistics.getYesterday.getKeyCount

  month.text = "----"
  year.text  = "----"


  def update(add: Long = 0L): Unit = {
    val cCount = todayCount.getOrElse(
      appName,
      if (appName == "item.all".localize) Statistics.getToday.getKeyCount
      else Statistics.getToday.filter(appName).getKeyCount
    ) + add

    todayCount(appName) = cCount
    today.text = cCount


    if (LocalDate.now().isAfter(start.minusDays(1)) && LocalDate.now().isBefore(end.plusDays(1))) {
      selectedCount += add
      selected.text = selectedCount
    }

    //TODO: update year and month
  }

  def updateSelectedItem(): Unit = {
    selectedCount =
      if (appName == "item.all".localize) Statistics.getRecords(start, end).getKeyCount
      else Statistics.getRecords(start, end).filter(appName).getKeyCount

    selected.text = selectedCount
  }


  def setSelected(start: LocalDate, end: LocalDate): Unit = {
    this.start = start
    this.end   = end

    updateSelectedItem()
  }

  def setApp(appName: String): Unit = {
    this.appName = appName

    update()
    updateSelectedItem()
  }

  def shutdown(): Unit = {
    InputGatherer.listeners.remove(listener)
  }


}
