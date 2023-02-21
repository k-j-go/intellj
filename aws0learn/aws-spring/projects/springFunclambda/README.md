[ref](https://github.com/spring-cloud/spring-cloud-function/blob/3.2.x/spring-cloud-function-samples/function-sample-aws/pom.xml)

#### Create project
```shell
http -d https://start.spring.io/starter.zip type==maven-project \
language==java \
bootVersion==2.4.5 \
baseDir==springFunclambda1 \
groupId==com.azunitech.search \
artifactId==springFunclambda1 \
name==springFunclambda1 \
packageName==com.azunitech.search \
javaVersion==1.8 \
dependencies==web,webflux,okta,lombok
```
```text
handleRequest
```
```shell
awslocal lambda create-function \
--function-name springFunclambda \
--runtime java8 \
--handler org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest \
--region us-east-1 \
--zip-file fileb://target/springFunclambda-0.0.1-SNAPSHOT-aws.jar \
--role arn:aws:iam::12345:role/ignoreme
```

```shell
aws s3 mb s3://mybucket --endpoint-url http://localhost:4566 --region us-east-1
aws s3api put-bucket-notification-configuration --bucket mybucket --region us-east-1 --notification-configuration file://s3hook.json --endpoint-url http://localhost:4566
aws s3 cp pom.xml s3://mybucket/samplefile.txt --endpoint-url http://localhost:4566 --region us-east-1
```


```text
  <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.springframework</groupId>
                            <artifactId>spring-context-indexer</artifactId>
                            <version>${spring-framework.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <dependencies>
                    <dependency>
                        <groupId>org.springframework.boot.experimental</groupId>
                        <artifactId>spring-boot-thin-layout</artifactId>
                        <version>1.0.22.RELEASE</version>
                    </dependency>
                </dependencies>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <configuration>
                    <createDependencyReducedPom>false</createDependencyReducedPom>
                    <!-- there is a bug in scs artifactory which makes maven-dependency-plugin request fail most of the time because it cannot find the dependency.
                         It uses the build timestamp instead of the snapshot extension of the artifact name. When not using a classifier this issue does not occur. -->
                    <!-- different than spring cloud examples: do not attach the shaded artifact this making shaded artifact this module's the main artifact -->
                    <!-- artifact containing only the classes will not be installed or deployed -->
                    <shadedArtifactAttached>false</shadedArtifactAttached>
                    <!-- different than spring cloud examples: do not append a classifier to the artifact and its file name -->
                    <!-- <shadedClassifierName>aws</shadedClassifierName>-->
                </configuration>
            </plugin>
        </plugins>
    </build>
```