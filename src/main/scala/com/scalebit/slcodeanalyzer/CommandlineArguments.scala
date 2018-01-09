package com.scalebit.slcodeanalyzer

import java.io.File
import java.nio.file.{Path, Paths}


case class CommandlineArguments(
             sourceDir: File = new File("."),
             settings: File = null, poolSize: Int = 1,
             root: String = "", output: Path = Paths.get("-"),
             format: String = "dot")

object CommandlineArguments {

  private val parser = new scopt.OptionParser[CommandlineArguments]("slcodeanalyzer") {
    head("slcodeanalyzer", "0.1")

    opt[File]('d', "directory").required().valueName("<directory>").
      action((x, c) => c.copy(sourceDir = x)).
      text("directory is the directory that holds" +
        " the files that should be analyzed")

    opt[File]('o', "output").required().valueName("<file>").
      action((x, c) => c.copy(output = x.toPath)).
      text("the output file to write to. A single dash '-' means stdout")

    opt[File]('s', "settings").valueName("<file>").
      action((x, c) => c.copy(settings = x)).
      text("a file that contains the settings for how to process the graph")

    opt[Int]('p', "thread-pool-size").valueName("<size>").
      action((x, c) => c.copy(poolSize = x)).
      text("the size of the thread pool")

    opt[String]('r', "root").valueName("<root name>").
      action((x, c) => c.copy(root = x)).
      text("show only this root and all the dependencies recursively")

    opt[String]('f', "format").valueName("<output format>").
      action((x, c) => c.copy(format = x)).
      text("the output format of the diagram. 'dot' is default. Only 'dot' is supported right now")
  }

  def help(): Unit = {
    parser.help("slcodeanalyzer")
  }

  def parse(args: Array[String]): Option[CommandlineArguments] = {

    val cmdLinArgs = CommandlineArguments()

    parser.parse(args, cmdLinArgs) match {
      case Some(parsedArgs) => Some(parsedArgs)
      case None => None
    }

  }

}