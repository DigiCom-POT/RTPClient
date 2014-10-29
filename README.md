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
**Get Top 5 Movies**
```
http://localhost:8983/recommendations/TopMovies
``` 

**Get Recommendations for the User**
```
http://localhost:8983/recommendations/UserBased?userID=100
```

**Get Similar Recommendations for movies**
```
http://localhost:8983/recommendations/ItemBased?movieID=6659
```