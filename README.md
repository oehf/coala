# CoALA - Consent Acknowledgement Lookup Assistant

CoALA is a system to create and manage patient contents based on the 
IHE profile BPPC (Basic Patient Privacy Consents). 
The complete communication with third party systems are performed using 
the following IHE transactions using Open eHealth Integration Platform (IPF):

* XDS.b - Registry Stored Query (ITI-18)
* PDQ - Patient Demographics Query (ITI-21)
* XDS.b - Provide and Register Document Set-b (ITI-41)
* XDS.b - Retrieve Document Set (ITI-43)

The JSF 2.0 based user interface provides a login, allows query and display 
of patient data, the display of the patient consent as well as the creation
of new patient consents.

CoALA Continuous Integration: [![Build Status](https://buildhive.cloudbees.com/job/oehf/job/coala/badge/icon)](https://buildhive.cloudbees.com/job/oehf/job/coala/)