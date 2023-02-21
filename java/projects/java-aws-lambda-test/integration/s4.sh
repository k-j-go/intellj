echo 'delete LambdaFour functions'
alias laws="aws --endpoint-url=http://localhost:4566"

laws lambda delete-function \
--function-name LambdaFour

echo 'deploy LambdaFour'
laws lambda create-function \
  --function-name LambdaFour \
  --runtime java8 \
  --handler com.azunitech.lambda.HandlerFour \
  --role arn:aws:iam::012345678901:role/DummyRole \
  --zip-file fileb://../target/java-aws-lambda-test.jar

echo 'test LambdaFour'
laws lambda invoke \
--function-name LambdaFour \
--cli-binary-format raw-in-base64-out \
--payload '{"payload":"your json", "transactionId":"testm"}' response.json
cat response.json
