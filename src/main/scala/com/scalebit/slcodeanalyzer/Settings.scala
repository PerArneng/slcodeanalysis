package com.scalebit.slcodeanalyzer

import java.nio.file.Path
import java.util.regex.Pattern
import javax.xml.parsers.SAXParserFactory

import scala.xml.{Node, XML}

case class Group(name:String, patterns:List[Pattern])

case class Settings(execludIds:List[Pattern], groups:List[Group])

object Settings {

  def default:Settings = Settings(List(), List())

  def extractPatterns(node:Node):List[Pattern] =
      node.text.split("\n")
                .map(line => line.trim)
                .filter(line => line.length > 0)
                .map(Pattern.compile)
                .toList

  def createGroup(node:Node):Group = {
    val name = node \@ "name"
    val patterns = extractPatterns(node)
    Group(name, patterns)
  }

  def fromFile(file:Path):Settings = {

    val factory = SAXParserFactory.newInstance()
    factory.setNamespaceAware(false)

    val rootElement = XML.loadFile(file.toFile)
    val excludeIdPatterns = (rootElement \ "excludeIdPatterns")
                                .flatMap(extractPatterns)
                                .toList

    val groups = (rootElement \ "group")
                      .map(createGroup)
                      .toList

    Settings(excludeIdPatterns, groups)
  }

}