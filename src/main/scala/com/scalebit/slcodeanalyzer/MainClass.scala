package com.scalebit.slcodeanalyzer

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.{Path, Paths}
import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

import com.scalebit.slcodeanalyzer.output.graphviz.GraphVizOutputWriter
import com.scalebit.slcodeanalyzer.parsers.{MSBuildParser, VbpParser}
import com.scalebit.slcodeanalyzer.transformers.{Cleaner, Excluder, Grouper, Rooter}

import scala.collection.mutable

case class CmdLinArgs(sourceDir: File = new File("."),
                      settings: File = null, poolSize:Int = 1,
                      root:String = "", output:Path = Paths.get("-")
                     )


object MainClass {

  def start(args: CmdLinArgs): Unit = {

    val settings = if (args.settings != null) {
      Settings.fromFile(args.settings.toPath)
    } else {
      Settings.default
    }

    val transformers = Seq[Seq[GraphItem] => Seq[GraphItem]](
      Excluder.exclude(settings.excludeIds, _),
      Grouper.createAllGroups(settings.groups, _),
      Rooter.itemsFromRoot(args.root, _),
      Cleaner.clean
    )

    val rootPath = Paths.get(args.sourceDir.toURI).normalize()

    val parsers = List[FileParser](
      new MSBuildParser, new VbpParser
    )

    val pool: ExecutorService = Executors.newFixedThreadPool(args.poolSize)

    val foundItems = mutable.MutableList[GraphItem]()

    FileFunctions.recursiveTraverse(rootPath,
      path => {

        val relative = rootPath.relativize(path)

        parsers.foreach(parser => {

          if (parser.canParse(path)) {

            pool.submit(new Runnable() {
              def run():Unit = {
                val inp = new FileInputStream(path.toFile)
                val items = parser.parse(rootPath, relative, inp)
                inp.close()
                foundItems.synchronized {
                  foundItems ++= items
                }
              }
            })
          }

        })

      }
    )

    pool.shutdown()
    pool.awaitTermination(50, TimeUnit.SECONDS)

    // make sure that one id is represented once
    val itemsToProcess = foundItems.groupBy(i => i.id)
                                   .map(x => x._2.head).toList


    var processedItems:Seq[GraphItem] = itemsToProcess

    for (transformer <- transformers) {
      processedItems = transformer.apply(processedItems)
    }

    val writeToStd = args.output.getFileName.toString.equals("-")

    val outputstream = if (writeToStd)
      System.out
    else
      new FileOutputStream(args.output.toFile)

    val output = new GraphVizOutputWriter()
    output.generate(processedItems, outputstream)

    if (!writeToStd) outputstream.close()

  }

  def main(args: Array[String]):Unit = {

    val parser = new scopt.OptionParser[CmdLinArgs]("slcodeanalyzer") {
      head("slcodeanalyzer", "0.1")

      opt[File]('d', "directory").required().valueName("<directory>").
        action( (x, c) => c.copy(sourceDir = x) ).
        text("directory is the directory that holds" +
             " the files that should be analyzed")

      opt[File]('o', "output").required().valueName("<file>").
        action( (x, c) => c.copy(output = x.toPath) ).
        text("the output file to write to. A single dash '-' means stdout")

      opt[File]('s', "settings").valueName("<file>").
        action( (x, c) => c.copy(settings = x) ).
        text("a file that contains the settings for how to process the graph")

      opt[Int]('p', "thread-pool-size").valueName("<size>").
        action( (x, c) => c.copy(poolSize = x) ).
        text("the size of the thread pool")

      opt[String]('r', "root").valueName("<root name>").
        action( (x, c) => c.copy(root = x) ).
        text("show only this root and all the dependencies recursively")
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
