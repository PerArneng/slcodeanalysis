package com.scalebit.slcodeanalyzer

object Grouper {

  def createAllGroups(items:List[GraphItem], groups:List[Group]):List[GraphItem] = {

    var newItems = items

    groups.foreach(group => {
      newItems = createGroup(newItems, group)
    })

    newItems
  }

  def createGroup(items:List[GraphItem], group:Group):List[GraphItem] = {

    List()
  }

}
