# Find customer duplications

##Under the hood

What is used in the implementation

###Frameworks
Spring: For dependency inject. I created almost everything with annotations. I kept as xml when it was easier this way.
Resteasy: For REST API implementation and resteasy-client with proxy to write the tests.
Spring-Data: To access database and solr.
Dozer: For java bean mapping between domain and entity objects

###Aplications
Derby: Database.
ActiveMQ: To provide queues for asynchronous processing. It make it easy to scale the batch processing.
Solr: Solr is the main part of the de-duplication. Provides lots of searching features (MoreLikeThis, GeoSearch, Terms frequency and relevancy) and 
     can be configured to achieve the desired precision. It also can scale.

I put both Derby and ActiveMQ embedded to make it easy to test the project. I decided to not embed Solr because it is to big to keep inside the project.


##How to execute
Sadly I am using Windows here, let me know in case of any problems with these steps

###Starting solr
- Download it from http://www.apache.org/dyn/closer.cgi/lucene/solr/5.0.0
- Unzip it, lets say inside /opt/solr/, start it and create the solr core`
```bash
$ cd /opt/solr/
$./bin/solr start -p 8983
$ ./bin/solr create -c customer;
```
 
###Executing find-dups
- Clone the repo or download the zip from https://github.com/camaral1/find-dups , lets say inside ~/find-dups/ and start the application.
```bash
$ cd ~/find-dups
$ mvn jetty:run
```
- Import the project on eclipse and execute the junit test midas.service.CustomerServiceTest . You can check the interface at the end of the test for a reference of what services were implemented.
 
 