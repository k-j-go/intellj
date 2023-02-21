#!/bin/bash

# -- > Create DynamoDb Table
echo Creating  DynamoDb \'messages\' table ...
echo $(awslocal dynamodb create-table --cli-input-json '{"TableName":"ItemInfo", "KeySchema":[{"AttributeName":"id","KeyType":"HASH"}], "AttributeDefinitions":[{"AttributeName":"id","AttributeType":"S"}],"BillingMode":"PAY_PER_REQUEST"}')

echo Creating  DynamoDb \'messages\' table ...
echo $(awslocal dynamodb create-table --cli-input-json '{"TableName":"Product", "KeySchema":[{"AttributeName":"productId","KeyType":"HASH"},{"AttributeName":"productName","KeyType":"RANGE"}], "AttributeDefinitions":[{"AttributeName":"productId","AttributeType":"S"}, {"AttributeName":"productName","AttributeType":"S"}],"BillingMode":"PAY_PER_REQUEST"}')

echo Creating  DynamoDb \'messages\' table ...
echo $(awslocal dynamodb create-table --cli-input-json '{"TableName":"MoviesAndActors", "KeySchema":[{"AttributeName":"actor","KeyType":"HASH"},{"AttributeName":"movie","KeyType":"RANGE"}], "AttributeDefinitions":[{"AttributeName":"actor","AttributeType":"S"}, {"AttributeName":"movie","AttributeType":"S"}],"BillingMode":"PAY_PER_REQUEST"}')


# --> List DynamoDb Tables
echo Listing tables ...
echo $(awslocal dynamodb list-tables)

echo Describe tables ...
awslocal dynamodb describe-table --table-name ItemInfo

awslocal dynamodb describe-table --table-name Product

awslocal dynamodb describe-table --table-name MoviesAndActors
