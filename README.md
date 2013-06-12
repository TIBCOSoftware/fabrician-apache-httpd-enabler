[fabrician.org](http://fabrician.org/)
==========================================================================
Apache HTTP Server Enabler Guide
==========================================================================

Introduction
--------------------------------------
A Silver Fabric Enabler allows an external application or application platform, such as a J2EE application server to run in a TIBCO Silver Fabric software environment. The Apache HTTP Server Enabler for Silver Fabric provides integration between Silver Fabric and Apache HTTP Server. The Enabler automatically provisions, orchestrates, controls and manages an Apache HTTP environment. 

Supported Platforms
--------------------------------------
* Silver Fabric 5
* Linux

Installation
--------------------------------------
The Apache HTTP Server Enabler consists of an Enabler Runtime Grid Library and a Distribution 
Grid Library. The Enabler Runtime contains information specific to a Silver Fabric version that is used to integrate the Enabler, and the Distribution contains the application server or program used for the 
Enabler. Installation of the Apache HTTP Server Enabler involves copying these Grid 
Libraries to the SF_HOME/webapps/livecluster/deploy/resources/gridlib directory on the Silver Fabric Broker. 


Distribution Grid Library
--------------------------------------
The Distribution library must be built from source on the platform on which you will run the server.
Because there are many ways that Apache can be built, please see the following for a step-by-step guide on how to build and deploy:

[Creating a 64-bit Apache-2.4 Linux Distribution](https://github.com/fabrician/apache-httpd-enabler/wiki/Creating-a-64-bit-Apache-2.4-Linux-Distribution-on-Ubuntu)

The fundamental steps, as covered in more detail in the link above, are:
* Obtain the Apache HTTP Server source from http://httpd.apache.org/.
* Obtain the APR (Apache Portable Runtime) and APR-utils source from http://apr.apache.org/
* Build the binaries according the Apache HTTP Server instructions, with local APR. At this point you need to decide what modules to have available. For more information, see http://httpd.apache.org/docs/2.4/install.html
* In apache2/conf/httpd.conf, add the directive **Include conf/fabric/*.conf**. This allows a component to add new configurations to the enabler.
* Enable httpd-info by copying **conf/extra/httpd-info.conf** into the **conf/fabric** directory. This is required by the enabler to function.
* Create a grid-library.xml file and place it next to the apache2 directory.
* Create an archive library called apache-distribution-{version}.tar.gz, with the **apache2** directory and **grid-library.xml** in the root of the archive.

```XML
    <grid-library os="linux">
        <grid-library-name>apache-distribution</grid-library-name>
        <grid-library-version>{version}</grid-library-version>
    </grid-library>
```

**Notes** 
* **Make sure the 'os' attribute is appropriate for you platform (linux or linux64)**
* **Make sure the 'grid-library-version' element is set to the version of the server you just built**
* **If you do not build into the default /usr/local/apache2 directory, you must update your configure.xml file with your install directory.**

Runtime Grid Library
--------------------------------------
The Enabler Runtime Grid Library is created by building the maven project.  The build depends on the
SilverFabricSDK jar file that is distributed with TIBCO Silver Fabric.  The SilverFabricSDK.jar file needs to
be referenced in the maven pom.xml or it can be placed in the project root directory.

* Edit the pom.xml file, and update the version of the distribution to the one you just built.
* Build the library with maven:

```bash
mvn package
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
