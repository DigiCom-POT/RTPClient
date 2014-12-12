package digicom.rtpclient;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import digicom.rtpclient.dsc.CassandraConnector;
import digicom.rtpclient.flume.FluentHbaseClient;

@RestController
public class TopMovieWebClientSVB {

	@RequestMapping("/")
	public String index() {
		return "Greetings from RTP Client !";
	}

	@RequestMapping("/getTopMovies")
	public String getTopMovies(@RequestParam String mode) {
		// Mode can be either realtime or batch
		CassandraConnector cassandraConnector = new CassandraConnector();
		Gson g = new Gson();
		return g.toJson(cassandraConnector.getTopMovies(mode));
	}

	/**
	 * Gives the result from real time processed data
	 * (Spark Streaming)
	 * @return
	 */
	@RequestMapping("/recommendations/TopMovies")
	public String getSortedTopMovies() {
		CassandraConnector cassandraConnector = new CassandraConnector();
		return cassandraConnector.getTopMoviesList().toString();
	}

	/**
	 * Recommends the movies based on user taste.
	 * Use User-Item based CF
	 * @param userID
	 * @return
	 */
	@RequestMapping("/recommendations/UserBased")
	public String userRecommendMovie(@RequestParam String userID) {
		return FluentHbaseClient.userRecommendMovie(userID);
	}

	/**
	 * Recommends the movie based on Item-Item Similarity
	 * It is more static in nature
	 * @param movieID
	 * @return
	 */
	@RequestMapping("/recommendations/ItemBased")
	public String similarMovieRecommend(@RequestParam String movieID) {
		return FluentHbaseClient.similarMovieRecommend(movieID);
	}

	/**
	 * Gives the result from real time and batch processed data
	 * @return
	 */
	@RequestMapping("/recommendations/AllTimeTopMovies")
	public String allTimeTopMovies() {
		CassandraConnector cassandraConnector = new CassandraConnector();
		return cassandraConnector.getAllTimeTopMoviesList().toString();
	}

}
