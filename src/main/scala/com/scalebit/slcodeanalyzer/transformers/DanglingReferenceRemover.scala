package com.scalebit.slcodeanalyzer.transformers

import com.scalebit.slcodeanalyzer.{GraphItem, Id, Reference, Transformation}

import scala.collection.mutable.ListBuffer

object DanglingReferenceRemover {

  def fixReferences(item:GraphItem, existingIds:Set[Id]):(GraphItem, Seq[String]) = {

    val newReferences = new ListBuffer[Reference]
    val messages = new ListBuffer[String]

    for (reference <- item.references) {
      if (existingIds.contains(reference.id)) {
        newReferences += reference
      } else {
        messages += s"removed: '${item.id}' -> '${reference.id}' (${reference.description.getOrElse("N/A")})"
      }
    }

    (item.copy(references = newReferences.toList), messages)
  }

  def remove(items:Seq[GraphItem]):Transformation = {
    val existingIds = items.map(_.id).toSet
    val fixedItems = items.map(i => fixReferences(i, existingIds))

    Transformation("DanglingReferenceRemover",
                   fixedItems.map(_._1),
                   fixedItems.flatMap(_._2))
  }

}
