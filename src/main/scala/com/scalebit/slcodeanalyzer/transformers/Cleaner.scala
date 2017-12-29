package com.scalebit.slcodeanalyzer.transformers

import com.scalebit.slcodeanalyzer.{GraphItem, Transformation}

object Cleaner {

  def clean(items:Seq[GraphItem]):Transformation = {
    val visibleItems = items.filter(_.visible)
    val visibleIds = visibleItems.map(_.id).toSet
    val newItems = visibleItems.map(i =>
      i.copy(references = i.references.filter(r => visibleIds.contains(r.id)))
    )

    Transformation("cleaner", newItems, Seq())
  }

}
