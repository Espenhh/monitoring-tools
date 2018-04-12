package no.espenhh.monitoringtools.awscloudwatch;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.*;
import no.espenhh.monitoringtools.AWSSecrets;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;

public class CloudwatchClientExamples {

    private AmazonCloudWatch amazonCloudWatch;

    @Before
    public void setup() {
        amazonCloudWatch = AmazonCloudWatchClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new AWSSecrets()))
                .withRegion(AWSSecrets.REGION)
                .build();
    }

    /**
     * Dette eksempelet grupperer statistikken inn i ulike dimensjoner,
     * slik at man kan grave seg ned i de dataene i ettertid.
     */
    @Test
    public void example_with_dimensions() {
        postWithDimensions("free-user", "youth");
        postWithDimensions("free-user", "senior");
        postWithDimensions("paid-user", "youth");
        postWithDimensions("paid-user", "senior");
    }

    private void postWithDimensions(String userType, String userAge) {
        Collection<Dimension> dimensions = Arrays.asList(
                new Dimension().withName("user-type").withValue(userType),
                new Dimension().withName("user-age").withValue(userAge)
        );

        MetricDatum metricDatum = new MetricDatum()
                .withMetricName("login")
                .withTimestamp(new Date())
                .withUnit(StandardUnit.Count)
                .withValue(1D)
                .withDimensions(dimensions);

        postToCloudwatch("dimensions-test", singletonList(metricDatum));
    }


    /**
     * Dette eksempelet sender inn et statistikk-sett
     *
     * Det betyr at du selv kan regne ut gjennomsnitt lokalt, og så sende det over til
     * AWS hvert minutt og dermed likevel få grafer på responstider etc. uten å måtte
     * sende over alle de enkle statistikkene
     *
     * PS: for å få percentiler må man likevel sende over enkeltverdier... (??)
     */
    @Test
    public void example_with_statistics() {
        StatisticSet statisticSet = new StatisticSet()
                .withMinimum(20D)
                .withMaximum(2900D)
                .withSampleCount(45D)
                .withSum(50098D);
        MetricDatum metricDatum = new MetricDatum()
                .withMetricName("response-times")
                .withTimestamp(new Date())
                .withUnit(StandardUnit.Milliseconds)
                .withStatisticValues(statisticSet);

        postToCloudwatch("statistics-test", singletonList(metricDatum));
    }


    private void postToCloudwatch(String namespace, List<MetricDatum> metricData) {
        PutMetricDataRequest request = new PutMetricDataRequest()
                .withNamespace(namespace)
                .withMetricData(metricData);
        amazonCloudWatch.putMetricData(request);
    }

}