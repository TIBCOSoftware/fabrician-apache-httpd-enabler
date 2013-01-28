Apache Web Server Container Guide

Chapter 1 - Introduction
	A FabricServer Container allows an external application or application 
platform, such as a HTTP Web Server, to run in a FabricServer software 
environment. The Apache Web Server Container for FabricServer provides 
integration between FabricServer and the Apache Web Server. The Container 
automatically provisions, orchestrates, controls and manages an Apache Web 
Server environment. Apache Web Server instances are distributed and run on 
FabricServer Engines. The management of the Apache Web Server environment 
through FabricServer is dynamic, centralized, and largely automated. This 
centralized management adapts the structure of the Apache Web Server 
environment, and manages the life cycle process state of server instances, 
as specified in FabricServer by Policies that are directed by business 
objectives associated with the target applications.

Architecture
	The FabricServer Engine manages an Apache Web Server instances. The 
FabricServer Broker and the FabricServer Engine instance collaborate to perform 
the following actions:
* Automatically provision all Apache Web Server-related software to machines 
with FabricServer Engines. This software is encapsulated within archive 
libraries (ZIP files) and distributed to computers running FabricServer Engines.
* Automatically start and stop Apache Web Server on a node, as specified by 
Policies specified within FabricServer.
* Monitor the health of the Apache Web Server and retrieve statistics.

Before Beginning
	This guide provides the instructions for installing and configuring the 
Apache Web Server Container for FabricServer. This guide assumes a FabricServer 
Broker is running with at least one Engine installed, and that you have the 
Broker’s hostname, a username, and password for the FabricServer Administration 
Tool. If this isn’t true, see the FabricServer Installation Guide, or contact 
your administrator.

	This guide presumes a strong familiarity with your particular version of 
Apache Web Server. While the Apache Web Server Container for FabricServer 
is tested with Apache Web Server version 2.2.9 on Linux, the instructions 
provided for accomplishing tasks are not limited to this version. Chapter 5
provides information on how to extend the container to support other versions of 
Apache Web Server. If you are uncertain of how to achieve a particular task, 
consult your version-specific Apache Web Server documentation.

Glossary of Basic FabricServer Terms
	Below is a list of basic FabricServer terms that are used frequently 
throughout the guide. 
Container - A wrapper around an external application or application platform, 
such as a HTTP Web Server.
Distribution - The Distribution contains the application server or program used 
for the Container.
Grid - The collection of all DataSynapse Engines, Brokers, and components 
running on your network used to virtualize applications.
Application Component - One of the executable pieces (eg utility service or 
HTTP Server Server) that makes up an Application.
Application - A bundle of Application Components that is activated as a unit, 
plus one or more policies specifying the resource needs, priorities 
and constraints of those Components.
Engine - The process that provisions and runs an Application Component instance.
Broker - The component that provides policy-driven resource allocation and 
monitoring.


Chapter 2 - Installation
	The Apache Web Server Container consists of a Container Runtime Grid 
Library, a Distribution Grid Library. The Container Runtime contains information
specific to a FabricServer version that is used to integrate the Container, and
the Distribution contains the application server or program used for the 
Container. Installation of the Apache Web Server Container involves copying 
these Grid Libraries to the deploy/resources/gridlib directory on the 
FabricServer Broker.

	The required Grid Libraries for Apache Web Server container are listed 
below.
apache-container-gridlib.zip (Linux only)
apache-distribution-2_2_9-gridlib.zip (This is build by user and not necessarily
to be version 2.2.9. See Chapter 5 on building other version of Apache Web 
Server)

Installing the Apache Web Server Container
1. Copy the desired Apache Web Server Container version Grid Library files to 
the [FS Broker Dir]/webapps/livecluster/deploy/resources/gridlib directory in 
the following order:
* Distribution
* Container Runtime

IMPORTANT: 
* Copying the Grid Libraries files to this directory also extracts them to the 
deployed directory: [FS Broker Dir]/webapps/livecluster/deploy/expanded/. This 
overwrites any changes to the existing Grid Library in the staging directory.
* You cannot upgrade or remove GridLibraries while the respective Component and
Container active.

2. Verify successful installation by selecting Applications > Containers in the
FabricServer Administration tool and ensuring that the container appears in the
list.


Chapter 3 - Configuration and Running
This chapter describes how to set up FabricServer to run Apache Web Server.

Before Beginning
	These instructions presume a strong familiarity with your particular version
of Apache Web Server. If you are uncertain of how to achieve a particular task, 
consult your Apache Web Server documentation.

Running Apache Web Server in Standalone Mode
	The following section describes how to run an Apache Web Server application 
component in a standalone mode. In this mode, FabricServer Engines run 
individual self-contained servers that do not communicate with each other. On 
activation, the Engine unzips archives contained in the Component to the server.

To define the Component in FabricServer, do the following:
1. Enter your FabricServer host and port in your browser and log in to the 
FabricServer Administration Tool.
2. Select Applications > Application Components.
3. Select Create New Apache Web Server Application Component from the Global 
Actions list.
4. Enter a Component name in the Name field.
5. Select the Apache Container and the desired Container Version from the lists.
6. Click Next. The Configure Component Features screen appears.
7. HTTP Support, File Archive Support, and Application Logging Support are added
to the feature list by default.
* You can further customize Component features as needed by selecting the 
feature and clicking Edit. When finished, click Next. The Configure Component 
Options screen appears.
8. Enter any desired options and click Next. The Add/Edit Archive Files screen 
appears.
9. Click Add and upload the archives that you want to deploy and run on the 
Apache Web Server. The archives are the zipped up files of the web content you 
want to publish on the server.
10. Click Next. The Add/Edit Relative URLs page appears. VirtualRouter uses 
these URLs to map incoming requests to a Component, and hence to a particular 
Engine running that Component. For example, /index.html if that’s the starting 
page of the web site.
11. Click Next. The Add/Remove Log File Patterns page appears. Add default or
additional log file patterns so that when engine shuts down the Web Server, it 
keeps a copy of the files matching the specified pattern.
12. Configure the options on the remaining screens as needed. Additional options
are also configurable through context variables.
13. Click Finish.
14. Select Applications > Application Components in the FabricServer
Administration Tool.
15. Select Deploy Application Component from the corresponding Actions list of
the Component you created.
16. Create an Application and add the Component to the Application as described
in the FabricServer User Guide. Start the Application when desired.
* If you selected all the default values and even if you did not include an 
application archive, the Apache Web Server should still run with default 
configuration settings in the supplied httpd.conf. An index.html page is also 
supplied to verify if the server is running. To access this page, use 
http://engine-host:listen_port/index.html, where listen_port is 8080 by default.


Chapter 4 - Statistics and Reporting
Statistics
	The following are the default statistics supported by the Apache Web Server 
Container. The Container uses Apache Web Server’s server query URL to retrieve
statistic values once it is running on a FabricServer engine. You can select and
track these statistics from the Component Wizard. Tracked statistics are 
available for report output. You can also create Policy rules based on any 
tracked statistic.

Apache Web Server Statistics
Total Accesses - Total requests processed since last started. 
Total kBytes - Total amount of data that has been transferred since last started. 
Uptime - Total amount of time web server has been running since last started. 
Request Per Second - Number of HTTP requests per second the web server processed
during last poll interval. 
Bytes Per Second - The amount of data the web server is transferring per second 
during last poll interval. 
Bytes Per Request - The average number of bytes being transferred per HTTP 
request during last poll interval. 
Busy Workers - The number of Apache threads actively processing HTTP requests. 
Idle Workers - The number of idle Apache threads awaiting HTTP requests. 
Idle Workers Percentage - The percentage of idle Apache threads of all Apache 
threads. 
Busy Workers Percentage - The percentage of busy Apache threads of all Apache 
threads.

Runtime-Context Variables
SERVER_RUNTIME_DIR 
Default value: ${ENGINE_WORK_DIR}/ap
Type: Environment
Description: Work directory containing server configuration, log, and web site 
files

LISTEN_PORT 
Default value: 8080 
Type: String/Inc
Description: Web Server listening port

LISTEN_PORT_SSL
Default value: 443
Type: String/Inc
Description: Web Server SSL listening port 

SERVER_STATUS_PROTOCAL
Default value: http
Type: String
Description: Protocal used to retrieve server statistics

SERVER_STATUS_PATH
Default value: /server-status
Type: String URL path to retrieve server statistics

SERVER_STATUS_QUERY 
Default value: auto
Type: String
Description: Query path to retrieve server statistics

DOCUMENT_ROOT
Default value: ${SERVER_RUNTIME_DIR}/htdocs
Type: String
Description: Web Server document root

FILE_ARCHIVE_DEPLOY_DIRECTORY
Default value: ${DOCUMENT_ROOT}
Type: String
Description: Directory to where uploaded application zip file will be unzipped

DELETETARGETDIR
Default value: true
Type: String
Description: Delete engine target work directory when deactivating container


Chapter 5 - Extending Apache Web Server Container
	The pre-build Apache Web Server Container is tested against a minimally 
build Apache Web Server 2.2.9 on Linux. However, you can extend the container to
run other versions of Apache Web Server with additional module support. The 
following describes the steps to build and run an Apache Web Server with PHP
module support.

Step 1
	Build a copy of Apache Web Server with desired version and module support. 
For example, you would like to build a version of the server with mod_php 
support so you can run PHP enabled web sites such as Mediawiki or Wordpress. 

Build your own baseline Apache distribution (Linux)
1. Download/unpack Apache2 source from Apache web site.
2. In Apache 2 source directory, create makefile by:
./configure --prefix=/opt/apache2 --enable-so --enable-cgi --enable-info 
--enable-rewrite --enable-speling --enable-usertrack --enable-deflate 
--enable-ssl --enable-mime-magic
3. Make Apache from the makefile by:
make
4. Install Apache by:
make install
5. Apache is now installed to the directory specified. 

Build mod_php
1. Download/unpack PHP source form PHP web site.
2. In PHP source directory, create makefile by:
./configure --with-apxs2=/opt/apache2/bin/apxs --with-mysql --prefix=/opt/php 
--with-config-file-path=/tmp/apache_php/php/config --enable-force-cgi-redirect 
--disable-cgi --with-zlib --with-gettext --with-gdbm
3. Make PHP from the makefile by:
make
4. Install PHP module to previous installed Apache by:
make install

You now have a version of Apache Web Server that supports mod_php. 

Step 2
	Make an Apache Web Server distribution based on the build. You can use 
FabricServer Studio for this step or you can do it manually using any of your 
favorite zip utility. FabricServer Studio is a Eclipse based program to create
DataSynapse Grid Libraries. To create the Grid Library manually, tar and zip up 
the build Apache directory (.zip or .gz) so it has .\apache2 at the root 
directory of the zip file. Also at the root level of the zip file, add a 
grid-library.xml with content similar to the following:

<?xml version="1.0" encoding="UTF-8"?>
<grid-library os="linux">
    <grid-library-name>apache-distribution</grid-library-name>
    <grid-library-version>2.2.9</grid-library-version>
    <!-- No JRE arguments -->
    <!-- No dependencies -->
    <!-- No conflicts -->
    <!-- No JAR paths -->
    <!-- No library paths -->
    <!-- No assembly paths -->
    <!-- No command paths -->
    <!-- No hooks paths -->
    <!-- No environment variables -->
    <!-- No system properties -->
</grid-library>

Very important:
	When you tar up the directory, make sure you follow symbolic links, 
otherwise these files are 0 bytes.  For example, in my Apache’s lib 
subdirectory, I have the following symbolic links:
libapr-1.so -> libapr-1.so.0.3.0
libapr-1.so.0 -> libapr-1.so.0.3.0
libaprutil-1.so -> libaprutil-1.so.0.3.0
libaprutil-1.so.0 -> libaprutil-1.so.0.3.0

Step 3
	Copy the distribution Grid Library file to [FS Broker Dir]/webapps/
livecluster/deploy/resources/gridlib directory.

Step 4
	Overwrite httpd.conf in Apache Web Server Container. The default httpd.conf 
shipped with Apache Web Server Container has minimum module support 
configurations. To add PHP support, on Administration Tool, modify either 
httpd.conf in Container by Edit Container or during Application Component 
creation so that it contains the relevant PHP configurations line. You can use 
httpd.conf in the newly build distribution as reference. For example, modify
container's httpd.conf, add the following line:
LoadModule php5_module modules/libphp5.so.

Under <IfModule mine_module>, add the following lines: 
AddHandler php5-script php
AddType text/html       php
AddType application/x-httpd-php-source phps

Save the modified container or Application Component.

Step 5
	Create Application as described in Chapter 3 and start.
