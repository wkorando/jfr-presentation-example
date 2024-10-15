# Taking off with JDK Flight Recorder

JDK Flight Recorder (JFR) is a profiling and diagnostic framework that built into the JVM (Hotspot).

This repo serves as an introduction to using JFR. 

The accompanying presentation for this repo can be found here: https://wkorando.github.io/presentations/taking-off-with-jfr/

The main body of this project is based on the Spring Boot Petclinic application you can view it here: https://github.com/spring-projects/spring-petclinic

## Using JFR

A key advantage of JFR is that it is built directly into the (Hotspot) JVM. This not only allows JFR to provide deep insight into your application, showing CPU and memory usage, GC behavior, and more. It also means that there is no additional configuration, beyond JVM arguments, you need to do to start using JFR. If you are on JDK 11 or later, you no longer need a commercial license as well! 

JFR, like it's namesake, flight recorders, are meant to have minimal impact and provide detailed diagnostic information, for improving performance, or figuring out the cause of failures. With default settings, JFR maintains a sub-1% overhead, this allows JFR to be used in production settings with mininmal impact on performance. 

### Starting JFR on Startup

To start JFR on JVM startup, include the `-XX:StartFlightRecording` JVM argument. `StartFlightRecording` takes several arguments, these are covered under the [Configuring JFR section](#configuring-jfr)

### Starting JFR on a Running Application


JFR can also be enabled on an already running JVM. This can be helpful for collecting diagnostic information on a JVM that's experiencing issues before shutting it down. To enable JFR on a running application, use the the `jcmd` tool like in this example:

```
jcmd <pid> JFR.start
```

https://docs.oracle.com/en/java/javase/23/docs/specs/man/jcmd.html

## Configuring JFR

JFR's behavior can be significantly influenced through how it's configured. There are three distinct levels for configuring JFR; JFR Flight Recording Settings, JFR Flight Recording Options, and JFR Settings. 

### JFR Flight Recording Settings

These options cover the overall behavior of JFR, including the name of the flight recording, for how long or how much recording data should be stored, and more. Key settings include:

* `filename`: The name of the file JFR data will be written to.
* `maxage`: The maximum age JFR will be retained. JFR data older than this setting will be overwritten.
* `maxsize`: The maximum amount of JFR data will be retained. When this limit is reached the oldest data will be overwritten first.
* `settings`: The settings file to be used. By default the provide `default.jfc` settings will be used located in [PROVIDE FIRLE PATH]. For more check the [settings section](#jfr-settings).
* `dumponexit`: Dump all unwritten data to the jfr file on JVM shutdown. Default `false`. Useful in a test setting.

There are many more options, to view them check this link under the `-XX:StartFlightRecording` section: https://docs.oracle.com/en/java/javase/23/docs/specs/man/java.html#advanced-runtime-options-for-java

### JFR Configuration Options

These options, set with `-XX:FlightRecorderOptions` influence the internal behavior of JFR. Typically the defaults should work in most cases, but might need to be changed to meet specific performance or operational needs. For details on all the options follow this link and look under the `-XX:FlightRecorderOptions` section: https://docs.oracle.com/en/java/javase/23/docs/specs/man/java.html#advanced-runtime-options-for-java

### JFR Settings

A JFR settings is an xml configuration primarily for the defining when and how JFR captures and handles events. Two settings files; default.jfc and profile.jfc, are provided located in: `${JAVA_HOME}/Contents/Home/lib/jfr/`. It's recommended not to modify these files. Instead as of JDK [VERSION] a new interactive wizard was added to the jfr tool:

```
jfr settings --interactive --oiutput [filename]
```

This tool can be used to generate a new settings file. Using the default option for all the question creates a file that's equivalent to `default.jfc`. 

A particularly useful setting for events is `treshold`. When using [custom events](#creating-custom-events). Often the need is to find instances when a segment of code has taken too long to execute. The threshold property can be so that only events that exceed that set value are captured by JFR, in the below example, only when a `org.MyEvent` takes great than a half-second to execute will it be captured by JFR. This can help greatly in filtering the captured information to stuff that you'd find useful. For more on writing and using custom jfr events, [go to that section](#creating-custom-events).

```
<event name="org.MyEvent">
  <setting name="enabled">true</setting>
  <setting name="threshold">500 ms</setting>
</event>
```

## Analyzing Results

There are a number of options for how to analyze the results of a JFR recording. From very broad analyzing the overall behavior of an application to vry specific, inspecting individual events. The best tool will depend on your specific needs. 

One important note, JFR is highly backwards compatible (JDK 7u40 or later), so while you might not be using the latest JDK version in production, you can still use the tools in the latest JDK version to analyze the results of a recording. 

### JFR Tool

The `jfr` tool is a command line tool that's included in the JDK. It provides many useful commands analyzing `.jfr` recordings. 

#### Summary

The `jfr summary [filename]` command lists all the events being captured by JFR, the number of times they were triggered, and the size in bytes of the data collected.

#### Print

The `jfr print [options] [values] [filename]` command provides a detailed view of the events that match the options and values specified. By default output is printed in a psuedo-json format, but providing the `--json` option returns formats the output in json. 

#### View

The `jfr view [view] [filename]` command was added in JDK [VERSION]. There are several dozen predefinedviews covering GC behavior, resource utilization, environment settings, and more. These views can be useful for providing more broader picture system analysis of an application from the command line that was previously only possible using a GUI tool like [JDK Mission Control](#jdk-mission-control). 

For more on the JFR tool view the official documentation on it: https://docs.oracle.com/en/java/javase/23/docs/specs/man/jfr.html

### JDK Mission Control
 
JDK Mission Control, as of the JDK 11 release, is an open source GUI that can be used for analyzing JFR files, configuring JFR in running JVMs, and other behavior like connecting to a JVM through the JMX interface that is beyond the scope of this project. 

https://docs.oracle.com/en/java/java-components/jdk-mission-control/

## Creating Custom Events

JFR comes with, as of JDK 23, over 180 pre-defined events, covering GC behavior, socket read/write, security events, and more. However in many cases an user would want to define their own events to better capture behavior that is happening within their application. 

/src/main/java/org/springframework/samples/petclinic/jfr/MyTransactionEvent.java

/src/main/java/org/springframework/samples/petclinic/jfr/MyDelayEvent.java

https://docs.oracle.com/en/java/javase/23/jfapi/flight-recorder-api-programmers-guide.pdf#_OPENTOPIC_TOC_PROCESSING_d129e1251

## JFR Streams

JFR added support for JFR Stream in JDK 14. JFR Streams provide programmatic access to JFR events. The options this can provide to developers is substantiall, from automated testing, to influencing application behavior based on events, to providing near realtime diagnostic information of an application. 


https://docs.oracle.com/en/java/javase/23/jfapi/flight-recorder-api-programmers-guide.pdf#_OPENTOPIC_TOC_PROCESSING_d129e2717
