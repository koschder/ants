package testreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class TestReader {

	public static void main(String[] args) throws Exception {
		File logdir = new File("../Ants/logs/");
		final File[] replayFiles = logdir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("replay");
			}
		});
		Map<String, Integer> wins = new HashMap<String, Integer>();
		for (File replayFile : replayFiles) {
			String winner = getWinner(replayFile);
			if (wins.containsKey(winner))
				wins.put(winner, wins.get(winner) + 1);
			else
				wins.put(winner, 1);
		}
		System.out.println(wins);
	}

	private static String readString(InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	private static String getWinner(File replayFile) throws Exception {
		InputStream is = new FileInputStream(replayFile);
		String jsonTxt = readString(is);
		Object obj = JSONValue.parse(jsonTxt);
		JSONObject json = (JSONObject) obj;
		JSONArray ranks = (JSONArray) json.get("rank");
		JSONArray playerNames = (JSONArray) json.get("playernames");
		for (int i = 0; i < ranks.size(); i++) {
			if (ranks.get(i).equals(0L))
				return (String) playerNames.get(i);
		}
		return null;
	}
}
