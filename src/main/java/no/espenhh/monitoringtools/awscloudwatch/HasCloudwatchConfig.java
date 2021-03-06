package no.espenhh.monitoringtools.awscloudwatch;

import com.amazonaws.auth.AWSCredentials;

public interface HasCloudwatchConfig extends AWSCredentials {

    String getAWSAccessKeyId();

    String getAWSSecretKey();

    String getRegion();
}
