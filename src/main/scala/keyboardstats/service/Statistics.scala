package keyboardstats.service

import java.io.{File, PrintWriter}
import java.time.LocalDate

import scala.collection.concurrent.TrieMap
import scala.concurrent.Future
import scala.io.Source
import scala.concurrent.ExecutionContext.Implicits.global
import keyboardstats.ui.Defaults._

import scala.util.Try

/**
  * Created by: 
  *
  * @author Raphael
  * @version 15.07.2018
  */
object Statistics {

  import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

  type App = String
  type KeyRecords = TrieMap[Int, Long]
  type Record = TrieMap[App, KeyRecords]

  implicit class KeyRecordClass(map: KeyRecords) {
    def updateValue(key: Int, uFunc: (Long) => Long): Unit = {
      map.put(key, uFunc(map.getOrElse(key, 0)))
    }
  }

  implicit class KeyCountExtractor(record: Record) {
    def getKeyCount: Long = Try(record.values.map(_.values.sum).sum).getOrElse(0L)
    def filter(app: App): Record = record.filter(_._1 == app)
  }

  implicit class KeyCountListExtractor(records: Seq[Record]) {
    def getKeyCount: Long = Try(records.map(_.getKeyCount).sum).getOrElse(0L)
    def filter(app: App): Seq[Record] = records.map(_.filter(_._1 == app))
  }

  implicit class KeyRecordCompacter(records: Seq[KeyRecords]) {
    def compact: KeyRecords = {
      val compactRecord = new KeyRecords
      records.flatMap(_.keys).distinct.foreach(keyCode =>
        compactRecord.put(keyCode, records.flatMap(_.get(keyCode)).sum)
      )

      compactRecord
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


  def getRecord(date: LocalDate): Record = statistics.getOrElseUpdate(date, loadRecordFromDisk(date)())

  def getRecords(start: LocalDate, end: LocalDate): Seq[Record] =
    (0 to start.until(end).getDays).map(x => getRecord(start.plusDays(x)))

  def getToday: Record = getRecord(LocalDate.now())
  def getYesterday: Record = getRecord(LocalDate.now().minusDays(1))

  def all(start: LocalDate, end: LocalDate): Seq[KeyRecords] =
    (0 to start.until(end).getDays).map(x => aggrStats.getOrElse(start.plusDays(x), new KeyRecords))

  def all(localDate: LocalDate = LocalDate.now()): KeyRecords =
    aggrStats.getOrElseUpdate(localDate, loadKeyRecordFromDisk(localDate)())


  def app(app: String, localDate: LocalDate = LocalDate.now()): KeyRecords =
    statistics.getOrElseUpdate(localDate, new Record).getOrElse(app, new KeyRecords)

  def app(app: String, start: LocalDate, end: LocalDate): Seq[KeyRecords] =
    (0 to start.until(end).getDays).map(x => statistics.getOrElseUpdate(start.plusDays(x), new Record).getOrElse(app, new KeyRecords))

  def syncToDisk(date: LocalDate = LocalDate.now())(file: String = DEFAULT_SAVE_FILE(date)): Unit = Future { blockingSyncToDisk(date)(file) }

  def blockingSyncToDisk(date: LocalDate = LocalDate.now())(file: String = DEFAULT_SAVE_FILE(date)): Unit = {
    val writer = new PrintWriter(new File(file))
    writer.print(getRecord(date).toMap.asJson.noSpaces)
    writer.flush()
    writer.close()
  }

  def syncFromDisk(date: LocalDate = LocalDate.now())(file: String = DEFAULT_SAVE_FILE(date)): Future[Unit] =
    loadFromDisk(date)().map(record => updateDataSet(date, record))

  def loadFromDisk(date: LocalDate)(file: String = DEFAULT_SAVE_FILE(date)): Future[Record] = Future {
    decode[Record](Source.fromFile(file).getLines().mkString).fold(
      (_) => new Record,
      (x) => x
    )
  }

  private def updateDataSet(date: LocalDate, record: Record): Unit =
    record.foreach { case (app, keyRecords) =>
      keyRecords.foreach { case (keyCode, count) =>
        statistics.getOrElseUpdate(date, new Record).getOrElseUpdate(app, new KeyRecords).updateValue(keyCode, (v) => v + count)
        aggrStats.getOrElseUpdate(date, new KeyRecords).updateValue(keyCode, (v) => v + count)
      }
    }

  private def loadRecordFromDisk(date: LocalDate)(file: String = DEFAULT_SAVE_FILE(date)): Record = Try {
    val record = decode[Record](Source.fromFile(file).getLines().mkString).fold(
      (_) => new Record,
      (x) => x
    )

    updateDataSet(date, record)
    record
  }.getOrElse(new Record)

  private def loadKeyRecordFromDisk(date: LocalDate)(file: String = DEFAULT_SAVE_FILE(date)): KeyRecords = Try {
    updateDataSet(date,
      decode[Record](Source.fromFile(file).getLines().mkString).fold(
        (_) => new Record,
        (x) => x
      )
    )

    aggrStats.getOrElseUpdate(date, new KeyRecords)
  }.getOrElse(new KeyRecords)

}
