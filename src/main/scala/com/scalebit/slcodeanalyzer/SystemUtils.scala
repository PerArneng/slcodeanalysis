package com.scalebit.slcodeanalyzer

object SystemUtils {

  def isWindows:Boolean =
    System.getProperty("os.name").toLowerCase().contains("windows")

  def toOSPath(path:String):String =
    if (isWindows)
      path
    else
      path.replace('\\', '/')


  def removeFileExtension(file:String):String =
    if(file.contains("."))
      file.substring(0, file.lastIndexOf('.'))
    else
      file

  def getExtension(file:String):String =
    if(file.contains("."))
      file.substring(file.lastIndexOf('.') + 1)
    else
      ""


  def hasExtension(file:String, extensions:String*):Boolean =
    extensions.contains(getExtension(file))


}
