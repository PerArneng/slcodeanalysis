package com.scalebit.slcodeanalyzer.parsers

import java.io.InputStream
import java.nio.file.{Path, Paths}

import com.scalebit.slcodeanalyzer._

import scala.io.Source

class VbpParser extends FileParser {

  override def canParse(file:Path):Boolean =
    SystemUtils.hasExtension(file.toString, "vbp")

  def getValue(line:String):String =
    if (line.contains("=")) {
      line.substring(line.indexOf('=') + 1)
    } else {
      line
    }

  def findValues(key:String, lines:List[String]):List[String] =
    lines.filter(line => line.startsWith(key))
         .map(line => getValue(line).trim())

  override def parse(basePath:Path, relPath:Path, contents:InputStream):List[GraphItem] = {

    val lines = Source.fromInputStream(contents, "UTF-8").getLines().toList

    val idString = findValues("ExeName32", lines)
                    .headOption
                    .map(name => name.replaceAll("\"", ""))
                    .map(SystemUtils.getFileNameWithoutExtension)
                    .getOrElse(SystemUtils.getFileNameWithoutExtension(relPath))

    val references = findValues("Reference", lines)
                          .map(value => value.split("#"))
                          .filter(parts => parts.length >= 5)
                          .map(parts => parts(3))
                          .map(file => SystemUtils.toOSPath(file))
                          .map(file => Reference(Id(SystemUtils.getFileNameWithoutExtension(file)),
                                                 "libraryreference",
                                                 Some(file))
                          )

    List(GraphItem(Id(SystemUtils.removeFileExtension(idString)),
                   SystemUtils.removeFileExtension(idString), references, "vbp"))
  }
}
