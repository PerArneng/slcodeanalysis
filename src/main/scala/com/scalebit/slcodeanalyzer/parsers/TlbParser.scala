package com.scalebit.slcodeanalyzer.parsers

import java.io.InputStream
import java.nio.file.Path

import com.scalebit.slcodeanalyzer.{FileParser, GraphItem, Id, SystemUtils}

class TlbParser extends FileParser {
  override def canParse(path: Path): Boolean =
    SystemUtils.hasExtension(path.toString, "tlb")

  override def parse(basePath: Path, relPath: Path, contents: InputStream): List[GraphItem] = {
    val name = SystemUtils.getFileNameWithoutExtension(relPath)
    List(GraphItem(Id(name), name, List(), "tlb"))
  }
}
