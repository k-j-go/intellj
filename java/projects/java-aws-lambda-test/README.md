#### Testing Lambda local
- MakeFile builds docker images which inclludes the tested handler, after start up using below to invoke the lambda
```shell script
make all
```

```makefile

```


```shell script
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{
                                                                                     "temperatureK": 281,
                                                                                     "windKmh": -3,
                                                                                     "humidityPct": 0.55,
                                                                                     "pressureHPa": 1020
                                                                                   }'
```

### Using Docker compose 
```shell script
docker-compose up
```

using the 
```shell script
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{
                                                                                     "temperatureK": 281,
                                                                                     "windKmh": -3,
                                                                                     "humidityPct": 0.55,
                                                                                     "pressureHPa": 1020
                                                                                   }'
```
to invoke the handler

```shell script
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '1'
```



(reference)[https://rieckpil.de/java-aws-lambda-container-image-support-complete-guide/]