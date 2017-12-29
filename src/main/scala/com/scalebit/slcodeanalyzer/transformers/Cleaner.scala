package com.scalebit.slcodeanalyzer.transformers

import com.scalebit.slcodeanalyzer.GraphItem

object Cleaner {

  def clean(items:Seq[GraphItem]):Seq[GraphItem] = {
    val visibleItems = items.filter(_.visible)
    val visibleIds = visibleItems.map(_.id).toSet
    visibleItems.map(i =>
      i.copy(references = i.references.filter(r => visibleIds.contains(r.id)))
    )
  }

}
