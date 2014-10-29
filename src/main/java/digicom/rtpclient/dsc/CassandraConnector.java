package digicom.rtpclient.dsc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.http.client.ClientProtocolException;

import com.datastax.driver.core.BoundStatement;
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

	private static PreparedStatement ps;
	private static PreparedStatement load;
	private static PreparedStatement alldata;
	//private static PreparedStatement update;
	
	public static void init() {
		session = client.connect("127.0.0.1");
		ps = session.prepare("INSERT INTO top_movie (movieid, viewscnt, time) VALUES (?, ?, dateof(now()))");
		load = session.prepare("select viewscnt from top_movie");
		alldata = session.prepare("select * from top_movie");

	}

	public static void persist(String movieid, Integer count) {

		try {
			if (null == session) {
				init();
			}
			
			long existingcount = getExistingCount(movieid);
			long l = count + existingcount;
			
			Long viewscnt = new Long(l);
			BoundStatement bind = ps.bind(movieid, viewscnt);
			session.execute(bind);
			
			if(existingcount == 0) {
				System.out.println("Inserted the data for " + movieid +  "with value : "+l);
			}else{
				System.out.println("Updating the data for " + movieid+  "with value : "+l);
			}
			

		} catch (Exception e) {
			System.out.println(" Error while persisting the data in cassandra "
					+ e);
			e.printStackTrace();
		}
	}

	public static Map<String, Long> getTopMovies() {
		
		Map<String, Long> moviesmap = new HashMap<String, Long>();;
		try {
			if (null == session) {
				init();
			}
			ResultSet result = session.execute(alldata.bind());
			if( !result.isExhausted()) {
				List<Row> alldata = result.all();
				for(Row row : alldata) {
					moviesmap.put(row.getString("movieid"),  row.getLong("viewscnt"));
				}
			}
		} catch (Exception e) {
			System.out.println(" Exception while getting the count for movie " + e);
		}
		return moviesmap;
	}
	
	public static Map<String, Long> getSortedTopMovies() {
		Map<String, Long> moviesmap = getTopMovies();
		
		//System.out.println("UNSORTED MAP " +moviesmap);
		
        ValueComparator bvc =  new ValueComparator(moviesmap);
        TreeMap<String,Long> sorted_map = new TreeMap<String,Long>(bvc);
        sorted_map.putAll(moviesmap);
        
        System.out.println("SORTED MAP " + sorted_map);
		return sorted_map;
	}
	
	
	private static long getExistingCount(String movieid) {
		long value = 0;
		try {
			ResultSet result = session.execute(load.bind(movieid));
			if( !result.isExhausted()) {
				Row one = result.one();
				value = one.getLong("viewscnt");
				System.out.println(" Got the value for movie " + movieid + " value :" + value);
			}
		} catch (Exception e) {
			System.out.println(" Exception while getting the count for movie " + movieid);
		}
		return value;
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

	public static String getTopMoviesList() {
		Map<String, Long> map = getSortedTopMovies();

		List<Movies> moviesList = new ArrayList<Movies>();
		int count = 0;
		
		for(String movieId : map.keySet()) {
			Movies m = new Movies();
			m.setMovieId(movieId);
			m.setViewCount(map.get(movieId));
			String movInfo = null;
			try {
				movInfo = FluentHbaseClient.getMovieInfo(movieId);
				if(null != movInfo) {
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
			if(count > 5) {
				break;
			}
		}
		Gson g = new Gson();
		return g.toJson(moviesList);
	}

}


class ValueComparator implements Comparator<String> {
	Map<String, Long> map;
	ValueComparator(Map<String, Long> map) {
		this.map = map;
	}
	
	
	@Override
	public int compare(String o1, String o2) {
		int  i = (int) (map.get(o2) - map.get(o1));
		if(i == 0) 
			i++;
		return i;
	}
}
