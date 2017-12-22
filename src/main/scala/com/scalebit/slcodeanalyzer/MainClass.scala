package com.scalebit.slcodeanalyzer

import java.io.{File, FileInputStream}
import java.nio.file.{Path, Paths}

import com.scalebit.slcodeanalyzer.parsers.MSBuildParser

case class CmdLinArgs(sourceDir: File = new File("."))


object MainClass {

  def start(args: CmdLinArgs): Unit = {
    printf("starting to analyze: %s", args.sourceDir)

    val rootPath = Paths.get(args.sourceDir.toURI)

    val msBuildParser = new MSBuildParser()

    FileFunctions.recursiveTraverse(rootPath,
      c => {

        val relative = rootPath.relativize(c)
        //printf("%s\n", relative.toString)

        if (relative.toString().endsWith(".csproj")) {
          printf("%s\n", relative.toString)

          val inp = new FileInputStream(c.toFile)
          val items = msBuildParser.parse(relative.toString(), inp)
          items.foreach(
            i => printf("item: %s %s\n", i.id, i.name)
          )
        }

      }
    )


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
