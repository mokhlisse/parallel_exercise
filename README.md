# Web Crawler Multithreading Program
https://github.com/mokhlisse/parallel_exercise
Spring Boot Application to crawl a web page.

## What you will need ?

* JDK 1.8 or later
* [Maven 3.0+](https://maven.apache.org/download.cgi)

## How to use

with Bash

    mvn package -DskipTests && java -jar target/demo-0.0.1.jar

then open report.txt to view urls listed.

## Source Code organization
```
toy-robot/src$ find . | sort | sed '1d;s,[^/]*/,|    ,g;s/..//;s/[^ ]*$/|-- &/'
     |-- .classpath
   |-- mvnw
   |-- mvnw.cmd
   |-- pom.xml
   |-- .project
   |-- README.md
   |-- .settings
   |    |-- org.eclipse.core.resources.prefs
   |    |-- org.eclipse.jdt.core.prefs
   |    |-- org.eclipse.m2e.core.prefs
   |-- src
   |    |-- main
   |    |    |-- java
   |    |    |    |-- com
   |    |    |    |    |-- badre
   |    |    |    |    |    |-- crawl
   |    |    |    |    |    |    |-- Application.java
   |    |    |    |    |    |    |-- model
   |    |    |    |    |    |    |    |-- Url.java
   |    |    |    |    |    |    |-- service
   |    |    |    |    |    |    |    |-- HttpClientBuilder.java
   |    |    |    |    |    |    |    |-- impl
   |    |    |    |    |    |    |    |    |-- LinkServiceImpl.java
   |    |    |    |    |    |    |    |    |-- LinkTask.java
   |    |    |    |    |    |    |    |    |-- ThreadSafeHttpClientBuilder.java
   |    |    |    |    |    |    |    |-- LinkService.java
   |    |    |    |    |    |    |-- utils
   |    |    |    |    |    |    |    |-- Utils.java
   |    |    |-- resources
   |    |    |    |-- application.properties
   |    |    |    |-- log4j2.xml
   |    |-- test
   |    |    |-- java
   |    |    |    |-- com
   |    |    |    |    |-- badre
   |    |    |    |    |    |-- crawl
   |    |    |    |    |    |    |-- demo
   |    |    |    |    |    |    |    |-- UrlServiceTest.java
   |    |    |    |    |    |    |    |-- UtilsTest.java
   |    |    |-- resources
   |    |    |    |-- europe.html

$ mvn test
... [INFO] Scanning for projects...
```
Current program crawl link https://en.wikipedia.org/wiki/Java_Transaction_API, you can change it at com.badre.crawl.service.impl.LinkServiceImpl..INITIAL_URL
You can change how many levels you want to crawl by setting com.badre.crawl.service.impl.LinkServiceImpl.MAX_LEVEL

## License

Licensed under the Apache License, Version 2.0.
