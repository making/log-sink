# OTLP log sink

a tiny OTLP log receiver


## Send a example record

```
cat src/test/resources/logs.json | curl -H "Content-Type: application/json" -s http://localhost:4318/v1/logs --data-binary @- -v
```

You'll see the following log output:

```
ResourceLogs #0
Attributes:
	-> service.name	: "my.service"
	SpanLogs #0
	Scope: my.library	1.0.0
	Attributes:
		-> my.scope.attribute	: "some scope attribute"
		Log #0
			Timestamp	: 2018-12-13T14:51:00.300Z
			Severity	: Information
			Body    	: "Example log record"
			Trace ID	: e41f0414517bf7cd37f35d370f6ebd07adf7f35dc50bad02
			Span ID 	: 104135f41ec40b70b5075ef8
			Flags   	: 0
			Attributes	: 
				-> string.attribute	: "some string"
				-> boolean.attribute	: true
				-> int.attribute	: 10
				-> double.attribute	: 637.704
				-> array.attribute	: ["many", "values"]
				-> map.attribute	: {some.map.key="some value"}
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