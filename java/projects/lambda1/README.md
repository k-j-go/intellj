- [reference](https://codetinkering.com/localstack-s3-lambda-example-docker/)
- [aws](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html#get-started-setup)
- [project](https://github.com/awsdocs/aws-doc-sdk-examples)

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
aws s3 mb s3://mybucket --endpoint-url http://localhost:4566
```

```shell
aws lambda create-function \
--endpoint-url http://localhost:4566 \
--function-name examplelambda \
--runtime java8 \
--handler com.azunitech.search.BucketHandler \
--region us-east-1 \
--zip-file fileb://target/lambda1-0.0.1-SNAPSHOT.jar \
--role arn:aws:iam::12345:role/ignoreme
```

```shell
aws s3api put-bucket-notification-configuration --bucket mybucket --notification-configuration file://s3hook.json --endpoint-url http://localhost:4566
```

```shell
aws lambda create-function \
--endpoint-url http://localhost:4566 \
--function-name examplelambdaput \
--runtime java8 \
--handler com.azunitech.search.BucketHandler \
--region us-east-1 \
--zip-file fileb://target/lambda1-0.0.1-SNAPSHOT.jar \
--role arn:aws:iam::12345:role/ignoreme
```

```shell
aws s3api put-bucket-notification-configuration --bucket mybucket --notification-configuration file://s3hook_put.json --endpoint-url http://localhost:4566
```


```shell
aws s3 cp samplefile.txt s3://mybucket/samplefile.txt --endpoint-url http://localhost:4566
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