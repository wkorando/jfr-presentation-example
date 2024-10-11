package org.springframework.samples.petclinic.jfr;

import jdk.jfr.*;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Name;

@Name("org.MyTransactionEvent")
@Description("Event for tracking transactions.")
@Category({ "Business", "Transaction", "PII" })
public class MyTransactionEvent extends Event {

	private int transactionID;
	private long executionTimeInMillis;

	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}

	public void setExecutionTimeInMillis(long executionTimeInMillis) {
		this.executionTimeInMillis = executionTimeInMillis;
	}

}
