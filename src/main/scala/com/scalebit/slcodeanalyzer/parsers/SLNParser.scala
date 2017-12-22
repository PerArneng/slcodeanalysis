package com.scalebit.slcodeanalyzer.parsers

import java.io.InputStream
import java.nio.file.{Path, Paths}

import com.scalebit.slcodeanalyzer.{FileParser, GraphItem}

import scala.io.Source

class SLNParser extends FileParser {

  case class ProjectRef(name: String, file:Path, uid:String)

  def trim(str:String):String = str.replace('"', ' ').trim()

  def parseProjectRef(projectRef:String):ProjectRef = {
    List[String](projectRef)
        .filter(ref => ref.contains(" = "))
        .map(ref => ref.split("="))
        .filter(parts => parts.length > 1)
        .map(parts => parts(1))
        .map(part => part.split(","))
        .map(parts =>
          new ProjectRef(trim(parts(0)),
                         Paths.get(trim(parts(1))),
                         trim(parts(2)))
        ).head
  }

  def parse(filePath:Path, contents:InputStream):List[GraphItem] = {

    def isProjectLine(str:String):Boolean = str.trim().startsWith("Project(")

    Source.fromInputStream(contents, "UTF-8").getLines()
        .filter(isProjectLine)
        .map(parseProjectRef)
        .foreach(i => printf("  %s\n", i))


    List()
  }

}
