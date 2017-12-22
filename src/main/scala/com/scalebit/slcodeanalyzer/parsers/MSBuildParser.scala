package com.scalebit.slcodeanalyzer.parsers

import java.io.InputStream
import java.nio.file.Path
import javax.xml.parsers.SAXParserFactory

import com.scalebit.slcodeanalyzer.{FileParser, GraphItem}

import scala.xml.XML

class MSBuildParser extends FileParser {


  def parse(filePath:Path, contents:InputStream):List[GraphItem] = {


    val factory = SAXParserFactory.newInstance()
    factory.setNamespaceAware(false)

    val rootElement = XML.load(contents);
    val projectReferences = rootElement \\ "ProjectReference"
    printf("%s\n", projectReferences.size)
    projectReferences.foreach(n =>
      printf("%s\n", n.toString())
    )

    List(new GraphItem(filePath.toString(), "x", List()))
  }

}
