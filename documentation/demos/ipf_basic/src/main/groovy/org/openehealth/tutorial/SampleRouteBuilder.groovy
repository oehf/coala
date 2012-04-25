package org.openehealth.tutorial

import org.apache.camel.spring.SpringRouteBuilder

class SampleRouteBuilder extends SpringRouteBuilder {
	void configure() {

        from('jetty:http://0.0.0.0:8080/tutorial')  // receive client requests on http://0.0.0.0:8080/tutorial
            .convertBodyTo(String.class)            // convert request input stream into a string
            .to('direct:input1')                    // continue from direct:input1

        from('direct:input1')
            .transmogrify { it * 2 }                // duplicate the request string
            .setFileHeaderFrom('destination')       // set name of result file to be written (a custom DSL extension)
            .to('file:target/output')               // replace content of file in target/output directory with body of in-message.

        from('direct:input2').reverse()
    }   
}
