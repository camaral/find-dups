# Find customer duplications

##Under the hood

What is used in the implementation

###Frameworks
- **Spring:** For dependency inject. Almost everything was created with annotations. I kept as XML when it was simpler this way.
- **Resteasy:** For REST API implementation and resteasy-client with proxy to write the tests.
- **Spring-Data:** To access Database and Solr.
- **Dozer:** For java bean mapping between domain and entity objects.

###Aplications
- **Derby:** An super easy to setup Database.
- **ActiveMQ:** To provide queues for asynchronous processing. This way we can scale the batch processing.
- **Solr:** The main part of the de-duplication. Provides lots of searching features and can be configured to achieve the desired precision. It also can scale.

I configured both Derby and ActiveMQ embedded to simplify the project testing and development. Solr was not embedded because it is too big to keep inside the project.


##Starting the engine

###Starting solr
- Download it from http://www.apache.org/dyn/closer.cgi/lucene/solr/5.0.0
- Unzip it, lets say, inside /opt/solr/, start it and create the solr core`
```bash
$ cd /opt/solr/
$./bin/solr start -p 8983
$ ./bin/solr create -c customer;
```
 
###Executing find-dups
- Clone the repo or download the zip from https://github.com/camaral/find-dups. Put it inside ~/find-dups/ (or any other path) and start the application.
```bash
$ cd ~/find-dups
$ mvn jetty:run
```
- Import the Maven project on Eclipse and execute the junit test  **midas.service.CustomerServiceTest** You can check the interface at the end of the test for a reference of what services were implemented.

##Paintshop
I implemented a very simple **Customer** domain. It only has firstName and LastName. With just those two fields I can show how the application will find similar customers no matter the case of the letters, the order of the names and the number of names. Also, the duplicates are returned in order, with the most similar in the first position.

Following is the API Reference.

###CRUD methods
The CRUD methods are pretty straightforward. Creating or updating a customer save it in both on Database and Solr. Deleting the customer also removes from both storages.

####Create
```bash
$ curl -v "http://localhost:9095/customers/" -d "{\"firstName\":\"Caio\", \"lastName\":\"Amaral\"}" -H "Content-Type: application/json" -H "Accept: application/json"
> POST /customers/ HTTP/1.1
> User-Agent: curl/7.38.0
> Host: localhost:9095
> Content-Type: application/json
> Accept: application/json
> Content-Length: 41
>
< HTTP/1.1 201 Created
< Content-Type: application/json
< Location: http://localhost:9095/customers/1
< Transfer-Encoding: chunked
< Server: Jetty(6.1.15)
<
{"id":1,"firstName":"Caio","lastName":"Amaral"}

```

####Retrieve
```bash
$ curl -v "http://localhost:9095/customers/1" -H "Accept: application/json"
> GET /customers/1 HTTP/1.1
> User-Agent: curl/7.38.0
> Host: localhost:9095
> Accept: application/json
>
< HTTP/1.1 200 OK
< Content-Type: application/json
< Transfer-Encoding: chunked
< Server: Jetty(6.1.15)
<
{"id":1,"firstName":"Caio","lastName":"Amaral"}
```

####Update
```bash
$ curl  "http://localhost:9095/customers/1" -X PUT -d "{\"firstName\":\"Kyle\", \"lastName\":\"Amaral\"}" -H "Content-Type: application/json" -H "Accept: application/json"
{"id":1,"firstName":"Kyle","lastName":"Amaral"}
```

####Delete
```bash
$ curl  "http://localhost:9095/customers/1" -X DELETE -H "Accept: application/json"
{"id":1,"firstName":"Kyle","lastName":"Amaral"}
```

###Duplicates
Checking for duplicates is available in two flavours, both returns up to 5 results. First, you can get the duplicates directly from one customer; in this case, the search is made on the fly. The second option is to list all customers that may have duplicates; for that to work, first is necessary to index all the customers, that is, to look for duplicates for every existent customer and save it in the Database. The index was implemented as a REST service to enable easy testing; in the future it can also be a scheduled process.

####Retrieve possible duplicates from one single customer
```bash
$ curl  "http://localhost:9095/customers/21/duplicates/" -H "Accept: application/json"
{
   "duplicates":[
      { "id":22,"firstName":"Caio","lastName":"Brandao Amaral"},
      {"id":20,"firstName":"Kyle","lastName":"Amaral"}
   ]
}
```

####Retrieve all possible duplicates
```bash
# sync=true responds only after the index process finishes; sync=false (default) accepts the request and process it asynchronously
$ curl  "http://localhost:9095/customers/duplicates/index?sync=true" -X POST -H "Accept: application/json"
{"pages":1,"count":10,"status":"FINISHED"}

$ curl  "http://localhost:9095/customers/duplicates?page=0&count=10" -H "Accept: application/json"
{  
   "page":0,
   "pages":1,
   "count":10,
   "items":[  
      {  "id":20,
         "firstName":"Kyle",
         "lastName":"Amaral",
         "duplicates":[ { "id":21,"firstName":"Caio", "lastName":"Amaral"},
            { "id":22, "firstName":"Caio", "lastName":"Brandao Amaral" }
         ]
      },
      {  "id":21,
         "firstName":"Caio",
         "lastName":"Amaral",
         "duplicates":[ { "id":22, "firstName":"Caio", "lastName":"Brandao Amaral"},
            { "id":20, "firstName":"Kyle", "lastName":"Amaral"
            }
         ]
      },
      {  "id":22,
         "firstName":"Caio",
         "lastName":"Brandao Amaral",
         "duplicates":[{ "id":21, "firstName":"Caio","lastName":"Amaral"},
            { "id":20, "firstName":"Kyle", "lastName":"Amaral" }
         ]
      }
   ]
}
```

##Road trip
The project was build with scalability in mind and Solr is returning good results even with no configuration. So with some improvements it will be ready to deploy in production. The next steps are:
- Add more fields to the customer 
- Test some Solr search features: MoreLikeThis, GeoSearch, Terms frequency, Word processing(Stop words, Stemming etc), Highlights
- It would be nice to implement a mechanism to prevent eventual inconsistencies between Database and Solr
- Create a real Database, create all the SQL scripts and improve the model to hold more information about the duplicates (e.g. probalility of being a duplicate)
- Externalize configurations
- Create scheduled process to re-index the duplicates
