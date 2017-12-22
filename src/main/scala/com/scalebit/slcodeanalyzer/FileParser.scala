package com.scalebit.slcodeanalyzer

import java.io.InputStream
import java.nio.file.Path

trait FileParser {

  def parse(filePath:Path, contents:InputStream):List[GraphItem]

}
