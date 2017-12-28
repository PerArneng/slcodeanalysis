package com.scalebit.slcodeanalyzer

object Grouper {

  def createAllGroups(items:List[GraphItem], groups:List[Group]):List[GraphItem] = {

    var newItems = items

    groups.foreach(group => {
      newItems = createGroup(newItems, group)
    })

    newItems
  }

  def updateReferences(item:GraphItem, ids:List[Id], newId:Id):GraphItem =
    item.copy(
      references =
        item.references.filter(r => !ids.contains(r.id))
          ::: List(Reference(newId, "group"))
    )
  
  def createGroup(items:List[GraphItem], group:Group):List[GraphItem] = {

    val groupId = Id(Utils.fixId(group.name))

    val groupedItems = items.filter(
          i => GraphItemFilter.matchesAny(i.id, group.patterns)
    )

    val groupedIds = groupedItems.map(_.id).distinct
    val groupReferences = groupedItems.flatMap(i => i.references)
                                      .groupBy(r => r.id)
                                      .map(r => r._2.head)
                                      .toList

    val restItems = items.filter(
      i => !GraphItemFilter.matchesAny(i.id, group.patterns)
    ).map(item => updateReferences(item, groupedIds, groupId))


    restItems ::: List(GraphItem(groupId, group.name, groupReferences, "group"))
  }

}
