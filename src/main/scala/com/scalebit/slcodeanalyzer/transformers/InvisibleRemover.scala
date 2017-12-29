package com.scalebit.slcodeanalyzer.transformers

import com.scalebit.slcodeanalyzer.{GraphItem, Transformation}

object InvisibleRemover {

  def remove(items:Seq[GraphItem]):Transformation = {
    val visibleItems = items.filter(_.visible)
    val visibleIds = visibleItems.map(_.id).toSet

    // fix all the references
    val newItems = visibleItems.map(i =>
      i.copy(references = i.references.filter(r => visibleIds.contains(r.id)))
    )

    Transformation("InvisibleRemover", newItems, Seq())
  }

}
