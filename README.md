# OTLP log sink

a tiny OTLP log receiver

* OTLP/HTTP only
* Protocol Buffers and JSON are supported
* gzip compression is supported

## Send a example record

```
cat src/test/resources/logs.json | curl -H "Content-Type: application/json" -s http://localhost:4318/v1/logs --data-binary @- -v
```

You'll see the following log output:

```
{"@timestamp":"2024-05-24T11:57:30.421Z","log.level": "INFO","message":"Received: Log[timestamp=2018-12-13T14:51:00.300Z, traceId=e41f0414517bf7cd37f35d370f6ebd07adf7f35dc50bad02, spanId=104135f41ec40b70b5075ef8, traceFlags=0, severity=Information, body=Example log record, attributes={int.attribute=10, array.attribute=[many, values], double.attribute=637.704, string.attribute=some string, map.attribute={some.map.key=some value}, boolean.attribute=true, my.scope.attribute=some scope attribute}, scope=my.library, resourceAttributes={service.name=my.service}]","ecs.version": "1.2.0","service.name":"log-sink","event.dataset":"log-sink","process.thread.name":"jetty-2","log.logger":"lol.maki.logsink.logs.LogsV1Controller","traceId":"424663af6ddca1070b3cbb9369a8b95d","spanId":"c9e44d14dc12a817"}
```

## How to rename the project

Edit `rewrite.yml`, then

```
./mvnw -U org.openrewrite.maven:rewrite-maven-plugin:run -Drewrite.activeRecipes=com.example.RenameProject
git add -A
git clean -fd
./mvnw clean spring-javaformat:apply test 
```

> [!NOTE]
> You can check the changes before running the above command by
> ```
> ./mvnw -U org.openrewrite.maven:rewrite-maven-plugin:dryRun -Drewrite.activeRecipes=com.example.RenameProject
> ```
> Check `./target/rewrite/rewrite.patch`