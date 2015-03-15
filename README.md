# Find customer duplications

##Under the hood

What is used in the implementation

###Frameworks
- **Spring:** For dependency inject. I created almost everything with annotations. I kept as xml when it was easier this way.
- **Resteasy:** For REST API implementation and resteasy-client with proxy to write the tests.
- **Spring-Data:** To access database and solr.
- **Dozer:** For java bean mapping between domain and entity objects

###Aplications
- **Derby:** Database.
- **ActiveMQ:** To provide queues for asynchronous processing. It make it easy to scale the batch processing.
- **Solr:** Solr is the main part of the de-duplication. Provides lots of searching features and can be configured to achieve the desired precision. It also can scale.

I configured both Derby and ActiveMQ embedded to make it easy to test the project. I decided to not embed Solr because it is to big to keep inside the project.


##Starting the engine
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
- Import the maven project on eclipse and execute the junit test **midas.service.CustomerServiceTest**. You can check the interface at the end of the test for a reference of what services were implemented.

##Paintshop
I implemented a very simple **Customer** domain. It only has firstName and LastName. With just those two fields I can show how the application will find similar customers no matter the case of the letters, the order of the names and the number of names. Also, the duplicates are returned in order, with the most similar in the first position. Following is the API Reference.

###CRUD methods
The CRUD methods are pretty straightforward. Creating or updating a customer save it in both on Database and Solr. Deleting the customer also removes from both storages. In the future, it would be nice to implement a batch job to re-index the Solr documents, to prevent eventual inconsistencies.

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
Checking for duplicates is available in two flavours, both returns up to 5 results. First, you can get the duplicates directly from one customer; in this case, the search is made on the fly. The second option is to list all customers that may have duplicates; for that to work, first is necessary to indexing all the customers, that is, to look for duplicates for every existent customer. I implemented the index as a REST service, so testing is easy. In the future it can also be an scheduled process.

####Retrieve possible duplicates from one customer
```bash
$ curl  "http://localhost:9095/customers/21/duplicates/" -H "Accept: application/json"
{"duplicates":[{"id":22,"firstName":"Caio","lastName":"Brandao Amaral"},{"id":20,"firstName":"Kyle","lastName":"Amaral"}]}
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
The next steps are to add more fields to the customer and test some Solr search(MoreLikeThis, GeoSearch, Terms frequency and relevancy).