echo 'delete LambdaTwo functions'
alias laws="aws --endpoint-url=http://localhost:4566"

laws lambda delete-function \
--function-name LambdaTwo

echo 'deploy LambdaTwo'
laws lambda create-function \
  --function-name LambdaTwo \
  --runtime java8 \
  --handler com.azunitech.lambda.HandlerTwo \
  --role arn:aws:iam::012345678901:role/DummyRole \
  --zip-file fileb://../target/java-aws-lambda-test.jar

echo 'test LambdaTwo'
laws lambda invoke \
--function-name LambdaTwo \
--cli-binary-format raw-in-base64-out \
--payload '{"payload":"your json", "transactionId":"tttt"}' response.json
cat response.json
