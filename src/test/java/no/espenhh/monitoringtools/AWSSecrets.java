package no.espenhh.monitoringtools;

import com.amazonaws.auth.AWSCredentials;
import no.espenhh.monitoringtools.awscloudwatch.HasCloudwatchConfig;

public class AWSSecrets implements AWSCredentials, HasCloudwatchConfig {

    public static final String ACCESS_KEY = "YOUR_ACCESS_KEY";
    public static final String SECRET_KEY = "YOUR_SECRET_KEY";
    public static final String REGION = "eu-central-1";

    @Override
    public String getAWSAccessKeyId() {
        return ACCESS_KEY;
    }

    @Override
    public String getAWSSecretKey() {
        return SECRET_KEY;
    }

    @Override
    public String getRegion() {
        return REGION;
    }
}
