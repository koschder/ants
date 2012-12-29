package testreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStream;

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
		StringBuffer report = new StringBuffer("Round,");
		for (int i = 0; i < replayFiles.length; i++) {
			File replayFile = replayFiles[i];
			JSONObject json = readJson(replayFile);
			if (i == 0)
				writeHeader(report, json);
			report.append(i).append(",");
			writeLine(report, json);
		}
		System.out.println(report);
		final FileWriter fileWriter = new FileWriter("Testreport.csv");
		fileWriter.write(report.toString());
		fileWriter.close();
	}

	private static JSONObject readJson(File replayFile)
			throws FileNotFoundException {
		InputStream is = new FileInputStream(replayFile);
		String jsonTxt = readString(is);
		Object obj = JSONValue.parse(jsonTxt);
		return (JSONObject) obj;
	}

	private static void writeHeader(StringBuffer report, JSONObject json) {
		String[] names = getBotNames(json);
		for (String name : names) {
			report.append("Rank(").append(name).append(")").append(",");
			report.append("Score(").append(name).append(")").append(",");
			report.append("Ants(").append(name).append(")").append(",");
		}
		report.append("\n");
	}

	private static void writeLine(StringBuffer report, JSONObject json) {
		String[] ranks = getBotRanks(json);
		String[] scores = getBotScores(json);
		// String[] ants = getBotAnts(json);
		for (int i = 0; i < ranks.length; i++) {
			report.append(ranks[i]).append(",");
			report.append(scores[i]).append(",");
			report.append("?").append(",");
		}
		report.append("\n");
	}

	private static String readString(InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	private static String[] getBotNames(JSONObject json) {
		JSONArray playerNames = (JSONArray) json.get("playernames");
		return toStringArray(playerNames);
	}

	private static String[] getBotRanks(JSONObject json) {
		JSONArray ranks = (JSONArray) json.get("rank");
		return toStringArray(ranks);
	}

	private static String[] getBotScores(JSONObject json) {
		JSONArray scores = (JSONArray) json.get("score");
		return toStringArray(scores);
	}

	private static String[] toStringArray(JSONArray jsonArray) {
		String[] strings = new String[jsonArray.size()];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = String.valueOf(jsonArray.get(i));
		}
		return strings;
	}
	// private static String[] getBotAnts(JSONObject json) {
	// JSONArray playerNames = (JSONArray) json.get("playernames");
	// return (String[]) playerNames.toArray();
	// }

}
