package digicom.rtpclient.test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Splittest {
	public static void main(String[] args) {
		String s = "5452^Children|Comedy|Romance^Look Who's Talking Now (1993)";
		String[] ss = s.split("\\^");
		System.out.println(ss[0]);
		System.out.println(ss[1]);
		System.out.println(ss[2]);
		
		Map<String, Long> map = new TreeMap<String, Long>();
		
		//1566=6, 1721=9, 1287=10, 3105=6, 2018=6, 1035=12, 2321=5, 938=15, 2804=11, 1907=4, 3408=9, 3186=6, 1=3, 588=4, 260=3, 1270=10, 2355=9, 661=17, 1029=3, 1028=3, 2762=4, 2791=6, 919=11, 720=10, 2398=14, 1545=9, 595=18, 48=9, 594=57, 914=9, 1962=3, 2797=5, 2692=3, 1961=3, 2687=6, 1022=4, 1212=12, 1207=2, 1836=4, 527=10}
		
		map.put("783", 4L);
		map.put("1197", null);
		map.put("2918", 13L);
		map.put("12112", 12L);
		map.put("1193", 27L);
		map.put("1097", 9L);
		map.put("150", 4L);
		map.put("745", 6L);
		map.put("2340", 10L);
		map.put("2294", 6L);
		
		printMap(map);

	}

	private static void printMap(Map<String, Long> map) {
		
		for(String k : map.keySet()) {
			System.out.println(k);
			System.out.println(map.get(k));
		}
		
	}
}
