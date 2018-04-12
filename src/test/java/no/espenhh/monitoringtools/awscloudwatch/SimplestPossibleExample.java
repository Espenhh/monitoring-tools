package no.espenhh.monitoringtools.awscloudwatch;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import no.espenhh.monitoringtools.AWSSecrets;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;

import static java.util.Collections.singletonList;

@Ignore
public class SimplestPossibleExample {

    /**
     * Enkleste mulige eksempel
     * <p>
     * Dette er all koden som skal til for å sende et målepunkt til AWS
     */
    @Test
    public void simplest_possible_example() {

        //
        // 1. oppretter en CloudWatch klasse
        //
        AmazonCloudWatch amazonCloudWatch = AmazonCloudWatchClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new AWSSecrets()))
                .withRegion(AWSSecrets.REGION)
                .build();

        //
        // 2. lager ett enkelt målepunkt for en metrikk vi skal sende inn
        //

        MetricDatum metricDatum = new MetricDatum()
                .withMetricName("user-login")
                .withTimestamp(new Date())
                .withUnit(StandardUnit.Count)
                .withValue(1D);

        //
        // 3. lager request med en namespace metrikken skal havne i
        //

        PutMetricDataRequest request = new PutMetricDataRequest()
                .withNamespace("simple-test")
                .withMetricData(singletonList(metricDatum));

        //
        // 4. sender over metrikken til AWS...
        //

        amazonCloudWatch.putMetricData(request);
    }

}
