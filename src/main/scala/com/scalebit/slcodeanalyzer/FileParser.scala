package com.scalebit.slcodeanalyzer

import java.io.InputStream

trait FileParser {

  def parse(filePath:String, contents:InputStream):List[GraphItem]

}
