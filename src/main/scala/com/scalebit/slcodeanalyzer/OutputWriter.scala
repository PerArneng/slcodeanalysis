package com.scalebit.slcodeanalyzer

import java.io.OutputStream

trait OutputWriter {

  def generate(items:List[GraphItem], out: OutputStream)

}
