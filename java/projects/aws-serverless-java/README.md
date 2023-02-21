- [reference](https://codetinkering.com/localstack-s3-lambda-example-docker/)
- [aws](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html#get-started-setup)
- [project](https://github.com/awsdocs/aws-doc-sdk-examples)
- [aws service](https://github.com/aws/?q=&type=&language=java)
- [example1](https://medium.com/javarevisited/spring-boot-with-aws-s3-bucket-from-zero-to-useful-c0895ab26aaa)
- [example2](https://mmarcosab.medium.com/how-about-s3-bucket-and-localstack-b0816bab452a)

#### Tool for kill a app with port
```shell
npm install --global fkill-cli
```

#### Tool httpie
```shell
brew install httpie
```


##### Create Bucket
```shell
aws s3 mb s3://mybucket --endpoint-url http://localhost:4566 --region us-east-1
```

```shell
aws s3 mb s3://logbucket --endpoint-url http://localhost:4566 --region us-east-1

```

```shell
aws lambda create-function \
--endpoint-url http://localhost:4566 \
--function-name examplelambda \
--runtime java8 \
--handler com.example.serverless.BucketHandler \
--region us-east-1 \
--zip-file fileb://target/aws-serverless-java-0.0.1-SNAPSHOT-aws.jar \
--role arn:aws:iam::12345:role/ignoreme
```

```shell
aws lambda update-function-code \
--endpoint-url http://localhost:4566 \
--function-name examplelambda \
--region us-east-1 \
--zip-file fileb://target/aws-serverless-java-0.0.1-SNAPSHOT-aws.jar

```

```shell
aws s3api put-bucket-notification-configuration --bucket mybucket --region us-east-1 --notification-configuration file://s3hook.json --endpoint-url http://localhost:4566
```

```shell
aws s3 cp pom.xml s3://mybucket/samplefile.txt --endpoint-url http://localhost:4566 --region us-east-1
```

```shell
http -d https://start.spring.io/starter.zip type==maven-project \
language==java \
bootVersion==2.4.5 \
baseDir==lambda2 \
groupId==com.azunitech.search \
artifactId==lambda2 \
name==lambda2 \
packageName==com.azunitech.search \
javaVersion==1.8 \
dependencies==web,webflux,okta,lombok

```