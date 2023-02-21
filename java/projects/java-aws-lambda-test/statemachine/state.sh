#/bin/bash
alias laws="aws --endpoint-url=http://localhost:4566"

echo 'start the state machine'
aws stepfunctions \
--endpoint http://localhost:8083 \
start-execution \
--state-machine arn:aws:states:us-east-1:123456789012:stateMachine:StepsStateMachine \
--name $1 \
--input file://payload.json

sleep 10
echo 'check running state name'
aws stepfunctions \
--endpoint http://localhost:8083 \
describe-execution \
--execution-arn arn:aws:states:us-east-1:123456789012:execution:StepsStateMachine:$1

