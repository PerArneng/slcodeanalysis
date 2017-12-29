package com.scalebit.slcodeanalyzer

import java.io.OutputStream

trait OutputWriter {

  def generate(items:Seq[GraphItem], out: OutputStream)

}
