package com.scalebit.slcodeanalyzer

import java.util.regex.Pattern

object GraphItemExcluder {

  def exclude(items:List[GraphItem], patterns: List[Pattern]): List[GraphItem] =
    items.map(i => i.copy(references = remove(i.references, patterns),
                          visible = isIncluded(i.id, patterns))
             )

  def remove(references:List[Reference], excludePatterns: List[Pattern]):List[Reference] =
    references.filter(ref => isIncluded(ref.id, excludePatterns))

  def isIncluded(id: Id, excludePatterns: List[Pattern]):Boolean =
    !matchesAny(id, excludePatterns)


  def matchesAny(id: Id, patterns: List[Pattern]):Boolean = {

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