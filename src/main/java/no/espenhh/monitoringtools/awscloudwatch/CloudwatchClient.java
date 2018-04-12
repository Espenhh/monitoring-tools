package no.espenhh.monitoringtools.awscloudwatch;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class CloudwatchClient {

    private final AmazonCloudWatch amazonCloudWatch;

    public CloudwatchClient(HasCloudwatchConfig cloudwatchConfig) {
        amazonCloudWatch = AmazonCloudWatchClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(cloudwatchConfig))
                .withRegion(cloudwatchConfig.getRegion())
                .build();
    }

    public void sendStatistics(String namespace, List<CloudwatchStatistic> statistics) {
        if (statistics == null || statistics.isEmpty()) {
            return;
        }
        Lists.partition(statistics, 15)
                .parallelStream()
                .forEach(l -> postToCloudwatch(namespace, l));
    }

    private void postToCloudwatch(String namespace, List<CloudwatchStatistic> statistics) {
        PutMetricDataRequest request = new PutMetricDataRequest()
                .withNamespace(namespace)
                .withMetricData(
                        statistics.stream()
                                .map(CloudwatchStatistic::toMetricsDatum)
                                .collect(toList())
                );
        amazonCloudWatch.putMetricData(request);
    }

    public static class CloudwatchStatistic {
        private String key;
        private Double value;
        private StandardUnit unit;

        public CloudwatchStatistic(String key, Double value, StandardUnit unit) {
            this.key = key;
            this.value = value;
            this.unit = unit;
        }

        public CloudwatchStatistic(String key, Double value) {
            this(key, value, StandardUnit.Count);
        }

        public MetricDatum toMetricsDatum() {
            return new MetricDatum()
                    .withMetricName(key)
                    .withTimestamp(new Date())
                    .withUnit(unit)
                    .withValue(value);
        }
    }
}
