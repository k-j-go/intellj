[node.js express to lambda](https://bitbucket.org/blog/deploy-an-express-js-app-to-aws-lambda-using-the-serverless-framework)
[servless deploy to localstack](https://docs.localstack.cloud/user-guide/integrations/serverless-framework/)
[serverless](https://www.serverless.com/framework/docs/getting-started)
```shell
    npm i serverless-http
    npm install -g serverless
    npm i -D serverless-localstack
```

#### Add following
```test
plugins:
  - serverless-localstack

custom:
  localstack:
    stages:
      - local
```

#### deploy
```shell
serverless deploy --stage local
```
```shell
serverless remove
```