package org.springframework.samples.petclinic.jfr;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JfrController {

	record MessageRecord(long randNumber, String date) {
		public String toString() {
			return String.format("The current time: %s and the retrieve random value is: %d", date, randNumber);
		}
	};

	@GetMapping("/jfr/doStuff")
	public String doStuff() {
		RandomGenerator randGenerator = RandomGenerator.getDefault();
		for (int i = 0; i < 100000000; i++) {

			long nextLong = randGenerator.nextLong();
			LocalDateTime rightNow = LocalDateTime.now();
			MessageRecord messageRecord = new MessageRecord(nextLong, rightNow.toString());
			System.out.println(messageRecord.toString());
		}

		return "completed";
	}

	@GetMapping("/jfr/processTransactions")
	public String processTransactions() {
		RandomGenerator randGenerator = RandomGenerator.getDefault();
		List<Thread> transactions = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			int x = i;
			Thread thread = Thread.ofVirtual().start(() -> {
				MyTransactionEvent event = new MyTransactionEvent();
				event.begin();
				long nextLong = randGenerator.nextLong(100L, 1000L);
				task(x, nextLong);
				event.end();
				event.setTransactionID(x);
				event.setExecutionTimeInMillis(nextLong);
				event.commit();
			});
			transactions.add(thread);
		}
		transactions.stream().forEach(t -> {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return "completed";
	}

	public static String task(int id, long executionTime) {
		executionTime(executionTime);
		String result = String.format("""
				{
					"value" : %d
				}
				""", id);
		System.out.println("Result of task: " + result);
		return result;
	}

	/**
	 * Sleeps the execution thread for the passed in value.
	 * 
	 * @param executionTime - how long the thread should sleep in milliseconds.
	 */
	private static void executionTime(long executionTime) {
		try {
			TimeUnit.MILLISECONDS.sleep(executionTime);
		} catch (InterruptedException e) {
			// Just eating this exception, it shouldn't happen and isn't relevant to the
			// demo.
			e.printStackTrace();
		}
	}
}
