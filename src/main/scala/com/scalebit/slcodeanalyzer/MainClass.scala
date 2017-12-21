package com.scalebit.slcodeanalyzer

import java.io.File

case class CmdLinArgs(sourceDir: File = new File("."))


object MainClass {

  def start(args: CmdLinArgs): Unit = {
    printf("starting to analyze: %s", args.sourceDir);
  }

  def main(args: Array[String]):Unit = {

    val parser = new scopt.OptionParser[CmdLinArgs]("slcodeanalyzer") {
      head("slcodeanalyzer", "0.1")

      opt[File]('d', "directory").required().valueName("<directory>").
        action( (x, c) => c.copy(sourceDir = x) ).
        text("directory is the directory that holds" +
             "the files that should be analyzed")
    }

    val cmdLinArgs = CmdLinArgs()

    parser.parse(args, cmdLinArgs) match {
      case Some(cmdLinArgs) =>
        start(cmdLinArgs)

      case None =>
        parser.help("x")
        System.exit(-1)
    }

  }



}
