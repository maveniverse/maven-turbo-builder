## simple-parallel Builder

Executes the build with maximum parallelism, modules are not waiting for all dependencies to be resolved as it's
assumed they are already built in the previous execution.

To set up the extension
```xml
<extensions>
    <extension>
        <!-- https://github.com/seregamorph/maven-surefire-cached -->
        <groupId>com.github.seregamorph</groupId>
        <artifactId>maven-simple-parallel-builder</artifactId>
        <version>0.2-SNAPSHOT</version>
    </extension>
</extensions>
```

Sample usage
```
./mvn clean install -DskipTests=true
# Don't forget to reduce the parallelism as all cores are loaded
./mvnw surefire:test -b simple-parallel -T8
```

It's also possible to define prioritized modules to be built first. Add `groupId:artifactId` of the modules
that are executed the longer than others.

Store the file at root of the project with name `.mvn/simple-parallel.json`. Sample file:
```json
{
  "//": "Prioritized modules for simple-parallel-builder",
  "prioritizedModules": [
    "com.acme:architecture-testing",
    "com.acme:api",
    "com.acme:utils"
  ]
}
```
