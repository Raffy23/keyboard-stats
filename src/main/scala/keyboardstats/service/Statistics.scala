package keyboardstats.service

import java.io.{File, PrintWriter}
import java.time.LocalDate

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future
import scala.io.Source

import scala.concurrent.ExecutionContext.Implicits.global
import keyboardstats.ui.Defaults._

/**
  * Created by: 
  *
  * @author Raphael
  * @version 15.07.2018
  */
object Statistics {

  type App = String
  type KeyRecords = TrieMap[Int, Long]
  type Record = TrieMap[App, KeyRecords]

  implicit class KeyRecordClass(map: KeyRecords) {
    def updateValue(key: Int, uFunc: (Long) => Long): Unit = {
      map.put(key, uFunc(map.getOrElse(key, 0)))
    }
  }

  def DEFAULT_SAVE_FILE(day: LocalDate = LocalDate.now()) =
    s"./${DEFAULT_STATISTICS_PATH.value}/${day.getDayOfMonth}-${day.getMonthValue}-${day.getYear}.json"

  private val aggrStats = new TrieMap[LocalDate, KeyRecords]()
  private val statistics = new TrieMap[LocalDate, Record]()


  def logKeyPress(date: LocalDate, app: String, keyCode: Int): Unit = {
    statistics.getOrElseUpdate(date, new Record).getOrElseUpdate(app, new KeyRecords).updateValue(keyCode, (x) => x + 1)
    aggrStats.getOrElseUpdate(date, new KeyRecords).updateValue(keyCode, (x) => x + 1)
  }


  def getRecord(data: LocalDate): Option[Record] = statistics.get(data)
  def getRecords(start: LocalDate, end: LocalDate): Seq[Record] =
    (0 until start.until(end).getDays).flatMap(x => statistics.get(start.plusDays(x)))

  def getToday: Option[Record] = getRecord(LocalDate.now())
  def getYesterday: Option[Record] = getRecord(LocalDate.now().minusDays(1))

  def all(localDate: LocalDate = LocalDate.now()): KeyRecords = aggrStats.getOrElseUpdate(localDate, loadAllKeyRecord(localDate)())
  def app(app: String, localDate: LocalDate = LocalDate.now()): KeyRecords =
    statistics.getOrElseUpdate(localDate, new Record).getOrElseUpdate(app, new KeyRecords)

  def getTodayKeys: Long = getToday.map(_.flatMap(_._2.values).sum).getOrElse(0L)

  import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._
  def syncToDisk(date: LocalDate = LocalDate.now())(file: String = DEFAULT_SAVE_FILE(date)): Unit = Future {
    getRecord(date)
      .map(x => x.toMap.asJson.noSpaces)
      .foreach(content => {
        val writer = new PrintWriter(new File(file))
        writer.print(content)
        writer.flush()
        writer.close()
      })
  }

  def syncFromDisk(date: LocalDate = LocalDate.now())(file: String = DEFAULT_SAVE_FILE(date)): Future[Unit] =
    loadFromDisk(date)().map(_.foreach { case (app, record) =>
    record.foreach { case (keyCode, count) =>
      statistics.getOrElseUpdate(date, new Record).getOrElseUpdate(app, new KeyRecords).updateValue(keyCode, (v) => v + count)
      aggrStats.getOrElseUpdate(date, new KeyRecords).updateValue(keyCode, (v) => v + count)
    }
  })


  def loadFromDisk(date: LocalDate)(file: String = DEFAULT_SAVE_FILE(date)): Future[Record] = Future {
    decode[Record](Source.fromFile(file).getLines().mkString).fold(
      (_) => new Record,
      (x) => x
    )
  }

  private def loadAllKeyRecord(date: LocalDate)(file: String = DEFAULT_SAVE_FILE(date)): KeyRecords = try {
    decode[Record](Source.fromFile(file).getLines().mkString).fold(
      (_) => new Record,
      (x) => x
    ).foreach{ case (app, record) =>
      record.foreach { case (keyCode, count) =>
        statistics.getOrElseUpdate(date, new Record).getOrElseUpdate(app, new KeyRecords).updateValue(keyCode, (v) => v + count)
        aggrStats.getOrElseUpdate(date, new KeyRecords).updateValue(keyCode, (v) => v + count)
      }
    }

    aggrStats.getOrElseUpdate(date, new KeyRecords)
  } catch {
    case _: Exception => new KeyRecords
  }

}
