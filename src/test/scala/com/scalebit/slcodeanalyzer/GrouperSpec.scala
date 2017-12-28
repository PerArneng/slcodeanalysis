package com.scalebit.slcodeanalyzer

import java.util.regex.Pattern

import org.scalatest.FlatSpec


class GrouperSpec extends FlatSpec {

  def createGroup(name:String, patterns:String*):Group =
    Group(name, patterns.map(Pattern.compile).toList)

  "1 item and no groups" should "have an unchanged list of items" in {

    val items = List(GraphItem(Id("test1"), "Test 1", List(), ""))

    val groupedItems = Grouper.createAllGroups(items, List())

    assert(groupedItems.size == 1)
    assert(groupedItems.head.id.id.equals(items.head.id.id))
  }
}
