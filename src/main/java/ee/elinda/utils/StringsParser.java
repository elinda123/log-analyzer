package ee.elinda.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class StringsParser {
	public List<String> parse(File file) {
		try {
			return Files.readAllLines(Paths.get(file.getPath()))
				.stream()
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(toList());
		} catch (IOException e) {
			System.out.println("Read file error: " + e.getMessage());
			return emptyList();
		}
	}
}
