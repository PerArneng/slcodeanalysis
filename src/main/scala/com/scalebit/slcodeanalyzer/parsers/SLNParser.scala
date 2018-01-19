package com.scalebit.slcodeanalyzer.parsers

import java.io.InputStream
import java.nio.file.{Path, Paths}

import com.scalebit.slcodeanalyzer._

import scala.io.Source

class SLNParser extends FileParser {


  override def canParse(file:Path):Boolean =
    SystemUtils.hasExtension(file.toString, "sln")

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
           ProjectRef(trim(parts(0)),
                      Paths.get(SystemUtils.toOSPath(trim(parts(1)))).normalize(),
                      trim(parts(2)))
        ).head
  }

  def toReference(relPath:Path, projectRef: ProjectRef):Reference = {
    Reference(Id(relPath.resolve(projectRef.file).toString), "slnreference")
  }

  override def parse(basePath:Path, relPath:Path, contents:InputStream):List[GraphItem] = {

    def isProjectLine(str:String):Boolean = str.trim().startsWith("Project(")

    val references = Source.fromInputStream(contents, "UTF-8").getLines()
                              .filter(isProjectLine)
                              .map(parseProjectRef)
                              .map(r => toReference(relPath.getParent, r))
                              .toList

    List(GraphItem(Id(relPath.toString),
          SystemUtils.getFileNameWithoutExtension(relPath),
          references, "sln",  true, Some(relPath.toString)))
  }

}
