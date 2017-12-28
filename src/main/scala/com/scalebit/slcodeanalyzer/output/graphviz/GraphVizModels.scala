package com.scalebit.slcodeanalyzer.output.graphviz

case class Graph(name:String, nodes:List[Node], edges:List[Edge])

case class Node(id:String, attributes:List[Attribute])

case class Edge(sourceId:String, targetId:String, attributes:List[Attribute])

case class Attribute(key: String, value: String)

object Attribute {

  def label(label:String):Attribute =
    Attribute(key = "label", value = label)

  def fillColor(color:String) =
    Attribute(key = "fillcolor", value = color)

  def color(color:String) =
    Attribute(key = "color", value = color)

  def fontColor(color:String) =
    Attribute(key = "fontcolor", value = color)

}

case class NodeColor(fillColor: String,
                     color: String,
                     fontColor: String)

