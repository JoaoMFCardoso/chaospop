# ChaosPop

ChaosPop is a framework that allows structured raw data concepts to be mapped into concepts of provided domain ontologies. It then allows users to create populated ontologies, by creating individuals from the structured raw data.

## Domain

This ER Diagram details the ChaosPop domain.

<p align="center">
  <img width="460" height="300" src="https://raw.githubusercontent.com/JoaoMFCardoso/chaospop/blob/master/documentation/ChaosPopERD.png">
</p>

![ChaosPop domain ERD ](https://raw.githubusercontent.com/JoaoMFCardoso/chaospop/blob/master/documentation/ChaosPopERD.png)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

Software that you need to install:

* [Java 8 or later](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - The software development kit that is used to develop the application.
* [MongoDB 3.6 or later](https://www.mongodb.com/download-center/enterprise/releases) - The database used
* [Tomcat 9 or later](https://tomcat.apache.org/download-90.cgi) - The web application server used
* [Eclipse IDE for Java EE Developers](http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/photon/R/eclipse-jee-photon-R-win32-x86_64.zip) - To run and debug ChaosPop locally.

### Installing

To install and run ChaosPop you need to take the following steps:

* Configure MongoDB

1. Create the database
```
use ontorepo
```

2. Create the user
```
db.createUser(
   {
     user: "chaospop",
     pwd: "ChaosPop2018",
     roles: [ "readWrite", "dbAdmin" ]
   }
)
```

* Configure a Tomcat server in Eclipse 

Please refer to the following [tutorial](https://crunchify.com/step-by-step-guide-to-setup-and-install-apache-tomcat-server-in-eclipse-development-environment-ide/).

## Deployment

//TODO

## Authors

* **João Cardoso** - [INESC-ID Profile](https://www.inesc-id.pt/member/12489/)

## License

//TODO

## Acknowledgments

* Ana Baptista [tester]
* Eliane Galdino [tester]
