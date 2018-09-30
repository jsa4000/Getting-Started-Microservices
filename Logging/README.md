
# LOGGING

## Introduction

[The Twelve Factors](https://12factor.net/)

## Treat logs as event streams (twelve-factor)

**Logs** provide visibility into the *behavior* of a running app. In server-based environments they are commonly written to a file on disk (a “logfile”); but this is *only* an output format.

Logs are the **stream** of aggregated, time-ordered **events** collected from the output streams of all running processes and backing services. Logs in their raw form are typically a ``text`` format with one event per line (though backtraces from exceptions may span multiple lines). Logs have **no** fixed beginning or end, but flow continuously as long as the app is operating.

```txt
21:32:10.311 [main] DEBUG com.baeldung.logback.LogbackTests - Logging message: This is a String
21:32:10.316 [main] DEBUG com.baeldung.logback.LogbackTests - Going to divide 42 by 0
21:32:10.316 [main] ERROR com.baeldung.logback.LogbackTests - Error dividing 42 by 0
java.lang.ArithmeticException: / by zero
  at com.baeldung.logback.LogbackTests.givenParameters_ValuesLogged(LogbackTests.java:64)
...
```

A **twelve-factor** app never concerns itself with routing or storage of its output stream. It should **not** attempt to write to or manage logfiles. Instead, each running process writes its event stream, unbuffered, to **stdout**. During local development, the developer will view this stream in the foreground of their terminal to observe the app’s behavior.

In staging or production deploys, each process’ stream will be captured by the execution environment, collated together with all other streams from the app, and routed to one or more **final** destinations for viewing and long-term archival. These archival destinations are not visible to or configurable by the app, and instead are **completely** managed by the execution environment. Open-source **log routers** (such as ``Logstash`` and ``Fluentd``) are available for this purpose.

The **event** stream for an app can be routed to a file, or watched via realtime tail in a terminal. Most significantly, the stream can be sent to a log indexing and **analysis** system such as Splunk, or a general-purpose data warehousing system such as Hadoop/Hive. These systems allow for great power and flexibility for introspecting an app’s behavior over time, including:

- **Finding** specific events in the past.
- Large-scale **graphing** of trends (such as requests per minute).
- Active **alerting** according to user-defined heuristics (such as an alert when the quantity of errors per minute exceeds a certain threshold).

# ELK/EFK Stack (Elasticsearch - Logstash/Fluentd - Kibana)

**ELK** is the acronym for three open source projects: **Elasticsearch**, **Logstash**, and **Kibana**.

- **Elasticsearch** is a search and analytics engine.
- **Logstash** is a server‑side data processing pipeline that ingests data from multiple sources simultaneously, transforms it, and then sends it to a "stash" like Elasticsearch.
- **Kibana** lets users visualize data with charts and graphs in Elasticsearch.

![elk stack](images/elk-stack.png)

Together, these different open-source products are most commonly used for **centralized** logging in IT environments (though there are many more use cases for the ELK Stack including business intelligence, security and compliance, and web analytics). Logstash collects and parses logs, and then Elasticsearch indexes and stores the information. Kibana then presents the data in visualizations that provide actionable insights into one’s environment.

![Kibana Dashboard](images/kibana-dashboard.png)

# Implementation

