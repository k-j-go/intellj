- [ref for start](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-project-maven.html)
- [good](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html)

#### SDK for RUsT
[sdk-rust](https://docs.aws.amazon.com/sdk-for-rust/latest/dg/localstack.html)

#### Cloud Function
[cloud function](https://docs.localstack.cloud/user-guide/integrations/spring-cloud-function/#starting-a-new-project)
#### Start
[awssdk](https://github.com/awsdocs/aws-doc-sdk-examples)
[spring cloud function](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-project-maven.html)
[spring cloud for s3](https://github.com/mengjiann/aws-lambda-s3)
```shell
mvn -B archetype:generate \
 -DarchetypeGroupId=software.amazon.awssdk \
 -DarchetypeArtifactId=archetype-lambda -Dservice=s3 -Dregion=US_EAST_1 \
 -DgroupId=com.azunitech.pilot \
 -DartifactId=pilot2
```
##### Add 
```text
<dependency>
        <groupId>com.amazonaws</groupId>
        <artifactId>aws-java-sdk-bom</artifactId>
        <version>1.12.397</version>
        <type>pom</type>
        <scope>import</scope>
</dependency>
```



See [Deploying Serverless Applications](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-deploying.html) for more info.

##### Create Bucket
```shell
aws s3 mb s3://mybucket --endpoint-url http://localhost:4566 --region us-east-1
```


```shell
aws lambda create-function \
--endpoint-url http://localhost:4566 \
--function-name examplelambda \
--runtime java8 \
--handler com.azunitech.pilot.S3EventHandler \
--region us-east-1 \
--zip-file fileb://target/pilot.jar \
--role arn:aws:iam::12345:role/ignoreme
```
```shell
aws lambda update-function-code \
--endpoint-url http://localhost:4566 \
--function-name examplelambda \
--region us-east-1 \
--zip-file fileb://target/pilot.jar
```

```shell
aws s3api put-bucket-notification-configuration --bucket mybucket --region us-east-1 --notification-configuration file://s3hook.json --endpoint-url http://localhost:4566
```

```shell
aws s3 cp pom.xml s3://mybucket/samplefile.txt --endpoint-url http://localhost:4566 --region us-east-1
```


```shell
awslocal lambda invoke --function-name examplelambda — cli-binary-format raw-in-base64-out --payload file://payload.json output.txt
awslocal lambda invoke --function-name examplelambda --payload '{"name": "John", "age": 30}' output.txt
```



```shell
mvn archetype:generate \
-DarchetypeGroupId=software.amazon.awssdk \
-DarchetypeArtifactId=archetype-app-quickstart \
-DarchetypeVersion=2.16.1 \
-DnativeImage=true \
-DhttpClient=apache-client \
-Dservice=s3 \
-DgroupId=com.azunitech.mynativeimageapp \
-DartifactId=mynativeimageapp \
-DinteractiveMode=false
```

```shell
http -d https://start.spring.io/starter.zip type==maven-project \
language==java \
bootVersion==2.4.5 \
baseDir==springFunclambda \
groupId==com.azunitech.search \
artifactId==springFunclambda \
name==springFunclambda \
packageName==com.azunitech.search \
javaVersion==1.8 \
dependencies==web,webflux,okta,lombok
```