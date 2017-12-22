package com.scalebit.slcodeanalyzer.parsers

import java.io.InputStream

import com.scalebit.slcodeanalyzer.{FileParser, GraphItem}

class MSBuildParser extends FileParser {

  def parse(filePath:String, contents:InputStream):List[GraphItem] = {


    List(new GraphItem(filePath, "x", List()))
  }

}
