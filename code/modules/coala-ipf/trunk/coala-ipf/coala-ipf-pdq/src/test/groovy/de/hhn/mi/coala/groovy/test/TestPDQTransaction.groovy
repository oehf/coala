package de.hhn.mi.coala.groovy.test

import org.apache.camel.CamelContext
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate
import org.apache.camel.impl.DefaultExchange
import org.openehealth.ipf.modules.hl7dsl.MessageAdapter
import org.openehealth.ipf.platform.camel.core.util.Exchanges
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpTestContainer;
import org.springframework.context.support.ClassPathXmlApplicationContext

import org.junit.After
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.Ignore
import static org.junit.Assert.*

class TestPDQTransaction extends MllpTestContainer{
	
	@BeforeClass
	static void setUpClass() {
		init('./iti-21.xml')
	}
   
   @Test
   void testPDQ() {
	   def body = 'MSH|^~\\&|OHFConsumer|OHFFacility|OTHER_KIOSK|HIMSSSANDIEGO|20070108145322-0800||' +
	                'QBP^Q22|9416994431147258002|P|2.5|\nQPD|Q22^Find Candidates|' + 
					   '7891956360974608557281601076319|@PID.5.1^M*|\nRCP|I|2^RD'
					   
	  def retMsg = 'MSH|^~\\&|OTHER_KIOSK|HIMSSSANDIEGO|OHFConsumer|OHFFacility|20110413123045||RSP^K22^RSP_K21|' + 
	                  '205019|P^T|2.5\nMSA|AA|9416994431147258002\nQAK|7891956360974608557281601076319|OK\n' + 
					     'QPD|Q22^Find Candidates|7891956360974608557281601076319|@PID.5.1^M*\nPID|1||' + 
						    '8^^^&2.16.840.1.113883.3.37.4.1.1.2.2.1&ISO^PI~78001^^^PKLN&2.16.840.1.113883.3.37.4.1.1.2.511.1&ISO^PI' +
							  '||Mueller^Hans^^^^^L~Mueller^^^^^^B||19400101|M|||Teststr. 1^^Köln^^57000^DE||022/235715^PRN^PH~h.mueller@gmx.de^PRN^Internet^h.mueller@gmx.de'+ 
							    '|||S||||||||||DE\nPID|2||6^^^&2.16.840.1.113883.3.37.4.1.1.2.2.1&ISO^PI~79471^^^HZLN&2.16.840.1.113883.3.37.4.1.1.2.411.1&IS' +
								  'O^PI~78912^^^PKLN&2.16.840.1.113883.3.37.4.1.1.2.511.1&ISO^PI||Müller^Hans^^^^^L~Müller^^^^^^B|'+
								    '|19400101|M|||Am Domplatz 1^^Köln^^57000^DE||022/235715^PRN^PH~h.mueller@gmx.de^PRN^Internet^h.mueller@gmx.de~022/235716^P' + 
									  'RN^FX~01732356265^PRN^CP|022/874491^WPN^PH||S||GLD-1-1^^^ANGID1&2.16.840.1.113883.3.37.4.1.5.2&ISO^AN|123-11-1234|||||||DE\n'+
									    'DSC|2|I'
	   def endpointUri = 'pdq-iti21://icw-pxs.iap.hs-heilbronn.de:3750'
	   def msg = send(endpointUri, body)
	   assertEquals(retMsg, msg)
	   assertACK(msg)
   }
}
