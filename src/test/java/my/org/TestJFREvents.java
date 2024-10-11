package my.org;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.jfr.MyDelayEvent;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingStream;

public class TestJFREvents {

	@Test
	public void testDoStuff() throws Exception {
		SampleApplication sampleApplication = new SampleApplication();
		List<RecordedEvent> recordedEvents 
			= new ArrayList<>();
		try (RecordingStream rs = 
			new RecordingStream();) {
			rs.enable("jdk.FileRead");
			rs.onEvent("TestEvent", 
				e -> rs.close());
			rs.onEvent("jdk.FileRead", 
				recordedEvents::add);
			rs.startAsync();
			MyDelayEvent te = new MyDelayEvent();
			sampleApplication.doStuff();
			te.commit();
			rs.awaitTermination();
		}
		
	}
	
	class SampleApplication{
		public void doStuff() throws FileNotFoundException {
			FileReader fileReader = new FileReader("sampleJson.json");
			
//			fileReader.read(null)
		}
	}
}
