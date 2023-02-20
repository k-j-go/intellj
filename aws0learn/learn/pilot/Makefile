clean:
	docker stop $$(docker ps -aq ) || true
	docker rm $$(docker ps -aq ) || true
	docker-compose down
build: clean
	mvn install
test:
	mvn install
	aws s3 mb s3://mybucket --endpoint-url http://localhost:4566 --region us-east-1
	aws lambda create-function \
	--endpoint-url http://localhost:4566 \
	--function-name examplelambda \
	--runtime java8 \
	--handler com.azunitech.pilot.S3EventHandler \
	--region us-east-1 \
	--zip-file fileb://target/pilot.jar \
	--role arn:aws:iam::12345:role/ignoreme
	aws s3api put-bucket-notification-configuration --bucket mybucket --region us-east-1 --notification-configuration file://s3hook.json --endpoint-url http://localhost:4566
	aws s3 cp pom.xml s3://mybucket/samplefile.txt --endpoint-url http://localhost:4566 --region us-east-1
update:
	mvn install
	aws lambda update-function-code \
	--endpoint-url http://localhost:4566 \
	--function-name examplelambda \
	--region us-east-1 \
	--zip-file fileb://target/pilot.jar
	aws s3 cp pom.xml s3://mybucket/samplefile.txt --endpoint-url http://localhost:4566 --region us-east-1

