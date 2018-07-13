
What is odata?

“Open Data Protocol (OData) is an open data access protocol from Microsoft that allows the creation and consumption of query-able and interoperable RESTful APIs in a simple and standard way”.

OData is an open source to exchange data over the Internet. Server hosts the data  and clients can call this service to retrieve the resources and manipulate them. Servers expose one or more endpoints which are services that refers to the resources. Clients need to know this server side endpoints to call the service to query or manipulate the data. The protocol is HTTP based and designed with RESTful mindset which means it follows the constraints to be called as a RESTful service.

OData (Open Data Protocol) is an ISO/IEC approved, OASIS standard that defines a set of best practices for building and consuming RESTful APIs. OData helps you focus on your business logic while building RESTful APIs without having to worry about the various approaches to define request and response headers, status codes, HTTP methods, URL conventions, media types, payload formats, query options, etc. OData also provides guidance for tracking changes, defining functions/actions for reusable procedures, and sending asynchronous/batch requests.

OData RESTful APIs are easy to consume. The OData metadata, a machine-readable description of the data model of the APIs, enables the creation of powerful generic client proxies and tools.

Why we need odata?
 The problem is data everywhere in the world and usually this data is locked into silos so what does that mean actually rdbms, file systems, xml files, nosql databases, all this data stores to have usually have proprietary API how you can access the data so for relational database you will use sql if the data is relational and on the rdbms, if you use xml you have a parser or if you use a nosql database you have a query API usually.

 The problem is if you want to consume all of this data you have to have knowledge about all of these different access points , in the goal of data is first of all to have an abstraction, so all of these data stores can be exposed via our data protocol and this protocol works on the internet. So binary APi doesn't work on the internet, ODBS or JDBC doesn't work on the internet. Internet is HTTP so the basic idea is to make the data accessible over http and put something like the idea behind SQL into that protocol.


Pros:
	Hide the underlying data source whatever it is.
	Hide the schema and where data comes from.
	Standard way to publish and consume a resource.
	Multiple clients can use the service.
	Potentially avoids the cost of building data exploration and query tools, letting some smart client do that for you.
	Driven by the private sector, meaning serious dollars invested in high quality tools.
	Client can combine information from multiple data sources.

Cons:
	Connection is via the web. possible lost of performance on bigdata. Odata not good solution between bigdata tools.
	Some issues managing associated big data.
	Service must be implemented and tuned to optimized the performance.
	Driven by business and Microsoft, meaning those high quality tools are expensive and locked to MS technologies that we don’t use. We may develop our tools.
	Future potential unclear
	Lower barrier to entry than LINKED DATA/RDF, odata easy than these.

https://www.slideshare.net/beautifulcode/o-data-atglance
https://stevebennett.me/tag/odata/

Problems:
1) Dogrumu emin degilim?
	not currently support Deep update and Referencing Nested Inserted entities features of OData V4.01 
	https://groups.google.com/forum/#!topic/odata-discussion/7E4rrn9C9u4
	https://issues.oasis-open.org/browse/ODATA-666


Frameworks and Libraries

http://odata4j.org/
Apache olingo

Projects

https://github.com/rohitghatol/spring-boot-Olingo-oData

https://github.com/Hevelian/hevelian-olastic


Resources

https://www.progress.com/blogs/odata-faqs-why-should-rest-api-developers-use-odata

???https://dzone.com/articles/what-is-odata-rest-easy-with-our-quick-guide

https://blogs.sap.com/2017/11/27/how-to-create-olingo-v2.0-odata-service-with-spring-cloud-in-cloudfoundry/

http://www.csharplearners.com/2016/06/15/odata-pros-and-cons/

???https://blogs.sap.com/2016/03/01/odata-everything-that-you-need-to-know-part-8/
https://blogs.sap.com/2016/02/08/odata-everything-that-you-need-to-know-part-3/

http://www.odata.org/getting-started/basic-tutorial/
http://www.odata.org/getting-started/understand-odata-in-6-steps/


ÖNEMLİ:

???https://templth.wordpress.com/2015/04/03/handling-odata-queries-with-elasticsearch/
???https://templth.wordpress.com/2015/04/10/what-can-odata-bring-to-elasticsearch/