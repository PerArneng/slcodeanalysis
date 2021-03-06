package com.scalebit.slcodeanalyzer


case class Id(id:String)

case class Reference(id:Id, referenceType:String,
                     description:Option[String] = None)

case class GraphItem(id:Id, name:String,
                     references:List[Reference],
                     itemType:String,
                     visible:Boolean = true,
                     originalFilePath:Option[String] = None)


case class Transformation(name:String,
                          items:Seq[GraphItem],
                          messages:Seq[String])

