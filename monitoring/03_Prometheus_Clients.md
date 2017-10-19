# Prometheus With Java

## 1. Prometheus

Prometheus is an open-source systems monitoring and alerting toolkit with an active ecosystem.
The main Prometheus server runs standalone and has no external dependencies.
Prometheus uses the *WhiteBox* and *Pull* technologies (phisolophies) to get the metrics directly from the sources, .

For high availability of the Alertmanager, you can run multiple instances in a *Mesh cluster* and configure the Prometheus servers to send notifications to each of them.

### 1.1 Definitions

Monitoring types:

- *Blackbox monitoring* Monitoring from the outside No knowledge of how the application works internally 
   Examples: ping, HTTP request, inserting data and waiting for it to appear on dashboard 
   
   - Blackbox monitoring should be treated similarly to smoke tests.
   - It’s good for finding when things have badly broken in an obvious way, and testing from outside your network
   - Not so good for knowing what’s going on inside a system
   
- *Whitebox Monitoring* Complementary to blackbox monitoring. Works with information from inside your systems. 
   Can be simple things like CPU usage, down to the number of requests triggering a particular obscure codepath. 
   
Pulling over HTTP offers a number of advantages:

- You can run your monitoring on your laptop when developing changes.
- You can more easily tell if a target is down.
- You can manually go to a target and inspect its health with a web browser.

In case of Prometheus, pulling is slightly better than pushing, but it should not be considered a major point when considering a monitoring system.

### 1.2 Prometheus Client Libraries

Choose a Prometheus client library that matches the language in which your application is written. 

This lets you define and expose internal metrics via an HTTP endpoint on your application’s instance.

There are several clients libraries available:

- Official Client libaries

    Go, Java or Scala, Python, Ruby

- Unofficial third-party client libraries:

    Bash,C++,Common Lisp,Elixir,Erlang, Haskell, Lua for Nginx, Lua for Tarantool,.NET / C#, Node.js, PHP, Rust

When Prometheus scrapes your instance's HTTP endpoint, the client library sends the current state of all tracked metrics to the server.
	
	http://localhost:9999/metrics
	
## 2. Prometheus Java Client

Following section will describe how to build an application that expose some metrics using Prometheus client library.

## 2.1 Maven installation

For the installation of Maven you will need to have already installed Java Runtime.

First at all, it's necessary to have the OS updated to follow the steps for the isntallation:

	$ sudo apt-get update -y
	$ sudo apt-get upgrade -y

To install defaults JRE and JDK using Ubuntu distribution, just use the following commands:

	$ sudo apt-get update
	$ sudo apt-get install -y default-jre default-jdk
	
However, sometimes it is required to install a particular version of Java, depending on the environment:

- Add Oracle Java PPA repository:

	$ sudo add-apt-repository ppa:webupd8team/java
	
- Update Ubuntu Packages (again)

	$ sudo apt-get update -y
	
- Install the latest version 

	$ sudo apt-get install oracle-java8-installer

- Verify the Java Version isntalled

	$ java -version
	
Once Java has been installed and the version is the correct one, proceed to install Maven Packages:

- Donwload the packages onto the */opt* and extract the package:

	$ cd /opt/
	$ sudo wget http://ftp.cixug.es/apache/maven/maven-3/3.5.0/binaries/apache-maven-3.5.0-bin.tar.gz
	
	$ sudo tar -xvzf apache-maven-3.5.0-bin.tar.gz
	
> The version of Maven it's just an example. You would need to use another specific version or the latest one.
	
- Rename the directory for simple management

	$ sudo mv apache-maven-3.5.0 maven 

> Be aware, the folder is not the same as the extracted file name.
	
- Setup the environemnt variables: *M2_HOME*, *M2*, *MAVEN_OPTS*, and *PATH*. 

- Create a *mavenenv.sh* file inside of the */etc/profile.d/* directory.

	$ sudo vi /etc/profile.d/mavenenv.sh
	
	*mavenenv.sh*
	
	export M2_HOME=/opt/maven
	export PATH=${M2_HOME}/bin:${PATH}
	
- Create execution priviledges and finally the script:

	$ sudo chmod +x /etc/profile.d/mavenenv.sh
	$ sudo source /etc/profile.d/mavenenv.sh

- Verify Maven is installed correctly:

	mvn --version


## 2.2 Simple Project

The idea is to create a simple Servlet Project developed with Java Client using Prometheus client.

In this examplem a *Counter* is going to be implemented as *hello_worlds_total*.
This metric increments every time a request is made.

The project has to files:

- Project Object Model: with the *maven* configuration file (.pom) 
- Java Class within the following package: *src/main/java/io/prometheus/java_examples/*

The folder extructure is the following:

	- project
		| -pom.xml
		| -src
			| - main
				| - java
					| - io
						| - prometheus
							| - java_examples
								| - JavaSimple.java


*pom.xml*

	<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
	  <modelVersion>4.0.0</modelVersion>
	  <groupId>io.prometheus.java_examples</groupId>
	  <artifactId>java_simple</artifactId>
	  <packaging>jar</packaging>
	  <version>1.0-SNAPSHOT</version>
	  <name>java_simple</name>
	  <url>http://maven.apache.org</url>
	  <dependencies>
		<dependency>
		  <groupId>io.prometheus</groupId>
		  <artifactId>simpleclient</artifactId>
		  <version>0.0.23</version>
		</dependency>
		<dependency>
		  <groupId>io.prometheus</groupId>
		  <artifactId>simpleclient_hotspot</artifactId>
		  <version>0.0.23</version>
		</dependency>
		<dependency>
		  <groupId>io.prometheus</groupId>
		  <artifactId>simpleclient_servlet</artifactId>
		  <version>0.0.23</version>
		</dependency>
		<dependency>
		  <groupId>org.eclipse.jetty</groupId>
		  <artifactId>jetty-servlet</artifactId>
		  <version>8.1.7.v20120910</version>
		</dependency>
	  </dependencies>

	  <build>
		<plugins>
		  <plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-compiler-plugin</artifactId>
			<version>3.1</version>
			<configuration>
			  <source>1.8</source>
			  <target>1.8</target>
			</configuration>
		  </plugin>
		  <!-- Build a full jar with dependencies --> 
		  <plugin>
			<artifactId>maven-assembly-plugin</artifactId>
			<configuration>
			  <archive>
				<manifest>
				  <mainClass>io.prometheus.java_examples.JavaSimple</mainClass>
				</manifest>
			  </archive>
			  <descriptorRefs>
				<descriptorRef>jar-with-dependencies</descriptorRef>
			  </descriptorRefs>
			</configuration>
			<executions>
			  <execution>
				<id>make-assembly</id>
				<phase>package</phase>
				<goals>
				  <goal>single</goal>
				</goals>
			  </execution>
			</executions>
		  </plugin>
		</plugins>
	  </build>
	</project>

*JavaSimple.java*

	package io.prometheus.java_examples;

	import io.prometheus.client.Counter;
	import io.prometheus.client.exporter.MetricsServlet;
	import io.prometheus.client.hotspot.DefaultExports;
	import javax.servlet.ServletException;
	import javax.servlet.http.HttpServlet;
	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;
	import org.eclipse.jetty.server.Server;
	import org.eclipse.jetty.servlet.ServletContextHandler;
	import org.eclipse.jetty.servlet.ServletHolder;
	import java.io.IOException;


	public class JavaSimple {
	  static class ExampleServlet extends HttpServlet {
		static final Counter requests = Counter.build()
			.name("hello_worlds_total")
			.help("Number of hello worlds served.").register();
		
		@Override
		protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		  resp.getWriter().println("Hello World!");
		  // Increment the number of requests.
		  requests.inc();
		}
	  }

	  public static void main( String[] args ) throws Exception {
		  Server server = new Server(1234);
		  ServletContextHandler context = new ServletContextHandler();
		  context.setContextPath("/");
		  server.setHandler(context);
		  // Expose our example servlet.
		  context.addServlet(new ServletHolder(new ExampleServlet()), "/");
		  // Expose Promtheus metrics.
		  context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");
		  // Add metrics about CPU, JVM memory etc.
		  DefaultExports.initialize();

		  // Start the webserver.
		  server.start();
		  server.join();
	  }
	}

> To build the example bellow Maven is needed. 

Following are the steps to build the project:

- Copy the project source-code and go to the location of the pom.xml file 

- Build Maven Project. It will install all neccesary dependences and builts the java package

	$ mvn package
	
> Verify java jdk and maven are correctly isntalled
	
- Run java application using java runtime and using references in package created by maven.

	$ java -jar target/java_simple-1.0-SNAPSHOT-jar-with-dependencies.jar
	
- Verify if it's working correctly and metrics

	http://localhost:1234/ 
	
- The metrics implemented is a Counter called *hello_worlds_total*, that increments every time the app is refreshed. 
	
	http://localhost:1234/metrics

- Verify following metrics appear

	# HELP hello_worlds_total Number of hello worlds served.
	# TYPE hello_worlds_total counter
	hello_worlds_total 3.0
	
## 2.3 Basic client Implementation

The previous example was based using a simple *Counter* Metrics. 
In prometheus Metrics could be: Histograms, counters, summaries, etc.
The nomenclature of the metrics should be understandable and clear to the users.

There’s two statements that are important in ExampleServlet. To create a Counter just use the following

	 static final Counter requests = Counter.build()
	   .name("hello_worlds_total")
	   .help("Number of hello worlds served.").register();

This creates the metric which is shared across all instances of the object. It gives it a name and includes some help text, so that those using the metric later on will know what it means.

	requests.inc();

This simple statement increments the counter by one. 

- Setup

There’s a small bit of setup work that need to be done once, no matter how many metrics you have. To expose the metrics used in your code, we add the Prometheus servlet to our Jetty server:

	context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");

You may have noticed that there were many other useful *metrics* included about the JVM and process. These come from several classes, but it’s only one line to use them:

	DefaultExports.initialize();

Following are the dependences needed into *pom.xml*:

	<dependency>
	  <groupId>io.prometheus</groupId>
	  <artifactId>simpleclient</artifactId>
	  <version>0.0.11</version>
	</dependency>
	<dependency>
	  <groupId>io.prometheus</groupId>
	  <artifactId>simpleclient_hotspot</artifactId>
	  <version>0.0.11</version>
	</dependency>
	<dependency>
	  <groupId>io.prometheus</groupId>
	  <artifactId>simpleclient_servlet</artifactId>
	  <version>0.0.11</version>
	</dependency>
	
## 2.4 Run Application With prometheus

The process to add targets to Prometheus can be done by using some ways:

> https://prometheus.io/docs/operating/configuration/

The simplest way is using static references in the config file:








## 3. Prometheus JMX Exporter


https://www.robustperception.io/monitoring-kafka-with-prometheus/
