package org.openehealth.coala.mocks.pdq;

import org.apache.camel.spring.SpringRouteBuilder;
import org.openehealth.ipf.platform.camel.ihe.pixpdq.PixPdqCamelValidators;

public class PdqRouteBuilder extends SpringRouteBuilder {

	@Override
	public void configure() throws Exception {
		from("pdq-iti21://localhost:8765")
			.process(PixPdqCamelValidators.iti21RequestValidator()).id("requestValidator")
			.process(PixPdqCamelValidators.iti21ResponseValidator()).id("responseValidator");

	}

}
