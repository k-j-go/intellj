### lambda error hanlding
- change code with if input records size is not 2 throws exception
```test
    if (event.size() != 2) {
            throw new InputLengthException("Input must be an array that contains 2 numbers.");
    }
```

- using local lambda test
```shell script
make all
```

- in another command 
```shell script
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" \
-d '{  "temperatureK": 281, "windKmh": -3, "humidityPct": 0.55, "pressureHPa": 1020 }'
```

```shell script
awslocal lambda invoke \
--function-name JavaLambda \
--cli-binary-format raw-in-base64-out \
--payload '{"temperatureK": 281,"windKmh": -3}' response.json

cat response.json
```


### For testing error state
- Inpput 4 items will cause error 
```shell script
awslocal lambda invoke   \
--function-name JavaLambda   \
--cli-binary-format raw-in-base64-out  \
--payload '{"key1": "value1", "key2": "value2", "key3": "value3"}' output.txt
```

- try with 2 items
```shell script
awslocal lambda invoke   \
  --function-name JavaLambda   \
      --cli-binary-format raw-in-base64-out  \
          --payload '{"key1": "value1", "key2": "value2"}' output.txt
```

```shell script
aws lambda create-function \
  --function-name JavaLambda \
  --runtime java8 \
  --handler com.azunitech.lambda.JavaLambda \
  --role arn:aws:iam::012345678901:role/DummyRole \
  --zip-file fileb://target/java-aws-lambda-test.jar
```