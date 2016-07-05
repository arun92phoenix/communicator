# Communicator

Communicator is a simple LAN based chat application that can be deployed easily in any environment where Java is available. 

## Installation

Communicator is built using Spring Boot and comes with embedded Tomcat. Installation and running communicator is as simple as downloading and opening a file. 

Access the [latest release](https://github.com/arun92phoenix/communicator/releases/latest) and download the jar file to the server where you want communicator to run and execute by opening it. Communicator will be accessible to everyone at the following location

```sh
http://<<server_ip/hostname>>/index.html
```

### Changing the Tomcat Port

The default port used will be 8080. Refer to the [this link](http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#howto-change-the-http-port) on how the default port can be changed.

## Minimum Requirements

Java Runtime Environment 1.7 or higher is needed to run Communicator. 

The communication with the server (both from and to) happens with Websockets. SockJS is being used to provide a fallback mechanism in browsers where websockets are not supported but this feature is not fully tested yet.

## Release Notes
 - [Release notes of Version 1.0.0](https://github.com/arun92phoenix/communicator/releases/tag/v1.0.0)
 - [Release notes of Version 1.1.0](https://github.com/arun92phoenix/communicator/releases/tag/v1.1.0)
 
