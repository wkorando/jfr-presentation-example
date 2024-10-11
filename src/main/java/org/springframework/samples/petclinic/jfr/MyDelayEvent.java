package org.springframework.samples.petclinic.jfr;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Name;

@Name("org.MyDelayEvent")
@Description("Event for tracking delays.")
@Category({ "Business", "Performance" })
public class MyDelayEvent extends Event {

	private int delayCounter;

	public MyDelayEvent() {
		super();
	}

	public void setDelayCounter(int delayCounter) {
		this.delayCounter = delayCounter;
	}

}
