##### Get stream consumer arg
```shell
awslocal kinesis describe-stream --stream-name test --query "StreamDescription.StreamARN" --output text
```

##### register stream consumer
```shell
awslocal kinesis register-stream-consumer --consumer-name con1 --stream-arn arn:aws:kinesis:us-east-1:000000000000:stream/test
```