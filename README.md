README

BBPI is an easy-to-use API module implementing Blue Button Plus (BB+) specs. It’s designed to be so easy to install and integrate that care providers or EHR vendors can simply drop it in front of EHR to enable health information exchange (HIE) immediately.

To meet Meaningful Use stage 2 and 3 requirements, care providers need to enable their EHR to exchange patient data with patient tools, HIEs, or other EHRs. BB+ specs developed by ONC S&I Framework’s group offers the desired security, functionalities and usability for a variety of data holders. Activ3p believes adoption of BB+ specs really depends on how easy the data holders and EHR vendors can integrate BB+ API with existing EHRs. 

1. Design principles for BBPI:

(1). BBPI must be an end-to-end solution for BB+ API so that care providers can install it in front of EHR and be able to exchange EHR data right away. It has to be that simple.

(2). BBPI has to be a friend of “rich” EHR shop as well as “poor” EHR shop given so many different use cases.  That means, there must be at least two mechanisms for connecting EHR to BB+ API. In rich shop with Java developer, one can create a connector for API to get data directly from EHR or EHR database. Care providers with only EHR database admin available can export data from database periodically and feed it into API.

(3). BBPI must be able to exchange patient data in C-CDA format because it is required by MU 2&3. Because C-CDA is so complicated, BBPI must take care of the complexity to prevent EHR admin from becoming insane when given the 500+ pages C-CDA guide.

2. Brief outlines of API functions:

(1). Dynamic client registration according to OAuth2 specs. 

(2). Patient authorization by OAuth2, including presenting User Consent form for patient to grant access to the patient’s EHR data.

(3). After OAuth2 authorization, provide patient data on demand to applications controlled by patients.  Search results of patient data are returned in json format. Patient record summary document is returned in C-CDA (xml).   

3. System requirements:

(1) Java, Apache Tomcat web server
  
(2) MongoDB
  
(3) BBPI package
  
(4) Any operation system supporting tomcat and mongodb, such as Windows and Linux.


4. Installation instruction:

(1) Install Java 1.6, e.g. in /java/java6. Set JAVA_HOME in your environment. Include <JAVA_HOME>/bin in your path.

(2) Install Apache Tomcat web server (version 6.0), e.g. in /tomcat/tomcat6.

(3) Install MongoDB  database (version 2.4 or later)

Create directory: /data/db

Start mongodb:

  $ mongod 

(4) Install BBPI

Copy bbpi-package.jar to /data directory.

Unpack bbpi package in /data directory:

  $ jar xvf bbpi-package.jar

Edit bin/startup.sh and bin/shutdown.sh: set TOMCAT_HOME to your tomcat directory; set -Dbbpi.conf to your bbpi.cf file path.

(If your BBPI is not installed in /data/bbpi, you also need to correct the log4j.cf file path in conf/bbpi.cf and log file path in conf/log4j.cf.)

Deploy bbpi on tomcat: copy bbpi.war from /data/bbpi/ directory to <tomcat>/webapps/ directory.

Start BBPI:

  $ /data/bbpi/bin/startup.sh

(5) Test BB+ API: Run client test application at http://localhost:8080/bbpi/client. 

Test setup is defined in /data/bbpi/conf/client.cf. If tomcat is running on a different host, you should use the right host:port to run bbpi server and change the host:port in client.cf.  Test users and providers are defined in files under /data/bbpi/res/ directory.




---
Activ3p team


