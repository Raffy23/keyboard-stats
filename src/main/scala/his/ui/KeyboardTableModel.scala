package his.ui

import java.awt.event.KeyEvent

import his.util.i18n._
import scalafx.application.Platform
import scalafx.beans.property.ReadOnlyStringWrapper
import scalafx.scene.control.{TreeItem, TreeTableColumn, TreeTableView}

import scala.collection.concurrent.TrieMap

/**
  * Created by: 
  *
  * @author Raphael
  * @version 16.07.2018
  */
class KeyboardTableModel(table: TreeTableView[KeyDataProperty], data: TrieMap[Int, Long]) {

  private val keyCodeColumn = new TreeTableColumn[KeyDataProperty, String]("table.keycode".localize)
  keyCodeColumn.cellValueFactory  = { p => ReadOnlyStringWrapper(KeyEvent.getKeyText(p.value.value.value.keyCode.value)) }
  keyCodeColumn.prefWidth = 170


  private val keyCountColumn = new TreeTableColumn[KeyDataProperty, Long]("table.count".localize)
  keyCountColumn.cellValueFactory = { p => p.value.value.value.count }

  table.getColumns.setAll(keyCodeColumn, keyCountColumn)
  table.showRoot = false

  private val root = new TreeItem[KeyDataProperty]()
  table.root = root

  Platform.runLater(update())

  def update(): Unit = {
    root.children = data.map { case (keyCode, _) => new TreeItem(new KeyDataProperty(keyCode, data)) }.toSeq
  }

  def refresh(keyCode: Int): Unit = {
    root.children.find(_.getValue.keyCode.get() == keyCode).map(_.getValue) match {
      case Some(key) => key.refresh()
      case None => root.children ++= Seq(new TreeItem(new KeyDataProperty(keyCode, data)))
    }
  }

}
