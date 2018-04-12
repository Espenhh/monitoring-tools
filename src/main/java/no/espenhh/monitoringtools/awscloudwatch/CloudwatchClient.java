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

    public final AmazonCloudWatch amazonCloudWatch;

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
        postToCloudwatchRaw(
                namespace,
                statistics.stream()
                        .map(CloudwatchStatistic::toMetricsDatum)
                        .collect(toList())
        );
    }

    public void postToCloudwatchRaw(String namespace, List<MetricDatum> metricDatumList) {
        PutMetricDataRequest request = new PutMetricDataRequest();
        request.setNamespace(namespace);
        request.setMetricData(metricDatumList);

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
            MetricDatum metricDatum = new MetricDatum();
            metricDatum.setMetricName(key);
            metricDatum.setTimestamp(new Date());
            metricDatum.setUnit(unit);
            metricDatum.setValue(value);
            return metricDatum;
        }
    }
}
