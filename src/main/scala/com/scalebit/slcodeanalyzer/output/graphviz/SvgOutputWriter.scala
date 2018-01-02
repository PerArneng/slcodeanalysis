package com.scalebit.slcodeanalyzer.output.graphviz

import java.io.OutputStream

import com.scalebit.slcodeanalyzer.{GraphItem, OutputWriter}

import scala.sys.process.ProcessIO

object SvgOutputWriter extends OutputWriter {


  override def generate(items: Seq[GraphItem],
                        out: OutputStream): Unit = {


    //DotOutputWriter.generate(items, )

    //new ProcessIO(inp => {}, out => {})

    printf("not yet implemented!")

  }
}
