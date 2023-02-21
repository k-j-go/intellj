echo 'delete JabaLambda functions'
awslocal lambda delete-function \
--function-name JavaLambda

echo 'deploy JabaLambda'
awslocal lambda create-function \
  --function-name JavaLambda \
  --runtime java8 \
  --handler com.azunitech.lambda.JavaLambda \
  --role arn:aws:iam::012345678901:role/DummyRole \
  --zip-file fileb://../target/java-aws-lambda-test.jar

echo 'test LambdaOne'
awslocal lambda invoke \
--function-name JavaLambda \
--cli-binary-format raw-in-base64-out \
--payload '{
           "temperatureK": 281,
           "windKmh": -3,
           "humidityPct": 0.55,
           "pressureHPa": 1020
         }' response.json
cat response.json

