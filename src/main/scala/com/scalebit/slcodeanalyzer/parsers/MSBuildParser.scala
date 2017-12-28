package com.scalebit.slcodeanalyzer.parsers

import java.io.{File, InputStream}
import java.nio.file.{Path, Paths}
import javax.xml.parsers.SAXParserFactory

import com.scalebit.slcodeanalyzer._

import scala.xml.{Elem, Node, XML}

class MSBuildParser extends FileParser {

  override def canParse(file:Path):Boolean =
    SystemUtils.hasExtension(file.toString, "csproj", "vbproj")

  def extractProjectReferenceFile(refElement:Node):Option[String] =
    refElement.attribute("Include")
              .map(i => i.toString)
              .map(SystemUtils.toOSPath)

  override def parse(basePath:Path, relPath:Path, contents:InputStream):List[GraphItem] = {

    val factory = SAXParserFactory.newInstance()
    factory.setNamespaceAware(false)

    val rootElement = XML.load(contents)
    val projectReferences = rootElement \\ "ProjectReference"

    val ids = projectReferences.map(n => n.asInstanceOf[Elem])
                      .map(extractProjectReferenceFile)
                      .filter(n => n.nonEmpty)
                      .map(n => relPath.getParent.resolve(n.get).normalize())
                      .map(p => Utils.fixId(p.toString))
                      .map(idString => Reference(Id(idString), "projectreference"))

    val itemType = SystemUtils.getExtension(relPath.toString).toLowerCase

    List(GraphItem(Id(Utils.fixId(relPath.toString)),
                   SystemUtils.removeFileExtension(relPath.getFileName.toString),
                   ids.toList, itemType))
  }

}
