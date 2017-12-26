package com.scalebit.slcodeanalyzer.output

import java.io.{OutputStream, OutputStreamWriter, PrintWriter}

import com.scalebit.slcodeanalyzer.{GraphItem, OutputWriter}

class GraphVizOutputWriter extends OutputWriter {

  def formatItem(item:GraphItem):String = {

    val color = item.itemType match {
      case "csproj" => ("gold", "gold4")
      case "vbproj" => ("aquamarine", "aquamarine4")
      case _ => ("antiquewhite", "antiquewhite4")
    }

    val background = color._1
    val foreground = color._2

    String.format(
      """%s [label = "%s", fillcolor="%s", color="%s", fontcolor="%s"];""".stripMargin,
      item.id, item.name, background, foreground, foreground
    )
  }

  def formatDependency(item:GraphItem, id:String):String = String.format(
    """%s -> %s [label = ""];""".stripMargin, item.id, id)

  override def generate(items: List[GraphItem], out: OutputStream): Unit = {

    val printer = new PrintWriter(new OutputStreamWriter(out))

    def pf(format:String, items:Object*):Unit = printer.printf(format, items:_*)
    def pfn(format:String, items:Object*):Unit = pf(format + "\n", items:_*)

    pfn("digraph G {")
    pfn("   node [shape=\"box\", style=\"rounded, filled\", fillcolor=\"antiquewhite\", color=\"antiquewhite4\", fontcolor=\"antiquewhite4\"];")
    pfn("   edge [color = \"antiquewhite4\"];")

    items.foreach(
      i => {
        pfn("  %s", formatItem(i))
      }
    )

    items.foreach(
      i => {
        i.dependencies.foreach(d => pfn("  %s",formatDependency(i, d)))
      }
    )


    pfn("}")

    printer.flush()
  }

}
