DYNAMIC CLASS RELOADER
======================

Usage
-----
Download the jvmagent.jar to your local directory
Three JVM Arguments have to be specified to use this tool.
-javaagent:
	The value of this attribute is the location where the jvmagent.jar is placed. 
	Eg -javaagent:D:\Project\jvmagent.jar
-Dagent.refreshdir
	This attribute defines the root directory of the class files to monitor.
	Eg -Dagent.refreshdir=D:/iLogistics/Client/Client/clientclasses
-Dagent.refreshinterval
	This attribute is optional and defines the time interval(in milliseconds) for checking the previous attribute (agent.refreshdir) for class file modifications. 
	Eg -Dagent.refreshinterval=10000  Checks the folder every 10 seconds for modifications.

Sample Command to run a java application with the agent
-------------------------------------------------------
java -javaagent:D:\jvmagent.jar -Dagent.refreshdir=D:/iLogistics/Client/clientclasses -Dagent.refreshinterval=10000 com.ibsplc.client.launcher.AppLauncher

Features
--------
Dynamically reloads modified class files.
New instances of the class will use the reloaded class defenition.
Server restart or desktop application restart not required.

Restrictions
------------
New class will not be reloaded if it adds, removes or renames fields or methods, changes the signatures of methods, or changes inheritance.
Existing objects of the class will not be changed(Eg. Singleton objects if created, cannot be reloaded)
Applicable from JDK1.5 onwards.

Advantages
----------
Producitvity Improvement  by avoiding server redeploy/restart time
Significantly reduces time for trouble shooting and bug fix.

APIs Used
---------
Java Instrumentation API http://java.sun.com/javase/6/docs/api/java/lang/instrument/Instrumentation.html
Java bytecode manipulation tool http://asm.ow2.org/