APP?=java-aws-lambda-custom-image

clean:
	docker stop $(docker ps -aq) || true
	docker rm $(docker ps -aq) || true
	docker rmi APP -f || true

build: clean
	mvn clean
	mvn package -DakipTests

images: build
	docker build -t  java-aws-lambda-custom-image .

lambda: images
	docker run --rm -v ~/.aws-lambda-rie:/aws-lambda -p 9000:8080 \
        --entrypoint /aws-lambda/aws-lambda-rie \
        java-aws-lambda-custom-image:latest \
        /opt/java/openjdk/bin/java -cp '/function/*' com.amazonaws.services.lambda.runtime.api.client.AWSLambda com.azunitech.lambda.HandlerInteger

all: clean build images lambda curl





