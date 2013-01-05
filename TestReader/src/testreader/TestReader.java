package testreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class TestReader {

	public static void main(String[] args) throws Exception {
		final File[] replayFiles = getReplayFiles();
		writeReport(replayFiles);
		if (getResultsPerBot(replayFiles).size() > 2)
			writeProfileSummary(replayFiles);
		else
			writeSummary(replayFiles);
	}

	private static void writeSummary(File[] replayFiles) throws Exception {
		Map<String, List<TestResult>> resultsPerBot = getResultsPerBot(replayFiles);
		String myBotName = getMyBotName(resultsPerBot.keySet());
		String otherBotName = getOtherBotName(resultsPerBot.keySet());
		List<TestResult> myTestResults = resultsPerBot.get(myBotName);
		List<TestResult> opponentResults = resultsPerBot.get(otherBotName);

		TestSummary summary = new TestSummary();
		for (int i = 0; i < myTestResults.size(); i++) {
			summary.games++;
			TestResult myRes = myTestResults.get(i);
			TestResult oppRes = opponentResults.get(i);
			if (myRes.status.equals("survived"))
				summary.timesSurvived++;
			if (myRes.rank.equals("0")) {
				if (oppRes.rank.equals("0"))
					summary.draws++;
				else
					summary.wins++;
			} else
				summary.losses++;
			summary.totalPointsWon += Integer.valueOf(myRes.score);
			summary.totalPointsLost += Integer.valueOf(oppRes.score);
		}

		System.out.println(summary);
		final FileWriter fileWriter = new FileWriter(myBotName + " vs "
				+ otherBotName + " Summary .csv");
		fileWriter.write(summary.toString());
		fileWriter.close();
	}

	private static void writeProfileSummary(File[] replayFiles)
			throws Exception {
		Map<String, List<TestResult>> resultsPerBot = getResultsPerBot(replayFiles);
		List<String> botNames = new ArrayList<String>(resultsPerBot.keySet());
		List<List<TestResult>> testResults = new ArrayList<List<TestResult>>();
		for (String bot : botNames) {
			testResults.add(resultsPerBot.get(bot));
		}
		StringBuilder sb = new StringBuilder("Profile,");
		sb.append(TestSummary.SUMMARY_HEADER);
		for (String bot : botNames) {
			sb.append(bot).append(",");
			List<TestResult> myTestResults = resultsPerBot.get(bot);
			Map<String, List<TestResult>> opponentResults = getOpponentTestResults(
					resultsPerBot, bot);
			TestSummary summary = new TestSummary();
			for (int i = 0; i < myTestResults.size(); i++) {
				summary.games++;
				TestResult myRes = myTestResults.get(i);
				if (myRes.status.equals("survived"))
					summary.timesSurvived++;

				if (myRes.rank.equals("0")) {
					boolean draw = false;
					for (List<TestResult> res : opponentResults.values()) {
						if (res.get(i).rank.equals("0")) {
							summary.draws++;
							draw = true;
							break;
						}
					}
					if (!draw)
						summary.wins++;
				} else
					summary.losses++;

				summary.totalPointsWon += Integer.valueOf(myRes.score);
				for (List<TestResult> res : opponentResults.values()) {
					summary.totalPointsLost += Integer
							.valueOf(res.get(i).score);
				}
			}
			summary.appendSummaryLine(sb);
		}

		System.out.println(sb);
		final FileWriter fileWriter = new FileWriter("Profile Summary .csv");
		fileWriter.write(sb.toString());
		fileWriter.close();
	}

	private static Map<String, List<TestResult>> getOpponentTestResults(
			Map<String, List<TestResult>> resultsPerBot, String myBot) {
		Map<String, List<TestResult>> results = new HashMap<String, List<TestResult>>();
		for (Entry<String, List<TestResult>> entry : resultsPerBot.entrySet()) {
			if (!entry.getKey().equals(myBot))
				results.put(entry.getKey(), entry.getValue());
		}
		return results;
	}

	private static String getMyBotName(Set<String> keySet) {
		for (String string : keySet) {
			if (string.startsWith("MyBot-"))
				return string;
		}
		return null;
	}

	private static String getOtherBotName(Set<String> keySet) {
		for (String string : keySet) {
			if (!string.startsWith("MyBot-"))
				return string;
		}
		return null;
	}

	private static Map<String, List<TestResult>> getResultsPerBot(
			File[] replayFiles) throws Exception {
		Map<String, List<TestResult>> resultsPerBot = new HashMap<String, List<TestResult>>();
		for (int i = 0; i < replayFiles.length; i++) {
			File replayFile = replayFiles[i];
			JSONObject json = readJson(replayFile);
			String[] players = getBotNames(json);
			String[] ranks = getBotRanks(json);
			String[] scores = getBotScores(json);
			String[] status = getBotStatus(json);

			for (int j = 0; j < players.length; j++) {
				if (i == 0) {
					resultsPerBot.put(players[j], new ArrayList<TestResult>());
				}
				resultsPerBot.get(players[j]).add(
						new TestResult(ranks[j], scores[j], status[j]));
			}
		}
		return resultsPerBot;
	}

	private static void writeReport(final File[] replayFiles)
			throws FileNotFoundException, IOException {
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

	private static File[] getReplayFiles() {
		File logdir = new File("../Ants/logs/");
		final File[] replayFiles = logdir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("replay");
			}
		});
		return replayFiles;
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
			report.append("Status(").append(name).append(")").append(",");
		}
		report.append("\n");
	}

	private static void writeLine(StringBuffer report, JSONObject json) {
		String[] ranks = getBotRanks(json);
		String[] scores = getBotScores(json);
		String[] status = getBotStatus(json);
		for (int i = 0; i < ranks.length; i++) {
			report.append(ranks[i]).append(",");
			report.append(scores[i]).append(",");
			report.append(status[i]).append(",");
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

	private static String[] getBotStatus(JSONObject json) {
		JSONArray status = (JSONArray) json.get("status");
		return toStringArray(status);
	}

	private static String[] toStringArray(JSONArray jsonArray) {
		String[] strings = new String[jsonArray.size()];
		for (int i = 0; i < strings.length; i++) {
			strings[i] = String.valueOf(jsonArray.get(i));
		}
		return strings;
	}

	static class TestResult {
		String rank;
		String score;
		String status;

		TestResult(String rank, String score, String status) {
			super();
			this.rank = rank;
			this.score = score;
			this.status = status;
		}
	}

	static class TestSummary {
		static final String SUMMARY_HEADER = "Wins,Losses,Draws,Total Points won, Total Points lost, Times survived, Games\n";
		int wins = 0;
		int losses = 0;
		int draws = 0;
		int totalPointsWon = 0;
		int totalPointsLost = 0;
		int timesSurvived = 0;
		int games = 0;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(SUMMARY_HEADER);
			appendSummaryLine(sb);
			return sb.toString();
		}

		void appendSummaryLine(StringBuilder sb) {
			sb.append(wins).append(",").append(losses).append(",")
					.append(draws).append(",").append(totalPointsWon)
					.append(",").append(totalPointsLost).append(",")
					.append(timesSurvived).append(",").append(games)
					.append("\n");
		}

	}
}
