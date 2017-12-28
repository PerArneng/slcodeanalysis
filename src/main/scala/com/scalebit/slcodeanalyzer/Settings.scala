package com.scalebit.slcodeanalyzer

import java.nio.file.Path
import java.util.regex.Pattern
import javax.xml.parsers.SAXParserFactory

import scala.xml.XML

case class Settings(execludIds:List[Pattern])

object Settings {

  def default:Settings = Settings(List())

  def fromFile(file:Path):Settings = {

    val factory = SAXParserFactory.newInstance()
    factory.setNamespaceAware(false)

    val rootElement = XML.loadFile(file.toFile)
    val excludeIdPatterns = (rootElement \ "excludeIdPatterns")
                                .map(node => node.text)
                                .flatMap(contents => contents.split("\n"))
                                .map(line => line.trim)
                                .filter(line => line.length > 0)
                                .map(line => Pattern.compile(line))
                                .toList

    Settings(excludeIdPatterns)
  }

}