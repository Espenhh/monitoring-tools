package no.espenhh.monitoringtools.eventlogger;

import no.espenhh.monitoringtools.AWSSecrets;
import no.espenhh.monitoringtools.awscloudwatch.CloudwatchClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

@Ignore
public class EventLoggerExamples {

    private static final int PERCENT_CHANCE_OF_SUCCESS = 90;

    private static final Random RANDOM = new Random();

    private EventLogger eventLogger;

    @Before
    public void setup() {
        CloudwatchClient cloudwatchClient = new CloudwatchClient(new AWSSecrets());
        eventLogger = new EventLogger(cloudwatchClient, "test-events", 10);
        eventLogger.startScheduledEventPublish();
    }


    /**
     * Use case: kun grafing i AWS
     *
     * Hver gang "spørringen kjøres", så "ticker" vi det – det vil si at hendelsen registreres og det
     * vil fremstå som "én gang" i grafene i AWS
     */
    @Test
    public void simple_events() throws InterruptedException {
        while (true) {
            if (success()) {
                eventLogger.tick("QUERY_SUCCESS");
            } else {
                eventLogger.tick("QUERY_FAILED");
            }

            Thread.sleep(10);
        }
    }


    /**
     * Use case: grafing i AWS, og logging av detaljer i vanlig "java fil-logg"
     *
     * Hver gang brukeren logger inn, så vil det "tickes" som en hendelse i grafene i AWS.
     * I tillegg vil det komme et logginnslag for hendelsen med detaljene man legger på
     */
    @Test
    public void events_with_audit_logging() throws InterruptedException {
        while (true) {

            if (success()) {
                eventLogger.tickAndLog("LOGIN_SUCCESS",
                        "userId " + fakeUserId()
                );
            } else {
                eventLogger.tickAndLog("LOGIN_FAILED",
                        "userId " + fakeUserId()
                );
            }

            Thread.sleep(1000);
        }
    }


    /**
     * Use case: økt antall i grafen pr. kall
     *
     * Si at du f.eks skal tracke antall kroner du selger for. Da vil du ikke at hvert kall skal
     * bare trackes som "1" i loggene i AWS. Da bruker du funksjonen som tar inn en int.
     */
    @Test
    public void events_with_value_and_audit_logging() throws InterruptedException {
        while (true) {
            int price = RANDOM.nextInt(1000);

            if (success()) {
                eventLogger.tickAndLog("PURCHACE_SUCCESSFUL_KRONER", price,
                        "userId " + fakeUserId(),
                        "price " + price + "kr"
                );
            } else {
                eventLogger.tickAndLog("PURCHACE_FAILED_KRONER",
                        "userId " + fakeUserId(),
                        "price " + price + "kr"
                );
            }

            Thread.sleep(1000);
        }
    }

    private boolean success() {
        return RANDOM.nextInt(100) < PERCENT_CHANCE_OF_SUCCESS;
    }

    private String fakeUserId() {
        return String.valueOf(RANDOM.nextInt(1000));
    }

}