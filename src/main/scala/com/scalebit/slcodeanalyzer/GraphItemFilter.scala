package com.scalebit.slcodeanalyzer

import java.nio.file.Path
import java.util.regex.Pattern
import scala.io.Source

class GraphItemFilter(lines: List[String]) {

  private val patterns = lines.map(l => Pattern.compile(l))

  def filter(items:List[GraphItem]): List[GraphItem] =
    GraphItemFilter.filter(items, this.patterns)

}

object GraphItemFilter {

  def empty = new GraphItemFilter(List[String]())

  def createFromFile(file:Path):GraphItemFilter =
    new GraphItemFilter(
      Source.fromFile(file.toFile).getLines
                                  .map(l => l.trim)
                                  .filter(l => !l.startsWith("#"))
                                  .toList
    )

  def filter(items:List[GraphItem], patterns: List[Pattern]): List[GraphItem] =
    items.filter(i => isIncluded(i.id, patterns))
           .map(i =>
             GraphItem(i.id, i.name, remove(i.references, patterns), i.itemType)
           )

  def remove(references:List[Reference], excludePatterns: List[Pattern]):List[Reference] =
    references.filter(ref => isIncluded(ref.id, excludePatterns))

  def isIncluded(id: Id, excludePatterns: List[Pattern]):Boolean =
    !matchesAny(id, excludePatterns)


  def matchesAny(id: Id, patterns: List[Pattern]):Boolean = {

    var isMatch = false

    patterns.foreach(p => {
      val localMatch = p.matcher(id.id).matches()
      if (localMatch == true) {
        isMatch = true
      }
    })

    isMatch
  }


}