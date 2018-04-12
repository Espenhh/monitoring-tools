package no.espenhh.monitoringtools.eventlogger;

import no.espenhh.monitoringtools.awscloudwatch.CloudwatchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class EventLogger {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    private ConcurrentMap<String, DoubleAdder> counts = new ConcurrentHashMap<>();

    private CloudwatchClient cloudwatchClient;
    private String namespace;
    private int secondsBetweenSync;

    public EventLogger(CloudwatchClient cloudwatchClient, String namespace, int secondsBetweenSync) {
        this.cloudwatchClient = cloudwatchClient;
        this.namespace = namespace;
        this.secondsBetweenSync = secondsBetweenSync;
    }

    public void startScheduledEventPublish() {
        SCHEDULER.scheduleWithFixedDelay(this::publish, 0, secondsBetweenSync, TimeUnit.SECONDS);
    }

    public void tickAndLog(String event, String... logappends) {
        tickAndLog(event, 1, logappends);
    }

    public void tickAndLog(String event, double count, String... logappends) {
        performLog(event, logappends);
        performTick(event, count);
    }

    public void tick(String event) {
        tick(event, 1);
    }

    public void tick(String event, double count) {
        performTick(event, count);
    }

    private void performLog(String event, String... logappends) {
        LOG.info("AUDIT EVENT - [" + event + "] - " + Stream.of(logappends).map(s -> "[" + s + "]").collect(joining(" - ")));
    }

    private void performTick(String event, double count) {
        counts.computeIfAbsent(event, s -> new DoubleAdder()).add(count);
    }

    private void publish() {
        long before = System.currentTimeMillis();

        ConcurrentMap<String, DoubleAdder> mapToProcess = counts;
        counts = new ConcurrentHashMap<>();

        List<CloudwatchClient.CloudwatchStatistic> statistics = mapToProcess.entrySet().stream()
                .map(e -> new CloudwatchClient.CloudwatchStatistic(e.getKey(), e.getValue().doubleValue()))
                .collect(toList());
        cloudwatchClient.sendStatistics(namespace, statistics);

        LOG.info(String.format("Sent %d events to cloudwatch in %dms", mapToProcess.size(), (System.currentTimeMillis() - before)));
    }
}
