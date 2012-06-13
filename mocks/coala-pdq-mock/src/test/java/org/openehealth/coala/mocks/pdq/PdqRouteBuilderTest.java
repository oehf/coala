package org.openehealth.coala.mocks.pdq;

import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

public class PdqRouteBuilderTest extends AbstractJUnit4SpringContextTests {

	@Produce(uri ="pdq-iti21://localhost:8765")
	private ProducerTemplate template;
	
	@Autowired
	private CamelContext camelContext;
	
	@Before
	public void setup() throws Exception {
		camelContext.getRouteDefinitions().get(0).adviceWith(camelContext, new RouteBuilder() {
	        @Override
	        public void configure() throws Exception {
	            // intercept sending to mock:foo and do something else
	            interceptFrom(uri)
	                    .skipSendToOriginalEndpoint()
	                    .to("log:foo")
	                    .to("mock:advised");
	        }
	    });
	}
}
