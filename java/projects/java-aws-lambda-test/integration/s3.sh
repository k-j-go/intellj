echo 'delete LambdaThree functions'
alias laws="aws --endpoint-url=http://localhost:4566"

laws lambda delete-function \
--function-name LambdaThree

echo 'deploy LambdaThree'
laws lambda create-function \
  --function-name LambdaThree \
  --runtime java8 \
  --handler com.azunitech.lambda.HandlerThree \
  --role arn:aws:iam::012345678901:role/DummyRole \
  --zip-file fileb://../target/java-aws-lambda-test.jar

echo 'test LambdaThree'
laws lambda invoke \
--function-name LambdaThree \
--cli-binary-format raw-in-base64-out \
--payload '{"payload":"your json", "transactionId":"tttt"}' response.json
cat response.json
