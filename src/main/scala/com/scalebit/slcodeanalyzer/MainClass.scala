package com.scalebit.slcodeanalyzer

import java.io.{File, FileInputStream}
import java.nio.file.{Path, Paths}

import com.scalebit.slcodeanalyzer.output.GraphVizOutputWriter
import com.scalebit.slcodeanalyzer.parsers.{MSBuildParser}

import scala.collection.mutable

case class CmdLinArgs(sourceDir: File = new File("."), exclude: File = null)


object MainClass {

  def start(args: CmdLinArgs): Unit = {

    val rootPath = Paths.get(args.sourceDir.toURI).normalize()

    val parsers = List[FileParser](
      new MSBuildParser()
    )

    val foundItems = mutable.MutableList[GraphItem]()

    FileFunctions.recursiveTraverse(rootPath,
      path => {

        val relative = rootPath.relativize(path)

        parsers.foreach(parser => {

          if (parser.canParse(path)) {
            val inp = new FileInputStream(path.toFile)
            val items = parser.parse(rootPath, relative, inp)
            inp.close()
            foundItems ++= items

          }

        })

      }
    )

    val itemsToProcess = foundItems.toList

    val graphItemFilter = if (args.exclude != null) {
      GraphItemFilter.createFromFile(args.exclude.toPath)
    } else {
      GraphItemFilter.empty
    }

    val filteredItems = graphItemFilter.filter(itemsToProcess)

    val output = new GraphVizOutputWriter()
    output.generate(filteredItems, System.out)

  }

  def main(args: Array[String]):Unit = {

    val parser = new scopt.OptionParser[CmdLinArgs]("slcodeanalyzer") {
      head("slcodeanalyzer", "0.1")

      opt[File]('d', "directory").required().valueName("<directory>").
        action( (x, c) => c.copy(sourceDir = x) ).
        text("directory is the directory that holds" +
             "the files that should be analyzed")

      opt[File]('e', "exclude").valueName("<file>").
        action( (x, c) => c.copy(exclude = x) ).
        text("a file that contains a list of regexp's that matches id's to execlude")
    }

    val cmdLinArgs = CmdLinArgs()

    parser.parse(args, cmdLinArgs) match {
      case Some(parsedArgs) =>
        start(parsedArgs)

      case None =>
        parser.help("x")
        System.exit(-1)
    }

  }



}
