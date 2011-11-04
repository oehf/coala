/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hhn.mi.coala.lifecycle;

import java.util.ArrayList;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.PreDestroyApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An application event Listener that can be used to prepare or cleanup stuff on
 * certain stages of lifecycle of our Coala-Web-UI.
 */
public class FacesAppListener implements SystemEventListener {

	private static final Log LOG = LogFactory.getLog(FacesAppListener.class);

	/* (non-Javadoc)
	 * @see javax.faces.event.SystemEventListener#processEvent(javax.faces.event.SystemEvent)
	 */
	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {

		/*
		 * STARTUP PHASE - nothing yet
		 */
		if (event instanceof PostConstructApplicationEvent) {
			LOG.info("PostConstructApplicationEvent is called");
		}

		/*
		 * SHUTDOWN PHASE - cleaning up PDQ/XDS ipf routes (mwiesner)
		 */
		
		if (event instanceof PreDestroyApplicationEvent) {
			LOG.info("PreDestroyApplicationEvent is called");
			MBeanServer mbserver = null;

			ArrayList<MBeanServer> mbservers = MBeanServerFactory
					.findMBeanServer(null);

			if (mbservers.size() > 0) {
				mbserver = (MBeanServer) mbservers.get(0);
			}

			if (mbserver != null) {
				LOG.info("Found our MBean server instance...");
			} else {
				mbserver = MBeanServerFactory.createMBeanServer();
			}

			try {
				Set<ObjectInstance> mbeans = mbserver.queryMBeans(null, null);
				for (ObjectInstance mb : mbeans) {
					if (mb.getObjectName().getCanonicalName()
							.contains("camelContext-pdq")) {
						LOG.info("Successfully removed MBean: "
								+ mb.getObjectName().getCanonicalName());
						mbserver.unregisterMBean(mb.getObjectName());
					} else if (mb.getObjectName().getCanonicalName()
							.contains("camelContext-xds")) {
						LOG.info("Successfully removed MBean: "
								+ mb.getObjectName().getCanonicalName());
						mbserver.unregisterMBean(mb.getObjectName());
					}
				}
			} catch (InstanceNotFoundException infe) {
				LOG.warn("Ignoring to unregister pdq/xds camelContext, as it was not found!?");
			} catch (Throwable e) {
				LOG.error(e.getLocalizedMessage(), e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.event.SystemEventListener#isListenerForSource(java.lang.Object)
	 */
	@Override
	public boolean isListenerForSource(Object source) {
		// only for Application
		return (source instanceof Application);

	}

}