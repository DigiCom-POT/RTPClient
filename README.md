Light-weight rest client to access the batch and real time personalization
==========================================================================

Used tech stack:
----------------
- Spring Boot ( to expose the resAPI )
- Apache Fluent ( to make call the Hbase rest client)
- Java Driver for Cassandra ( for getting real time data from cassandra )
- Google Gson (for converting the response to Gson)


Sample URLs
-----------
**Get Top 5 Movies (real time for last few hours )**
```
http://localhost:8983/recommendations/TopMovies
``` 

**Get All time Top 5 Movies (real time plus batch )**
```
http://localhost:8983/recommendations/AllTimeTopMovies
```

**Get Recommendations for the User**
```
http://localhost:8983/recommendations/UserBased?userID=100
```

**Get Similar Recommendations for movies**
```
http://localhost:8983/recommendations/ItemBased?movieID=6659
```

Note :
------
For the above URLs to work

- You need to have the HBase Mater and Region Server up and running
- Hbase Rest Deamon up and running
```
•	./bin/hbase-daemon.sh start rest -p 8500
•	./bin/hbase-daemon.sh stop rest -p 8500
```
- Cassandra service up and running
