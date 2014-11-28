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

	@RequestMapping("/recommendations/TopMovies")
	public String getSortedTopMovies() {
		CassandraConnector cassandraConnector = new CassandraConnector();
		return cassandraConnector.getTopMoviesList().toString();
	}

	@RequestMapping("/recommendations/UserBased")
	public String userRecommendMovie(@RequestParam String userID) {
		return FluentHbaseClient.userRecommendMovie(userID);
	}

	@RequestMapping("/recommendations/ItemBased")
	public String similarMovieRecommend(@RequestParam String movieID) {
		return FluentHbaseClient.similarMovieRecommend(movieID);
	}

	@RequestMapping("/recommendations/AllTimeTopMovies")
	public String allTimeTopMovies() {
		CassandraConnector cassandraConnector = new CassandraConnector();
		return cassandraConnector.getAllTimeTopMoviesList().toString();
	}

}
