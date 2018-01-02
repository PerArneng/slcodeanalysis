package com.scalebit.slcodeanalyzer

import java.nio.file.Path
import java.util.regex.Pattern
import javax.xml.parsers.SAXParserFactory

import scala.xml.{Node, XML}

case class Group(name:String, patterns:Seq[String])

case class Settings(excludeIds:Seq[String], groups:Seq[Group])

object Settings {

  def default:Settings = Settings(Seq(), Seq())

  def extractPatterns(node:Node):Seq[String] =
      node.text.split("\n")
                .map(line => line.trim)
                .filter(line => line.length > 0)

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
                                .map(_.trim)
                                .filter(!_.startsWith("//"))

    val groups = (rootElement \ "group")
                      .map(createGroup)

    Settings(excludeIdPatterns, groups)
  }

}