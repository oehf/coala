package org.openehealth.tutorial

import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.Exchange
class SampleModelExtension {

     static extensions = {
         
         ProcessorDefinition.metaClass.reverse = {
             delegate.transmogrify { it.reverse() }
         }
         ProcessorDefinition.metaClass.setFileHeaderFrom = { String sourceHeader ->
         delegate.setHeader(Exchange.FILE_NAME) { exchange ->
             def destination = exchange.in.headers."$sourceHeader"
             destination ? "${destination}.txt" : 'default.txt'
         }
         }
     }
     
}
