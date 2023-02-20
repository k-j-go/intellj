- [referecne Code](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/javav2/example_code)
- [refer](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-project-maven.html)
```
http -d https://start.spring.io/starter.zip type==maven-project \
language==java \
bootVersion==2.4.5 \
baseDir==learn \
groupId==com.azunitech.search \
artifactId==learn \
name==learn \
packageName==com.azunitech.search \
javaVersion==1.8 \
dependencies==web,webflux,okta,lombok
```

```shell
mvn -B archetype:generate \
 -DarchetypeGroupId=software.amazon.awssdk \
 -DarchetypeArtifactId=archetype-lambda -Dservice=s3 -Dregion=US_EAST_1 \
 -DgroupId=com.azunitech.pilot \
 -DartifactId=pilot
```