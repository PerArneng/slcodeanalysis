package com.scalebit.slcodeanalyzer.transformers

import com.scalebit.slcodeanalyzer.{GraphItem, Id, Transformation}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object Rooter {


  def itemsFromRoot(rootName:String, items:Seq[GraphItem]):Transformation = {

    val roots = items.filter(i => i.name.equals(rootName))
    if (roots.length < 1) {
      return Transformation("rooter", items, Seq(s"no root found by the name '$rootName'"))
    }

    val rootId = roots.head.id

    val itemMap = items.map(i => i.copy(visible = false))
                       .map(i => i.id -> i).toMap

    val foundItems = new mutable.HashMap[Id, GraphItem]

    findItems(ListBuffer(rootId), itemMap, foundItems)

    Transformation("rooter", foundItems.values.toList, Seq())
  }

  @tailrec
  def findItems(ids:ListBuffer[Id], itemMap:Map[Id, GraphItem], foundItems:mutable.HashMap[Id, GraphItem]):Unit = {

    val currentId = ids.head
    val restOfIds = ids.tail

    val graphItem = itemMap(currentId).copy(visible = true)
    foundItems += currentId -> graphItem

    for (ref <- graphItem.references) restOfIds.append(ref.id)

    if (restOfIds.nonEmpty) findItems(restOfIds, itemMap, foundItems)
  }

}
