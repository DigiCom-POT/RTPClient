Light-weight rest client to access the batch and real time personalization
==========================================================================
[![Build Status](https://travis-ci.org/DigiCom-POT/RTPClient.svg)](https://travis-ci.org/DigiCom-POT/RTPClient)
[![Coverage Status](https://coveralls.io/repos/DigiCom-POT/RTPClient/badge.png)](https://coveralls.io/r/DigiCom-POT/RTPClient)


Used tech stack:
----------------
- Spring Boot ( to expose the resAPI )
- Apache Fluent ( to make call the Hbase rest client)
- Java Driver for Cassandra ( for getting real time data from cassandra )
- Google Gson (for converting the response to Gson)


Sample URLs
-----------
**For Sape Network Users**
```
https://www.getpostman.com/collections/e3c8dbef8c8ec15c5fa4
```

**Get Top 5 Movies (real time for last few hours )**
```
http://localhost:60088/recommendations/TopMovies
``` 

**Get All time Top 5 Movies (real time plus batch )**
```
http://localhost:60088/recommendations/AllTimeTopMovies
```

**Get Recommendations for the User**
```
http://localhost:60088/recommendations/UserBased?userID=100
```

**Get Similar Recommendations for movies**
```
http://localhost:60088/recommendations/ItemBased?movieID=6659
```

**High Level Arch Diagram**
![High Level Arch Diagram](https://raw.githubusercontent.com/DigiCom-POT/RTPClient/master/src/main/resources/rtppot.PNG)


Note :
------
For the above URLs to work

- You need to have the HBase Mater and Region Server up and running
- Hbase Rest Deamon up and running [If you start as service the port may vary to 60080]
```
•	./bin/hbase-daemon.sh start rest -p 8500
•	./bin/hbase-daemon.sh stop rest -p 8500
```
- Cassandra service up and running

- To bring up the web client [Add 60088 to port forwarding rule in Virtual Box]
```
 java -Dserver.port=60088 -jar digicom-rtpclient-0.0.1-SNAPSHOT.jar
```
