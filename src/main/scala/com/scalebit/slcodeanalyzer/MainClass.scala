package com.scalebit.slcodeanalyzer

import java.io.{File, FileInputStream}
import java.nio.file.{Path, Paths}

import com.scalebit.slcodeanalyzer.parsers.{MSBuildParser, SLNParser}

case class CmdLinArgs(sourceDir: File = new File("."))


object MainClass {

  def start(args: CmdLinArgs): Unit = {
    printf("starting to analyze: %s\n", args.sourceDir)

    val rootPath = Paths.get(args.sourceDir.toURI).normalize()

    val parsers = List[FileParser](
      new MSBuildParser()
    )

    FileFunctions.recursiveTraverse(rootPath,
      path => {

        val relative = rootPath.relativize(path)

        parsers.foreach(parser => {

          if (parser.canParse(path)) {
            val inp = new FileInputStream(path.toFile)
            val items = parser.parse(rootPath, relative, inp)
            inp.close()
            items.foreach(
              i => {
                printf("item: %s %s\n", i.id, i.name)
                i.dependencies.foreach(d => printf("  %s\n",d))
              }
            )
          }

        })

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
      case Some(parsedArgs) =>
        start(parsedArgs)

      case None =>
        parser.help("x")
        System.exit(-1)
    }

  }



}
