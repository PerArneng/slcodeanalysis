package com.scalebit.slcodeanalyzer.transformers


import java.util.regex.Pattern

import com.scalebit.slcodeanalyzer.{GraphItem, Id, Reference}

object Excluder  {

  def exclude(patterns:Seq[String], items:Seq[GraphItem]):Seq[GraphItem] = {
    val compiledPatterns = patterns.map(Pattern.compile)
    items.map(i => i.copy(references = remove(i.references, compiledPatterns).toList,
      visible = isIncluded(i.id, compiledPatterns))
    )
  }

  def remove(references:Seq[Reference], excludePatterns: Seq[Pattern]):Seq[Reference] =
    references.filter(ref => isIncluded(ref.id, excludePatterns))

  def isIncluded(id: Id, excludePatterns: Seq[Pattern]):Boolean =
    !matchesAny(id, excludePatterns)


  def matchesAny(id: Id, patterns: Seq[Pattern]):Boolean = {

    var isMatch = false

    patterns.foreach(p => {
      val localMatch = p.matcher(id.id).matches()
      if (localMatch) {
        isMatch = true
      }
    })

    isMatch
  }
}
