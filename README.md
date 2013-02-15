[fabrician.org](http://fabrician.org/)
==========================================================================
Apache HTTP Server Enabler Guide
==========================================================================

Introduction
--------------------------------------
A Silver Fabric Enabler allows an external application or application platform, such as a J2EE application server to run in a TIBCO Silver Fabric software environment. The Apache HTTP Server Enabler for Silver Fabric provides integration between Silver Fabric and Apache HTTP Server. The Enabler automatically provisions, orchestrates, controls and manages an Apache HTTP environment. 

Installation
--------------------------------------
The Apache HTTP Server Enabler consists of an Enabler Runtime Grid Library and a Distribution 
Grid Library. The Enabler Runtime contains information specific to a Silver Fabric version that is used to integrate the Enabler, and the Distribution contains the application server or program used for the 
Enabler. Installation of the Apache HTTP Server Enabler involves copying these Grid 
Libraries to the SF_HOME/webapps/livecluster/deploy/resources/gridlib directory on the Silver Fabric Broker. 

Runtime Grid Library
--------------------------------------
The Enabler Runtime Grid Library is created by building the maven project.  The build depends on the
SilverFabricSDK jar file that is distributed with TIBCO Silver Fabric.  The SilverFabricSDK.jar file needs to
be referenced in the maven pom.xml or it can be placed in the project root directory.
```bash
mvn package
```

Distribution Grid Library
--------------------------------------
The Distribution Grid Library is created by performing the following steps for each platform:
* Download and extract the Apache HTTP Server source from http://httpd.apache.org/.
* Build the binaries according the Apache HTTP Server instructions.
* Rename the root directory to apache2.
* Create a grid-library.xml file and place it next to the apache2 directory.
* Create a tar.gz or zip of the contents.

```XML
    <grid-library os="linux">
        <grid-library-name>apache-distribution</grid-library-name>
        <grid-library-version>2.2.9</grid-library-version>
    </grid-library>
```
Statistics
--------------------------------------
* **Total Accesses** - Total requests processed since last started. 
* **Total kBytes** - Total amount of data that has been transferred since last started. 
* **Uptime** - Total amount of time web server has been running since last started. 
* **Request Per Second** - Number of HTTP requests per second the web server processed during last poll interval. 
* **Bytes Per Second** - The amount of data the web server is transferring per second during last poll interval. 
* **Bytes Per Request** - The average number of bytes being transferred per HTTP request during last poll interval. 
* **Busy Workers** - The number of Apache threads actively processing HTTP requests. 
* **Idle Workers** - The number of idle Apache threads awaiting HTTP requests. 
* **Idle Workers Percentage** - The percentage of idle Apache threads of all Apache threads. 
* **Busy Workers Percentage** - The percentage of busy Apache threads of all Apache threads.

Runtime Context Variables
--------------------------------------
* **SERVER_RUNTIME_DIR** - Work directory containing server configuration, log, and web site files.  
Note: when you build apache from source and configure with a prefix of say /usr/local/apache2, that 
prefix will be replaced with ${SERVER_RUNTIME_DIR} in the distribution configuration files (see configure.xml).
Therefore Apache ServerRoot will be ${SERVER_RUNTIME_DIR} and DocumentRoot will be ${SERVER_RUNTIME_DIR}/htdocs.
    * Type: Environment
    * Default value: ${ENGINE_WORK_DIR}/fabric/apache2
* **LISTEN_PORT** - Web Server listening port.  
    * Type: String/Inc
    * Default value: 8080 
* **LISTEN_PORT_SSL** - Web Server SSL listening port.  Note: your distribution must be setup for SSL, othewise ignore this.
    * Type: String/Inc
    * Default value: 443
* **SERVER_STATUS_PATH** - URL path to retrieve server statistics.  
    * Type: String
    * Default value: /server-status
* **SERVER_STATUS_QUERY** - Query path to retrieve server statistics.  
    * Type: String
    * Default value: auto
* **FILE_ARCHIVE_DEPLOY_DIRECTORY** - Directory to where uploaded application zip file will be unzipped.  
    * Type: String
    * Default value: ${SERVER_RUNTIME_DIR}/htdocs
* **DELETETARGETDIR** - Delete engine target work directory when deactivating container.  
    * Type: String
    * Default value: true
