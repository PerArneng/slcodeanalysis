package com.scalebit.slcodeanalyzer

import java.io.{File, FileInputStream, FileOutputStream, OutputStream}
import java.nio.file.{Path, Paths}
import java.util.concurrent.{ExecutorService, Executors, TimeUnit}

import com.scalebit.slcodeanalyzer.output.graphviz.{DotOutputWriter, SvgOutputWriter}
import com.scalebit.slcodeanalyzer.parsers.{MSBuildParser, TlbParser, VbpParser}
import com.scalebit.slcodeanalyzer.transformers._
import com.typesafe.scalalogging.Logger

import scala.collection.mutable


object MainClass {

  def start(args:CommandlineArguments, logger:Logger): Unit = {

    val settings = if (args.settings != null) {
      logger.info("reading settings from file")
      Settings.fromFile(args.settings.toPath)
    } else {
      logger.info("using default settings")
      Settings.default
    }

    val outputWriterOpt:Option[(Seq[GraphItem], OutputStream) => Unit] =
      args.format match {
        case "dot" => Some(DotOutputWriter.generate)
        case "svg" => Some(SvgOutputWriter.generate)
        case _ => None
      }

    if (outputWriterOpt.isEmpty) {
      logger.error("output format not supported: '{}'", args.format)
      return
    } else {
      logger.info("using output format: '{}'", args.format)
    }

    val writeOutputFn = outputWriterOpt.get

    logger.info("found {} groups", settings.groups.length)
    logger.info("found {} exclude id patterns", settings.excludeIds.length)

    logger.info("initialising transformers")

    val transformers = Seq[Seq[GraphItem] => Transformation](
      DuplicationRemover.remove,
      DanglingReferenceRemover.remove,
      Grouper.createAllGroups(settings.groups, _),
      Rooter.itemsFromRoot(args.root, _),
      Excluder.exclude(settings.excludeIds, _),
      InvisibleRemover.remove,
      DanglingReferenceRemover.remove
    )

    logger.info("using {} transformers", transformers.length)

    val rootPath = Paths.get(args.sourceDir.toURI).normalize()

    logger.info("analyzing root path '{}'", rootPath.toString)

    val parsers = List[FileParser](
      new MSBuildParser, new VbpParser, new TlbParser
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

    var transformation = Transformation("initial", foundItems, Seq())

    logger.info("starting transformation")

    for (transformer <- transformers) {
      transformation = transformer.apply(transformation.items)
      val transformationLog = Logger(transformation.name)
      transformation.messages.foreach(m => transformationLog.info(m))
      transformationLog.info("finished ending with {} items", transformation.items.length)
    }

    val processedItems = transformation.items

    logger.info("{} graph items after transformation", processedItems.length)

    val writeToStd = args.output.getFileName.toString.equals("-")

    val outputstream = if (writeToStd) {
      logger.info("writing to standard output")
      System.out
    } else {
      logger.info("writing to '{}'", args.output.toString)
      new FileOutputStream(args.output.toFile)
    }


    logger.info("writing the graph")
    writeOutputFn(processedItems, outputstream)

    if (!writeToStd) outputstream.close()

    logger.info("finished!")
  }

  def main(args: Array[String]):Unit = {

    val logger = Logger("Main")
    logger.info("slcodeanalyzer starting")


    val cmdLinArgs = CommandlineArguments.parse(args)

    cmdLinArgs match {
      case Some(parsedArgs) =>
        start(parsedArgs, logger)

      case None =>
        logger.error("failed to parse commandline arguments")
        CommandlineArguments.help()
        System.exit(-1)
    }

  }

}
