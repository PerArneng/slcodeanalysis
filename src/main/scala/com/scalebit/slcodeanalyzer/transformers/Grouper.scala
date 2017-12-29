package com.scalebit.slcodeanalyzer.transformers

import java.util.regex.Pattern

import com.scalebit.slcodeanalyzer._

object Grouper {

  def createAllGroups(groups:Seq[Group], items:Seq[GraphItem]):Transformation = {

    var newItems = items

    groups.foreach(group => {
      newItems = createGroup(newItems, group)
    })

    Transformation("grouper", newItems, Seq())
  }

  def updateReferences(item:GraphItem, ids:Seq[Id], newId:Id):GraphItem =
    item.copy(
      references =
        item.references.filter(r => !ids.contains(r.id))
          ::: List(Reference(newId, "group"))
    )

  def createGroup(items:Seq[GraphItem], group:Group):Seq[GraphItem] = {

    val groupId = Id(group.name)

    val groupPatterns = group.patterns.map(Pattern.compile)

    val groupedItems = items.filter(
          i => Excluder.matchesAny(i.id, groupPatterns)
    )

    val groupedIds = groupedItems.map(_.id).distinct
    val groupReferences = groupedItems.flatMap(i => i.references)
                                      .groupBy(r => r.id)
                                      .map(r => r._2.head)
                                      .toList

    val restItems = items.filter(
      i => !Excluder.matchesAny(i.id, groupPatterns)
    ).map(item => updateReferences(item, groupedIds, groupId))


    restItems ++ Seq(GraphItem(groupId, group.name, groupReferences, "group"))
  }

}
