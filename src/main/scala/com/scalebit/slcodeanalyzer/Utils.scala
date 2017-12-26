package com.scalebit.slcodeanalyzer

object Utils {

  def fixId(id: String): String = id.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase

}
