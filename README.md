# AWS Cloudwatch

Repoet inneholder:

1. Eksempler på ulike måter å integrere mot AWS Cloudwatch på
2. En fungerende og vel uttestet (på NSB) EventLogger som både logger til fil og til Cloudwatch

### Kom i gang

1. Begynn med å leke deg i testene
2. Opprett en AWS-konto, og så går du inn i IAM og lager en bruker som kun har rettigheten `CloudWatchFullAccess`
3. Gjør nødvendige endringer i filen `AWSSecrets` for å matche din bruker
4. Sjekk først ut testen `SimplestPossibleExample`, deretter kan du se på `EventLoggerExamples`, og om ønskelig kan du også titte på `MoreAdvancedExamples` :-)

### AWS Dokumentasjon

Info om AWS Cloudwatch:
https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/WhatIsCloudWatch.html

Spesifikk info om å publisere sine egne metrikker:
https://docs.aws.amazon.com/AmazonCloudWatch/latest/monitoring/publishingMetrics.html

Javadoc for Cloudwatch Java-SDKen:
https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cloudwatch/AmazonCloudWatchClient.html