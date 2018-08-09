package keyboardstats.ui.component

import java.time.LocalDate

import keyboardstats.service.{InputGatherer, Statistics}
import keyboardstats.util.KeyEventListener
import keyboardstats.util.i18n._
import scalafx.application.Platform
import scalafx.scene.control.Label
import scalafxml.core.macros.sfxml

import scala.collection.mutable

/**
  * Created by: 
  *
  * @author Raphael
  * @version 08.08.2018
  */
@sfxml class StatisticsController(today: Label, yesterday: Label, month: Label, year: Label, selected: Label) {
  import Statistics.{KeyCountExtractor, KeyCountListExtractor}

  private val todayCount  = new mutable.HashMap[String, Long]
  private val listener: KeyEventListener = (_,_,_) => Platform.runLater(() => update(1L))

  private var appName = "item.all".localize
  private var start   = LocalDate.now()
  private var end     = LocalDate.now()



  InputGatherer.listeners.add(listener)

  todayCount(appName) = Statistics.getToday.getKeyCount
  today.text     = todayCount(appName).toString
  yesterday.text = Statistics.getYesterday.getKeyCount.toString

  month.text = "----"
  year.text  = "----"


  def update(add: Long = 0L): Unit = {
    val cCount = todayCount.getOrElse(
      appName,
      if (appName == "item.all".localize) Statistics.getToday.getKeyCount
      else Statistics.getToday.filter(appName).getKeyCount
    ) + add

    todayCount(appName) = cCount

    today.text = cCount.toString
    if (LocalDate.now().isAfter(start.minusDays(1)) && LocalDate.now().isBefore(end.plusDays(1))) {
      selected.text = (selected.text.value.toLong + add).toString
    }


    //TODO: update year and month
  }

  def updateSelectedItem(): Unit = selected.text = (
      if (appName == "item.all".localize) Statistics.getRecords(start, end).getKeyCount
      else Statistics.getRecords(start, end).filter(appName).getKeyCount
    ).toString



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
