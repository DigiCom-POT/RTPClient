package digicom.rtpclient.flume;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import com.google.gson.Gson;

import digicom.rtpclient.dsc.Movies;


public class FluentHbaseClient {

	public static String userRecommendMovie(String userID) {
		List<String> getUserPreferences;
		try {
			getUserPreferences = getUserPreferences(userID);
			List<String> movies = getRecommendedMovie(getUserPreferences);
			
			List<Movies> mlist = new ArrayList<Movies>();
			if(null != movies) {
				for(String mov : movies) {
					Movies m = new Movies();
					String[] split = mov.split("\\^");
					m.setMovieId(split[0]);
					if(split.length > 1)
						m.setMovieGenre(split[1]);
					if(split.length > 2)
						m.setMovieName(split[2]);
					mlist.add(m);
				}
			}
			Gson g = new Gson();
			return g.toJson(mlist);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String similarMovieRecommend(String movieID) {
		List<String> movmovlist;
		try {
			movmovlist = getSimilarMovies(movieID);
			List<String> movies = getRecommendedMovie(movmovlist);
			List<Movies> mlist = new ArrayList<Movies>();
			if(null != movies) {
				for(String mov : movies) {
					Movies m = new Movies();
					String[] split = mov.split("\\^");
					m.setMovieId(split[0]);
					if(split.length > 1)
						m.setMovieGenre(split[1]);
					if(split.length > 2)
						m.setMovieName(split[2]);
					mlist.add(m);
				}
			}
			Gson g = new Gson();
			return g.toJson(mlist);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	
	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		String userId = "100";
		String movieId = "6659";
		
		System.out.println(userRecommendMovie(userId));
		
		System.out.println(similarMovieRecommend(movieId));
		/*// On Item Based recommendation
		List<String> getUserPreferences = getUserPreferences(userId);
		//System.out.println(getUserPreferences);
		
		List<String> movies = getRecommendedMovie(getUserPreferences);
		for(String mov : movies) {
			System.out.println(mov);
		}
		System.out.println("=============================================");
		List<String> m = new ArrayList<String>();
		m.add(movieId);
		List<String> movm = getRecommendedMovie(m);
		for(String mov : movm) {
			System.out.println("Movies Similar to : "  +mov);
		}
		
		
		List<String> movmovlist = getSimilarMovies(movieId);
		List<String> movmov = getRecommendedMovie(movmovlist);
		for(String mov : movmov) {
			System.out.println(mov);
		}*/
	}

	public static List<String> getSimilarMovies(String movieId) throws ClientProtocolException, IOException {
		List<String> items = new ArrayList<String>();
		String response = Request
				.Get("http://localhost:8500/movmovrec/".concat(movieId)).addHeader("Accept", "application/json")
				.execute().returnContent().asString();
		Gson json = new Gson();
		UserRecommendation rec = json.fromJson(response,
				UserRecommendation.class);
		String[] as = decode(rec.getRow()[0].getCell()[0].get$()).split(
				"\\s*\\),\\(\\s*");
		System.out.println("-----"
				+ decode(rec.getRow()[0].getCell()[0].get$()));
		for (String s : as) {
			items.add(s.split(",")[1]);
		}
		return items;
	}

	private static List<String> getRecommendedMovie(List<String> getUserPreferences)
			throws ClientProtocolException, IOException {

		List<String> items = new ArrayList<String>();
		for (String movieID : getUserPreferences) {
			String response = Request
					.Get("http://localhost:8500/moviesinfo/".concat(movieID)).addHeader("Accept", "application/json")
					.execute().returnContent().asString();
			Gson json = new Gson();
			UserRecommendation rec = json.fromJson(response,
					UserRecommendation.class);
			items.add(movieID + "^" + decode(rec.getRow()[0].getCell()[0].get$()) + "^"
					+ decode(rec.getRow()[0].getCell()[1].get$())) ;
		}
		return items;
	}

	private static List<String> getUserPreferences(String userId) throws ClientProtocolException, IOException {

		String response = Request
				.Get("http://localhost:8500/moviesrecommend/".concat(userId).concat("/d"))
				.execute().returnContent().asString();
		response = response.replace("[", "");
		response = response.replace("]", "");
		List<String> items = null;
		items = Arrays.asList(response.split("\\s*,\\s*"));
		List<String> filter = new ArrayList<String>();
		for (String s : items) {
			String[] n = s.split(":");
			filter.add(n[0]);
		}
		System.out.println(filter);
		return filter;
	}
	
	// for decoding
	public static String decode(String s) {
	    return StringUtils.newStringUtf8(Base64.decodeBase64(s));
	}
	
	// for encoding
	public static String encode(String s) {
	    return Base64.encodeBase64String(StringUtils.getBytesUtf8(s));
	}

	
	
	public static String getMovieInfo(String movieId)
			throws ClientProtocolException, IOException {
			String response = Request
					.Get("http://localhost:8500/moviesinfo/".concat(movieId)).addHeader("Accept", "application/json")
					.execute().returnContent().asString();
			Gson json = new Gson();
			UserRecommendation rec = json.fromJson(response,
					UserRecommendation.class);
			if(null != rec && !rec.equals("")) {
				return decode(rec.getRow()[0].getCell()[0].get$()) + "^"
						+ decode(rec.getRow()[0].getCell()[1].get$());
			}
			return null;
	}
	
	
}
