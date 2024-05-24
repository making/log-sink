# OTLP log sink

a tiny OTLP log receiver


## Send a example record

```
cat src/test/resources/logs.json | curl -H "Content-Type: application/json" -s http://localhost:4318/v1/logs --data-binary @- -v
```

You'll see the following log

```
ResourceLogs #0
Resource attributes:
  -> service.name : my.service
  SpanLogs #0
  Scope: my.library 1.0.0
  Scope attributes:
    -> my.scope.attribute : some scope attribute
    Log #0
      Timestamp: 1974-11-23T16:23:40.447Z
      Severity: Information
      Body:  Example log record
      Trace ID: e41f0414517bf7cd37f35d370f6ebd07adf7f35dc50bad02
      Span ID:  104135f41ec40b70b5075ef8
      Attributes:
      -> string.attribute : some string
      -> boolean.attribute : Bool(true)
      -> int.attribute : Int(10)
      -> double.attribute : Double(637.704)
      -> array.attribute : Array([many, values])
      -> map.attribute : KvList({some.map.key=some value})
```