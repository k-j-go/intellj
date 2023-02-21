#/bin/bash
alias laws="aws --endpoint-url=http://localhost:4566"

echo 'delete state machine'
aws stepfunctions \
--endpoint http://localhost:8083 \
delete-state-machine \
--state-machine-arn \
arn:aws:states:us-east-1:123456789012:stateMachine:StepsStateMachine



echo 'deploying state machine'
aws stepfunctions \
--endpoint http://localhost:8083 \
create-state-machine \
--definition file://state-machine.json \
--name "StepsStateMachine" \
--role-arn "arn:aws:iam::012345678901:role/DummyRole"

echo 'start the state machine'
aws stepfunctions \
--endpoint http://localhost:8083 \
start-execution \
--state-machine arn:aws:states:us-east-1:123456789012:stateMachine:StepsStateMachine \
--name test \
--input '{"payload":"your json"}'

sleep 10
echo 'check running state'
aws stepfunctions \
--endpoint http://localhost:8083 \
describe-execution \
--execution-arn arn:aws:states:us-east-1:123456789012:execution:StepsStateMachine:test

