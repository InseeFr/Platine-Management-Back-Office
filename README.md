# Platine-Management-Back-Office
Back office services for Platine data collection management
REST API for communication between DB and Platine-Management UI and Platine-My-Surveys UI

## Requirements

For building and running the application you need:

- JDK 21
- Maven 3

## Install and excute unit tests

Use the maven clean and maven install

```shell
mvn clean install
```

## Running the application locally

Use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Application Accesses locally

To access to swagger-ui, use this url : [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Deploy application on Tomcat server

### 1. Package the application

Use the [Spring Boot Maven plugin] (https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn clean package
```

The jar will be generate in `/target` repository

### 2. Tomcat start

From a terminal navigate to tomcat/bin folder and execute

```shell
catalina.bat run (on Windows)
```

```shell
catalina.sh run (on Unix-based systems)
```

### 3. Application Access

To access to swagger-ui, use this url : [http://localhost:8080/swagger-ui.html](http://localhost:8080/pearljam/swagger-ui.html)

## Before you commit

Before committing code please ensure,  
1 - README.md is updated  
2 - A successful build is run and all tests are sucessful  
3 - All newly implemented APIs are documented  
4 - All newly added properties are documented
