package com.scalebit.slcodeanalyzer

import java.util.regex.Pattern

import com.scalebit.slcodeanalyzer.transformers.Grouper
import org.scalatest.FlatSpec


class GrouperSpec extends FlatSpec {

  def createGroup(name:String, patterns:String*):Group =
    Group(name, patterns)

  "1 item and no groups" should "have an unchanged list of items" in {

    val items = Seq(GraphItem(Id("test1"), "Test 1", List(), ""))

    val groupedItems = Grouper.createAllGroups(Seq(), items)

    assert(groupedItems.size == 1)
    assert(groupedItems.head.id.id.equals(items.head.id.id))
  }

  "2 item and 1 groups" should "have 2 items returned and proper dependencies" in {

    val items = List(
      GraphItem(Id("test1"), "Test 1", List(
        Reference(Id("test2"), "")
      ), ""),
      GraphItem(Id("test2"), "Test 2", List(), "")
    )

    val group = createGroup("TestGroup", "test2")

    val groupedItems = Grouper.createAllGroups(Seq(group), items)

    assert(groupedItems.length == 2)
    val test1 = groupedItems.filter(i => i.id.id.equals("test1")).head
    val testGroup = groupedItems.filter(i => i.id.id.equals("testgroup")).head



  }

  "3 item and 1 groups" should "have 3 items returned and proper dependencies" in {

    val items = List(
      GraphItem(Id("test1"), "Test 1", List(
        Reference(Id("groupee1"), ""),
        Reference(Id("groupee2"), "")
      ), ""),
      GraphItem(Id("test3"), "Test 3", List(
        Reference(Id("groupee3"), ""),
        Reference(Id("test1"), "")
      ), ""),
      GraphItem(Id("groupee1"), "Groupee 1", List(
        Reference(Id("test1"), "")
      ), ""),
      GraphItem(Id("groupee2"), "Groupee 2", List(Reference(Id("test1"), "")), ""),
      GraphItem(Id("groupee3"), "Groupee 3", List(Reference(Id("test1"), "")), "")
    )

    val group = createGroup("TestGroup", "groupee.*")

    val groupedItems = Grouper.createAllGroups(Seq(group), items)

    assert(groupedItems.length == 3)
    val test1 = groupedItems.filter(i => i.id.id.equals("test1")).head
    val testGroup = groupedItems.filter(i => i.id.id.equals("testgroup")).head
    assert(testGroup.references.size == 1)



  }
}
