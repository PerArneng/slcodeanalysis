package com.scalebit.slcodeanalyzer.transformers

import com.scalebit.slcodeanalyzer.{GraphItem, Transformation}

import scala.collection.mutable.ListBuffer

object DuplicationRemover {

  def remove(items: Seq[GraphItem]):Transformation = {
    val groupedItems = items.groupBy(i => i.id)

    val newItems = new ListBuffer[GraphItem]
    val messages = new ListBuffer[String]

    for (group <- groupedItems) {
      if (group._2.length > 1) {

        val duplicateItemNames = group._2.map(i => s"${i.name}(${i.itemType})").mkString(", ")
        val first = group._2.head
        messages += s"${group._1.id} share the same id with '$duplicateItemNames' using '${first.name}(${first.itemType})'"
        newItems += first

      } else {
        newItems += group._2.head
      }
    }

    Transformation("duplication", newItems, messages)
  }
}
