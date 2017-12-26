package com.scalebit.slcodeanalyzer

import java.io.InputStream
import java.nio.file.Path

trait FileParser {

  def canParse(path:Path):Boolean

  def parse(basePath:Path, relPath:Path, contents:InputStream):List[GraphItem]

}
