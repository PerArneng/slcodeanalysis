package com.scalebit.slcodeanalyzer

import java.nio.file.{Path, Paths}

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

  def getFileNameWithoutExtension(path:String):String =
    getFileNameWithoutExtension(Paths.get(path))

  def getFileNameWithoutExtension(path:Path):String =
    Option.apply(path)
          .map(_.getFileName)
          .map(fileName => removeFileExtension(fileName.toString))
          .getOrElse(path.getFileName.toString)

  def hasExtension(file:String, extensions:String*):Boolean =
    extensions.contains(getExtension(file))


}
