## Mapping Scenarios

| LHS (Source - MVisitable) | RHS (Destination - Mappable)                                    |
|---------------------------|-----------------------------------------------------------------|
| basic type                | basic type with <br/> same name <br/> same basic type           |
| basic type                | basic type with <br/> same name <br/> different basic type      |
| basic type                | basic type with <br/> different name <br/> same basic type      |
| basic type                | basic type with <br/> different name <br/> different basic type |
| basic type                | property inside object type                                     |
| collection type           | collection type with same name                                  |
| collection type           | collection type with different name                             |

### Important observations

- When a different basic type is present, there needs to be a converter supplied

Running ASMifier on Windows

> set ASM_JAR=<path to asm jar >\asm-9.6.jar

> set ASM_UTIL_JAR=<path to asm-util jar>\asm-util-9.6.jar

> set CLASSPATH=.;%ASM_JAR%;%ASM_UTIL_JAR%

> java -cp %CLASSPATH% org.objectweb.asm.util.ASMifier java.lang.Runnable

Replace ```java.lang.Runnable``` above with any class you wish to check out, for example

> java -cp %CLASSPATH%;build\libs\obj-mapper-1.0-SNAPSHOT.jar org.objectweb.asm.util.ASMifier
> com.akilisha.mapper.incubator.sample.WrappedUser



