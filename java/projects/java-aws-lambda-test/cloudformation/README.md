### deploy cloudformation
```shell script
aws cloudformation create-stack \
  --endpoint-url http://localhost:4566 \
  --stack-name samplestack \
  --template-body file://sample.yaml \
  --profile localstack
```

