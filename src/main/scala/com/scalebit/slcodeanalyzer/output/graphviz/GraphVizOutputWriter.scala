package com.scalebit.slcodeanalyzer.output.graphviz

import java.io.{OutputStream, OutputStreamWriter, PrintWriter}

import com.scalebit.slcodeanalyzer.{GraphItem, Id, OutputWriter}

import scala.collection.mutable.ListBuffer


class GraphVizOutputWriter extends OutputWriter {

  def itemToEdges(item:GraphItem):List[Edge] = {

    item.references.map(r => {
      val color = r.referenceType match {
        case "projectreference" => "azure4"
        case "comreference" => "red"
        case "libraryreference" => "blue"
        case _ => "black"
      }
      Edge(item.id.id, r.id.id, List(Attribute.color(color)))
    })
  }

  def itemToNode(item:GraphItem):Node = {

    val color = item.itemType match {
      case "csproj" => ("gold", "gold4")
      case "vbproj" => ("aquamarine", "aquamarine4")
      case "vbp" => ("darkseagreen1", "darkseagreen4")
      case _ => ("azure", "azure4")
    }

    Node(item.id.id, List(
      Attribute.label(item.name),
      Attribute.fillColor(color._1),
      Attribute.color(color._2)
    ))
  }

  def itemsToGraph(items: List[GraphItem]):Graph = {

    val nodes = scala.collection.mutable.ListBuffer[Node]()

    nodes += Node("node", List(
      Attribute("shape", "box"),
      Attribute("style", "rounded, filled"),
      Attribute.fillColor("antiquewhite"),
      Attribute.color("antiquewhite4"),
      Attribute.fontColor("antiquewhite4")
    ))

    nodes += Node("edge", List(Attribute.color("antiquewhite4")))

    nodes ++= items.map(itemToNode)

    val edges = scala.collection.mutable.ListBuffer[Edge]()

    edges ++= items.flatMap(itemToEdges)

    Graph("G", nodes.toList, edges.toList)
  }

  def renderAttributes(attributes:List[Attribute]):String =
    attributes.map(attr => s"""${attr.key}="${attr.value}"""")
              .mkString("[", ", ", "]")

  def renderNode(node:Node):String =
    s"  ${node.id} ${renderAttributes(node.attributes)};"

  def renderEdge(edge:Edge):String =
    s"  ${edge.sourceId} -> ${edge.targetId} ${renderAttributes(edge.attributes)};"

  def renderGraph(graph:Graph):String = {

    val lines = ListBuffer[String]()
    lines += s"digraph ${graph.name} {"

    lines ++= graph.nodes.map(renderNode)
    lines ++= graph.edges.map(renderEdge)

    lines += "}"

    lines.mkString("\n")
  }

  override def generate(items: List[GraphItem], out: OutputStream): Unit = {

    val printer = new PrintWriter(new OutputStreamWriter(out))

    val graph = itemsToGraph(items)

    printer.print(renderGraph(graph))

    printer.flush()
  }

}
