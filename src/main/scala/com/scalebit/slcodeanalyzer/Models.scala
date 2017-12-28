package com.scalebit.slcodeanalyzer


case class Id(id:String)

case class Reference(id:Id, referenceType:String)

case class GraphItem(id:Id, name:String,
                     references:List[Reference], itemType:String);
