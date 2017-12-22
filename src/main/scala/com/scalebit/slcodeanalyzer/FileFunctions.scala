package com.scalebit.slcodeanalyzer

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{FileVisitResult, Files, Path, SimpleFileVisitor}

object FileFunctions {


  def recursiveTraverse(directory:Path, visitor:Path => Unit):Unit = {

    Files.walkFileTree(directory, new SimpleFileVisitor[Path] {

      override def visitFile(file: Path,
                             attrs: BasicFileAttributes): FileVisitResult = {
        super.visitFile(file, attrs)

        visitor(file)

        FileVisitResult.CONTINUE
      }

    })

  }

}
