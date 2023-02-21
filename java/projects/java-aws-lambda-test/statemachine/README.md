### Here we play with state-machine with lamdba
#### deploy test lambda
- go to integration, execure deply.sh, it will deploy JavaLambda function to local stack
```json 
{
    "FunctionName": "JavaLambda",
    "FunctionArn": "arn:aws:lambda:us-west-2:000000000000:function:JavaLambda",
    "Runtime": "java8",
    "Role": "arn:aws:iam::012345678901:role/DummyRole",
    "Handler": "com.azunitech.lambda.JavaLambda",
    "CodeSize": 14044,
    "Description": "",
    "Timeout": 3,
    "LastModified": "2021-07-17T19:59:01.755+0000",
    "CodeSha256": "tsavdAAQCAeKQ5Ko30XmQkDNwURoFr4YHc1+uJ+o55U=",
    "Version": "$LATEST",
    "VpcConfig": {},
    "TracingConfig": {
        "Mode": "PassThrough"
    },
    "RevisionId": "1ab47297-4be1-41fa-9216-64e9565596a3",
    "State": "Active",
    "LastUpdateStatus": "Successful",
    "PackageType": "Zip"
}
```
- We got 
```text
   "FunctionName": "JavaLambda",
   "FunctionArn": "arn:aws:lambda:us-west-2:000000000000:function:JavaLambda",
```

- Replace the state machine task FunctionName with above FunctionName and Resource with FunctionArn
```json
{
  "StartAt":"CallLambda",
  "States":{
    "CallLambda":{
      "Type":"Task",
      "Resource":"arn:aws:states:::lambda:invoke",
      "Parameters":{
        "FunctionName":"MyFunction"
      },
      "End":true
    }
  }
}
```
- after replace the values, it looks like this
```json
{
  "StartAt":"CallLambda",
  "States":{
    "CallLambda":{
      "Type":"Task",
      "Resource":"arn:aws:lambda:us-west-2:000000000000:function:JavaLambda",
      "Parameters":{
        "FunctionName":"JavaLambda"
      },
      "End":true
    }
  }
}
```


- push state machine to localstack
```shell script
aws stepfunctions \
--endpoint http://localhost:8083 \
create-state-machine \
--definition file://state-machine.json \
--name "StepsStateMachine" \
--role-arn "arn:aws:iam::012345678901:role/DummyRole"
```
- we get state machine config
```json
{
    "stateMachineArn": "arn:aws:states:us-east-1:123456789012:stateMachine:StepsStateMachine",
    "creationDate": "2021-07-17T13:13:05.945000-07:00"
}
```
- we created steps.sh for delete, deploy and start step funtions
```shell script
sh steps.sh
```

