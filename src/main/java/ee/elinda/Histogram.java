package ee.elinda;

import java.time.LocalDateTime;
import java.util.Map;

import static java.lang.System.out;
import static org.apache.commons.lang3.StringUtils.repeat;

class Histogram {
	public void draw(Map<LocalDateTime, Integer> numberOfRequests) {
		numberOfRequests.forEach((key, value) -> {
			int minute = key.getMinute();
			out.println(minute + " min " + repeat('-', value));
		});
	}
}
