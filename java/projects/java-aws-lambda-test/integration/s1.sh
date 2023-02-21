echo 'delete LambdaOne functions'
alias laws="aws --endpoint-url=http://localhost:4566"

laws lambda delete-function \
--function-name LambdaOne

echo 'deploy LambdaOne'
laws lambda create-function \
  --function-name LambdaOne \
  --runtime java8 \
  --handler com.azunitech.lambda.HandlerOne \
  --role arn:aws:iam::012345678901:role/DummyRole \
  --zip-file fileb://../target/java-aws-lambda-test.jar

echo 'test LambdaOne'
laws lambda invoke \
--function-name LambdaOne \
--cli-binary-format raw-in-base64-out \
--payload '{"payload":"your json"}' response.json
cat response.json
