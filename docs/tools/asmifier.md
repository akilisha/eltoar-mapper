## Running ASMifier on Windows

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
