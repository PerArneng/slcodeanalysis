package com.scalebit.slcodeanalyzer

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.{Path, Paths}
import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

import com.scalebit.slcodeanalyzer.output.graphviz.GraphVizOutputWriter
import com.scalebit.slcodeanalyzer.parsers.{MSBuildParser, VbpParser}
import com.scalebit.slcodeanalyzer.transformers.{Cleaner, Excluder, Grouper, Rooter}
import com.typesafe.scalalogging.Logger

import scala.collection.mutable

case class CmdLinArgs(sourceDir: File = new File("."),
                      settings: File = null, poolSize:Int = 1,
                      root:String = "", output:Path = Paths.get("-")
                     )


object MainClass {

  def start(args: CmdLinArgs, logger:Logger): Unit = {

    val settings = if (args.settings != null) {
      logger.info("reading settings from file")
      Settings.fromFile(args.settings.toPath)
    } else {
      logger.info("using default settings")
      Settings.default
    }

    logger.info("found {} groups", settings.groups.length)
    logger.info("found {} exclude id patterns", settings.excludeIds.length)

    logger.info("initialising transformers")

    val transformers = Seq[Seq[GraphItem] => Seq[GraphItem]](
      Excluder.exclude(settings.excludeIds, _),
      Grouper.createAllGroups(settings.groups, _),
      Rooter.itemsFromRoot(args.root, _),
      Cleaner.clean
    )

    logger.info("using {} transformers", transformers.length)

    val rootPath = Paths.get(args.sourceDir.toURI).normalize()

    logger.info("analyzing root path '{}'", rootPath.toString)

    val parsers = List[FileParser](
      new MSBuildParser, new VbpParser
    )

    logger.info("using {} parsers", parsers.length)

    logger.info("starting the thread pool with {} worker(s)", args.poolSize)

    val pool: ExecutorService = Executors.newFixedThreadPool(args.poolSize)

    val foundItems = mutable.MutableList[GraphItem]()

    logger.info("traversing the file tree")

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

    logger.info("shutting down the thread pool")

    pool.shutdown()
    pool.awaitTermination(50, TimeUnit.SECONDS)

    logger.info("found {} graph items", foundItems.length)


    // make sure that one id is represented once
    val itemsToProcess = foundItems.groupBy(i => i.id)
                                   .map(x => x._2.head).toList

    logger.info("{} unique graph items", itemsToProcess.length)

    var processedItems:Seq[GraphItem] = itemsToProcess

    logger.info("starting transformation")

    for (transformer <- transformers) {
      processedItems = transformer.apply(processedItems)
    }

    logger.info("{} graph items after transformation", processedItems.length)

    val writeToStd = args.output.getFileName.toString.equals("-")

    val outputstream = if (writeToStd) {
      logger.info("writing to standard output")
      System.out
    } else {
      logger.info("writing to '{}'", args.output.toString)
      new FileOutputStream(args.output.toFile)
    }

    val output = new GraphVizOutputWriter()

    logger.info("writing the graph")
    output.generate(processedItems, outputstream)

    if (!writeToStd) outputstream.close()

    logger.info("finished!")
  }

  def main(args: Array[String]):Unit = {

    val logger = Logger("Main")
    logger.info("slcodeanalyzer starting")

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
        start(parsedArgs, logger)

      case None =>
        logger.error("failed to parse commandline arguments")
        parser.help("x")
        System.exit(-1)
    }

  }



}
