#### Testing lambda in localstack
##### Deploy four lambdas
```shell script
sh s1.sh
sh s2.sh
sh s3.sh
sh s4.sh
```
- using aws command to list
```shell script
aws --endpoint-url=http://localhost:4566 \
lambda list-functions
```

```shell script
awslocal lambda invoke \
--function-name JavaLambda \
--cli-binary-format raw-in-base64-out \
--payload file://payload.json out.txt
```

```shell script
aws lambda invoke \                                                                                                                                                            
--function-name ${FUNCTION_NAME} \
--payload $(echo '{ "foo": "bar" }' | base64 -w 0 ) \
response.json
```