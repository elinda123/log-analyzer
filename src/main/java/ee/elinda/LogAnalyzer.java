package ee.elinda;

import ee.elinda.utils.StringsParser;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;
import static java.lang.System.nanoTime;
import static java.lang.System.out;
import static java.time.LocalDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

class LogAnalyzer {
	private String fileName;
	private int until;
	private String[] arguments;
	private List<Request> requests;
	private static long startTime;

	LogAnalyzer() {
	}

	public static void main(String[] arguments) {
		startTime = nanoTime();
		new LogAnalyzer(arguments);
	}

	private LogAnalyzer(String[] arguments) {
		this.arguments = arguments;
		if (arguments.length == 1 && arguments[0].contains("-h")) {
			out.println("Please insert log filename and number of resources you want to see.\n" +
				"For example 'timing.log 10' \uD83D\uDE03");
			printProgramDuration();
			return;
		}

		if (arguments.length != 2) {
			out.println("Please insert 2 input arguments or '-h' for help information.");
			printProgramDuration();
			return;
		}

		initializeVariables();
		readDataFromFile();
		requests.forEach(out::println);
		drawHistogram();
		printProgramDuration();
	}


	private void drawHistogram() {
		Map<LocalDateTime, Integer> numberOfRequests = countRequestsByMinutes(requests);
		Histogram histogram = new Histogram();
		out.println("\nHistogram:");
		histogram.draw(numberOfRequests);
	}

	private void readDataFromFile() {
		StringsParser parser = new StringsParser();
		List<String> data = parser.parse(new File(fileName));
		requests = data.stream()
			.map(this::convert)
			.sorted(comparing(Request::getDuration, reverseOrder()))
			.limit(until)
			.collect(Collectors.toList());
	}

	private void initializeVariables() {
		fileName = arguments[0];
		try {
			until = Integer.parseInt(arguments[1]);
		} catch (Exception e) {
			out.println("Second argument should be a number: " + e.getMessage());
		}
	}

	private Map<LocalDateTime, Integer> countRequestsByMinutes(List<Request> requests) {
		Set<LocalDateTime> dateTimes = new TreeSet<>();
		for (Request request : requests) {
			dateTimes.add(localDateTime(request));
		}

		Map<LocalDateTime, Integer> result = new TreeMap<>();
		for (LocalDateTime localDateTime : dateTimes) {
			for (Request request : requests) {
				if (localDateTime.compareTo(localDateTime(request)) == 0) {
					result.putIfAbsent(localDateTime, 0);
					result.put(localDateTime, result.get(localDateTime) + 1);
				}
			}
		}
		return result;
	}


	public Request convert(String line) {
		Request result = new Request();
		result.setOriginalRequestLogMessage(line);
		String[] split = line.split(" ");
		String dateTime = split[0] + " " + split[1];
		DateTimeFormatter formatter = ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
		result.setDateTime(parse(dateTime, formatter));
		result.setThreadId(split[2]);
		result.setUserContext(line.substring(line.indexOf('['), line.indexOf(']') + 1));
		boolean isUriRequest = split.length == 7;
		boolean isResourceRequest = split.length >= 8;
		if (isUriRequest) {
			String[] split1 = line.split("] ")[1].split(" ");
			result.setUri(split1[0]);
			result.setDuration(parseLong(split1[2]));
		} else if (isResourceRequest) {
			line = line.replace("  ", " ");
			String[] split1 = line.split("] ")[1].split(" ");
			result.setRequestedResourceName(split1[0]);
			result.setDataPayloadElementsForResource(split1[1]);
			result.setDuration(parseLong(split1[split1.length - 1]));
		}
		return result;
	}

	protected LocalDateTime localDateTime(Request request) {
		LocalDateTime temp = request.getDateTime();
		return LocalDateTime.of(temp.getYear(), temp.getMonth(), temp.getDayOfMonth(), temp.getHour(), temp.getMinute(), 0);
	}

	private void printProgramDuration() {
		out.println(String.format("\nProgram duration: %.2fms", (nanoTime() - startTime) / 1_000_000.0));
	}
}
