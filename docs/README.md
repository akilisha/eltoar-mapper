## Lightweight object properties mapper

Fast and lightweight mapper for copying properties between data objects. The framework is also non-intrusive because it
does not require additional implementation of interfaces or extending base classes, and most importantly, it does not
use the reflection API.

Although there are already many different mapping scenarios that have been addressed, there might still be other
scenarios that have not yet been contemplated, and for such, please open an issue in GitHub and describe the usage
scenario.

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

> Running ASMifier on Windows

```bash
set ASM_JAR=<path to asm jar>\asm-9.6.jar
set ASM_UTIL_JAR=<path to asm-util jar>\asm-util-9.6.jar
set CLASSPATH=.;%ASM_JAR%;%ASM_UTIL_JAR%

java -cp %CLASSPATH% org.objectweb.asm.util.ASMifier java.lang.Runnable
```

Replace ```java.lang.Runnable``` in the example above with any class you wish to check out, for example

```bash
java -cp %CLASSPATH%;build\libs\obj-mapper-1.0-SNAPSHOT.jar org.objectweb.asm.util.ASMifier ^
com.akilisha.mapper.incubator.sample.WrappedUser
```
