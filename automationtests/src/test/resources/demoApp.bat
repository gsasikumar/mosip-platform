"C:\Program Files\Java\jdk1.8.0_171/bin/java" -Dspring.cloud.config.label=QA_IDA -Dspring.profiles.active=testqa -Dspring.cloud.config.uri=http://104.211.212.28:51000 -Djava.net.useSystemProxies=true -agentlib:jdwp=transport=dt_socket,server=y,address=4000,suspend=n -jar "C:\Users\M1048373\.m2\repository\io\mosip\authentication\authentication-partnerdemo-service\0.12.4\authentication-partnerdemo-service-0.12.4.jar"