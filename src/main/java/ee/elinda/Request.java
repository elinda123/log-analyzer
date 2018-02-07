package ee.elinda;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
class Request {
	LocalDateTime dateTime;
	String threadId;
	String userContext;
	String uri;
	long duration;
	String requestedResourceName;
	String dataPayloadElementsForResource;
	String originalRequestLogMessage;

	@Override
	public String toString() {
		return originalRequestLogMessage;
	}
}



