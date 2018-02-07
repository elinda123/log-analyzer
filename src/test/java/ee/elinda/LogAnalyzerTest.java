package ee.elinda;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LogAnalyzerTest {

	private final LogAnalyzer logAnalyzer = new LogAnalyzer();

	@Test
	public void testLineConvertWhenUri() {
		String line = "2015-08-19 00:00:01,049 (http--0.0.0.0-28080-405) [] /checkSession.do in 187";
		LocalDateTime dateTime = LocalDateTime.of(2015, 8, 19, 0, 0, 1, 49000000);
		Request expected = new Request();
		expected.setOriginalRequestLogMessage(line);
		expected.setDateTime(dateTime);
		expected.setThreadId("(http--0.0.0.0-28080-405)");
		expected.setUserContext("[]");
		expected.setUri("/checkSession.do");
		expected.setDuration(187);

		Request actual = logAnalyzer.convert(line);

		assertThat(actual).isEqualToComparingFieldByField(expected);
	}

	@Test
	public void testLineConvertWhenRequestedResourceNameAndDataPayloadElements() {
		String line = "2015-08-19 00:04:45,259 (http--0.0.0.0-28080-405) [ASP CUST:CUS5T27233] getPermission 300445599231 in 32";
		LocalDateTime dateTime = LocalDateTime.of(2015, 8, 19, 0, 4, 45, 259000000);
		Request expected = new Request();
		expected.setOriginalRequestLogMessage(line);
		expected.setDateTime(dateTime);
		expected.setThreadId("(http--0.0.0.0-28080-405)");
		expected.setUserContext("[ASP CUST:CUS5T27233]");
		expected.setRequestedResourceName("getPermission");
		expected.setDataPayloadElementsForResource("300445599231");
		expected.setDuration(32);

		Request actual = logAnalyzer.convert(line);

		assertThat(actual).isEqualToComparingFieldByField(expected);
	}

	@Test
	public void testLineConvertCornerCaseA() {
		String line = "2015-08-19 00:00:11,219 (http--0.0.0.0-28080-3) [] getPermission  300407044035 in 35";
		LocalDateTime dateTime = LocalDateTime.of(2015, 8, 19, 0, 0, 11, 219000000);
		Request expected = new Request();
		expected.setOriginalRequestLogMessage(line);
		expected.setDateTime(dateTime);
		expected.setThreadId("(http--0.0.0.0-28080-3)");
		expected.setUserContext("[]");
		expected.setRequestedResourceName("getPermission");
		expected.setDataPayloadElementsForResource("300407044035");
		expected.setDuration(35);

		Request actual = logAnalyzer.convert(line);

		assertThat(actual).isEqualToComparingFieldByField(expected);
	}

	@Test
	public void testLineConvertCornerCaseB() {
		String line = "2015-08-19 00:00:13,088 (http--0.0.0.0-28080-85) [] getBroadbandSubscriptions CUS41Q3183 in 1000";
		LocalDateTime dateTime = LocalDateTime.of(2015, 8, 19, 0, 0, 13, 88000000);
		Request expected = new Request();
		expected.setOriginalRequestLogMessage(line);
		expected.setDateTime(dateTime);
		expected.setThreadId("(http--0.0.0.0-28080-85)");
		expected.setUserContext("[]");
		expected.setRequestedResourceName("getBroadbandSubscriptions");
		expected.setDataPayloadElementsForResource("CUS41Q3183");
		expected.setDuration(1000);

		Request actual = logAnalyzer.convert(line);

		assertThat(actual).isEqualToComparingFieldByField(expected);
	}

	@Test
	public void testLineConvertCornerCaseC() {
		String line = "2015-08-19 00:00:16,921 (http--0.0.0.0-28080-85) [USER:300500274042] getSubcriptionCampaigns 300500274042 true in 110";
		LocalDateTime dateTime = LocalDateTime.of(2015, 8, 19, 0, 0, 16, 921_000_000);
		Request expected = new Request();
		expected.setOriginalRequestLogMessage(line);
		expected.setDateTime(dateTime);
		expected.setThreadId("(http--0.0.0.0-28080-85)");
		expected.setUserContext("[USER:300500274042]");
		expected.setRequestedResourceName("getSubcriptionCampaigns");
		expected.setDataPayloadElementsForResource("300500274042");
		expected.setDuration(110);

		Request actual = logAnalyzer.convert(line);

		assertThat(actual).isEqualToComparingFieldByField(expected);
	}

	@Test
	public void testLocalDateTime() {
		Request request = new Request();
		LocalDateTime dateTime = LocalDateTime.of(2018, 2, 6, 15, 44, 33, 58300000);
		request.setDateTime(dateTime);
		LocalDateTime expected = LocalDateTime.of(2018, 2, 6, 15, 44, 0);

		LocalDateTime actual = logAnalyzer.localDateTime(request);

		assertThat(actual).isEqualTo(expected);
	}
}
