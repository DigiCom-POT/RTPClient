package digicom.rtpclient.dsc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.gson.Gson;

import digicom.rtpclient.flume.FluentHbaseClient;

public class CassandraConnector {
	private static Cluster cluster;
	private static Session session;
	static CassandraConnector client = new CassandraConnector();

	private static PreparedStatement alldata;
	private static PreparedStatement alldatabatch;

	Logger logger = LoggerFactory
			.getLogger(CassandraConnector.class);

	public void init() {
		session = client.connect("127.0.0.1");
		alldata = session.prepare("select * from top_movie");
		alldatabatch = session.prepare("select * from top_movie_new");
	}


	public Map<String, Long> getTopMovies(String mode) {
		Map<String, Long> moviesmap = new HashMap<String, Long>();
		try {
			if (null == session) {
				init();
			}
			ResultSet result = null;
			if (null != mode && mode.equalsIgnoreCase("realtime")) {
				result = session.execute(alldata.bind());
			} else {
				result = session.execute(alldatabatch.bind());
			}

			if (!result.isExhausted()) {
				List<Row> alldata = result.all();
				for (Row row : alldata) {
					moviesmap.put(row.getString("movieid"),
							row.getLong("viewscnt"));
				}
			}
		} catch (Exception e) {
			logger.info(" Exception while getting the count for movie "
					+ e);
		}
		return moviesmap;
	}

	public Map<String, Long> getSortedTopMovies(String mode) {
		Map<String, Long> moviesmap = getTopMovies(mode);

		ValueComparator bvc = new ValueComparator(moviesmap);
		TreeMap<String, Long> sorted_map = new TreeMap<String, Long>(bvc);
		sorted_map.putAll(moviesmap);

		logger.info("SORTED MAP " + sorted_map);
		return sorted_map;
	}

	public static void close() {
		cluster.close();
	}

	private Session connect(String node) {
		cluster = Cluster.builder().addContactPoint(node).build();
		Metadata metadata = cluster.getMetadata();
		System.out.printf("Connected to cluster: %s\n",
				metadata.getClusterName());
		for (Host host : metadata.getAllHosts()) {
			System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
					host.getDatacenter(), host.getAddress(), host.getRack());
		}
		return cluster.connect("test");
	}

	public String getTopMoviesList() {
		Map<String, Long> map = getSortedTopMovies("realtime");
		List<Movies> moviesList = getMoviesInfomation(map, 5);
		Gson g = new Gson();
		return g.toJson(moviesList);
	}

	/**
	 * Gets the movies information
	 * 
	 * @param map
	 * @param lenght
	 * @return
	 */
	private List<Movies> getMoviesInfomation(Map<String, Long> map,
			int lenght) {
		List<Movies> moviesList = new ArrayList<Movies>();
		int count = 0;

		for (String movieId : map.keySet()) {
			Movies m = new Movies();
			m.setMovieId(movieId);
			m.setViewCount(map.get(movieId));
			String movInfo = null;
			try {
				movInfo = FluentHbaseClient.getMovieInfo(movieId);
				if (null != movInfo) {
					String[] array = movInfo.split("\\^");
					m.setMovieGenre(array[0]);
					m.setMovieName(array[1]);
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			moviesList.add(m);
			count++;
			if (lenght != -1 && count > lenght) {
				break;
			}
		}
		return moviesList;
	}

	/**
	 * Returns the aggregated view of the Batch And Real Time
	 * 
	 * @return
	 */
	public String getAllTimeTopMoviesList() {
		// Top Movies from the real time db
		Map<String, Long> batchmap = getTopMovies("batch");
		Map<String, Long> realtimemap = getTopMovies("real");

		for (String movie : realtimemap.keySet()) {
			if (null != batchmap.get(movie)) {
				batchmap.put(movie,
						batchmap.get(movie) + realtimemap.get(movie));
			} else {
				batchmap.put(movie, realtimemap.get(movie));
			}
		}

		ValueComparator bvc = new ValueComparator(batchmap);
		TreeMap<String, Long> sorted_map = new TreeMap<String, Long>(bvc);
		sorted_map.putAll(batchmap);

		List<Movies> moviesList = getMoviesInfomation(sorted_map, 5);
		Gson g = new Gson();
		return g.toJson(moviesList);
	}
}

/**
 * Custom comparator class to sort the movies
 * @author spras3
 *
 */
class ValueComparator implements Comparator<String> {
	Map<String, Long> map;

	ValueComparator(Map<String, Long> map) {
		this.map = map;
	}

	@Override
	public int compare(String o1, String o2) {
		int i = (int) (map.get(o2) - map.get(o1));
		if (i == 0)
			i++;
		return i;
	}
}
