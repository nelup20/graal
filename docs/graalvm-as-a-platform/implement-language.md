---
layout: docs
link_title: Implement Your Language
permalink: /graalvm-as-a-platform/implement-language/
redirect_from: /docs/graalvm-as-a-platform/implement-language/
toc_group: graalvm-as-a-platform
---

# Introduction to SimpleLanguage

To implement your own language, get started by extending an existing language such as SimpleLanguage.
[SimpleLanguage](https://github.com/graalvm/simplelanguage) is a demonstration language built using the [Language API](https://www.graalvm.org/truffle/javadoc/com/oracle/truffle/api/package-summary.html).
The SimpleLanguage project provides a showcase on how to use the Language APIs for writing your own language.
It aims to use most of the available [Truffle language implementation framework](../../truffle/docs/README.md) (henceforth "Truffle") features, and documents their use extensively with inline source documentation.

The SimpleLanguage demonstration language is licensed under the [Universal Permissive License (UPL)](https://opensource.org/licenses/UPL).

### Prerequisites

* [Maven](https://maven.apache.org/download.cgi) available in your system.
* [GraalVM](https://www.graalvm.org/downloads/) installed.
* The [`mx` tool](https://github.com/graalvm/mx) for the development of GraalVM projects (see below).

## Get Started

1. Clone the SimpleLanguage repository by running:
    ```shell
    git clone https://github.com/graalvm/simplelanguage
    ```
2. Execute `mvn package` from the SimpleLanguage folder to build the language. Before that, verify that `native-image` is available with your GraalVM installation to avoid a build failure:
    ```shell
    cd simplelanguage
    ```
    ```shell
    native-image --version
    ```
    ```shell
    mvn package
    ```
    The command builds the `slnative` executable in the `simplelanguage/native` directory and the `sl-component.jar` language component which can be later installed into GraalVM using the [GraalVM Updater](../reference-manual/graalvm-updater.md) tool.

   You can disable the SimpleLanguage native executable build during the packaging phase by running:
    ```shell
    export SL_BUILD_NATIVE=false
    mvn package
    ```

3. Run SimpleLanguage from the project root folder:
    ```shell
    ./sl language/tests/HelloWorld.sl
    ```

4. To see assembly code for the compiled functions, run:
    ```shell
    ./sl -disassemble language/tests/SumPrint.sl
    ```

## IDE Setup

The [Truffle framework](../../truffle/docs/README.md) provides language-agnostic infrastructure to realize standard IDE features by providing additional APIs.
If you would like to experiment with your language and get the benefits of an IDE, consider importing SimpleLanguage as an example.

### Eclipse

The SimpleLanguage teaching project has been tested with Eclipse Neon.2 Release 4.6.2, and Eclipse Oxygen.3A. To import the project folder to the desirable Eclipse environment:
1. Open Eclipse with a new workspace.
2. Install the `m2e` and `m2e-apt` plugins from the Eclipse marketplace (Help -> Eclipse Marketplace).
3. Finally, import the `SimpleLanguage` project from File -> Import -> Maven -> Existing Maven Projects -> browse to the SimpleLanguage folder -> Finish.

### NetBeans

NetBeans provides GUI support for debugging arbitrary languages. To upload SimpleLanguage to NetBeans interface, go to File -> Open Project -> select `simplelanguage` folder -> check Open Required Projects -> open Project.


### IntelliJ IDEA

The SimpleLanguage project was tested with IntelliJ IDEA. Open IntelliJ IDEA and, from the main menu bar, select  File -> Open -> Navigate to and select the `simplelanguage` folder -> Press OK. All dependencies will be included automatically.

## Dump Graphs

To investigate performance issues, consider using the [Ideal Graph Visualizer (IGV)](../tools/ideal-graph-visualizer.md) on top of GraalVM. 
IGV is developed to view and inspect intermediate representation graphs – a language-independent intermediate representation (IR) between the source language and the machine code, generated by the compiler. IGV is free to use.

1. Set up the [`mx` tool](https://github.com/graalvm/mx/blob/master/README.md) on your computer.
    - Clone the `mx` repository by running:
    ```shell
    git clone https://github.com/graalvm/mx.git
    ```
    - Clone the [Graal repository](https://github.com/oracle/graal.git) to your work directory:
    ```shell
    git clone https://github.com/oracle/graal.git
    ```
    - Add `mx` to the `PATH` environment variable:
    ```shell
    export PATH="/path/to/mx:$PATH"
    ```
    - To check whether the installation was successful, run the command:
    ```shell
    mx --version 
    ```

2. Launch IGV with `mx`:
    ```shell
    mx -p graal/compiler igv
    ```
3. Execute the following from the SimpleLanguage root folder to dump graphs to IGV:
    ```shell
    ./sl -dump language/tests/SumPrint.sl
    ```

This dumps compiler graphs in the IGV format over the network to an IGV process listening on [127.0.0.1:4445](127.0.0.1:4445). 
Once the connection is made, you are able to see the graphs in the Outline window. 
Open a specific graph, search for nodes by name, ID, or by `property=value` data, and all matching results will be shown. 
Learn more [here](../tools/ideal-graph-visualizer.md).

## Debug

To start debugging the SimpleLanguage implementation with a Java debugger, pass the `-debug` option to the command-line launcher of your program:
```shell
./sl -debug language/tests/HelloWorld.sl
```

Then attach a Java remote debugger (like Eclipse) on port 8000.

## SimpleLanguage Component for GraalVM

Languages implemented with the [Truffle framework](https://github.com/oracle/graal/tree/master/truffle) can be packaged as _components_ which later can be installed into GraalVM using the [GraalVM Updater](../reference-manual/graalvm-updater.md) tool.
Running `mvn package` in the SimpleLanguage folder also builds a `sl-component.jar`.
This file is the SimpleLanguage component for GraalVM and can be installed by running:
```shell
gu -L install /path/to/sl-component.jar
```

## SimpleLanguage Native Image

A language built with Truffle can be AOT compiled using [Native Image](../reference-manual/native-image/README.md).
Running `mvn package` in the SimpleLanguage folder also builds a `slnative` executable in the `native` directory.
This executable is the full SimpleLanguage implementation as a single native application, and has no need for GraalVM in order to execute SimpleLanguage code.
Besides this, a big advantage of using the native executable when compared to running on GraalVM is the greatly faster startup time as shown bellow:
```shell
time ./sl language/tests/HelloWorld.sl
== running on org.graalvm.polyglot.Engine@2db0f6b2
Hello World!

real    0m0.405s
user    0m0.660s
sys     0m0.108s

time ./native/slnative
language/tests/HelloWorld.sl
== running on org.graalvm.polyglot.Engine@7fd046f06898
Hello World!

real    0m0.004s
user    0m0.000s
sys     0m0.000s
```

This snipped shows a timed execution of a "Hello World" program using the `sl` launcher script, which runs SimpleLanguage on GraalVM, using Native Image.
We can see that when running on GraalVM the execution takes 405ms.
Since our SimpleLanguage program does just one print statement, we can conclude that almost all of this time is spent starting up GraalVM and initializing the language itself.
When using the native executable we see that the execution takes only 4ms, showing two orders of magnitude faster startup than running on GraalVM.

For more information on the `native-image` tool consider reading the [reference manual](../reference-manual/native-image/README.md).

### Disable SimpleLanguage Native Image Build

Building the native executable through Maven is attached to the Maven `package` phase.
Since the native executable build can take a bit of time, we provide the option to skip this build by setting the `SL_BUILD_NATIVE` environment variable to `false` like so:

```shell
export SL_BUILD_NATIVE=false
mvn package
...
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] Building simplelanguage-graalvm-native
[INFO] ------------------------------------------------------------------------
[INFO]
[INFO] --- exec-maven-plugin:1.6.0:exec (make_native) @ simplelanguage-graalvm-native ---
Skipping the native image build because SL_BUILD_NATIVE is set to false.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
...
```

## Run SimpleLanguage with the Newest (Developement) version of the Compiler

To run SimpleLanguage with the development version of the Graal compiler we must build a GraalVM with that compiler.
Clone the `graal` repository (https://github.com/oracle/graal) and follow the instructions in the `vm/README.md` file to build a GraalVM.

Once that's done, point `JAVA_HOME` to the newly built GraalVM and proceed with normal building and running of SimpleLanguage.

## Run SimpleLanguage Using Command Line

Executing SimpleLanguage code is normally done with the `sl` script which sets up the necessary command line depending on whether `JAVA_HOME` points to GraalVM or another JVM installation.
The following subsections describe the command line for both cases.

### Run SimpleLanguage with GraalVM as JAVA_HOME

Assuming `JAVA_HOME` points to the GraalVM installation and that the current working directory is the `simplelanguage` directory, to run SimpleLanguage one should execute the following command:

```shell
$JAVA_HOME/bin/java \
    -cp launcher/target/launcher-22.1.0-SNAPSHOT.jar \
    -Dtruffle.class.path.append=language/target/simplelanguage.jar \
    com.oracle.truffle.sl.launcher.SLMain language/tests/Add.sl
```

In short, we place the launcher JAR on the class path and execute its main class, but we inform GraalVM of the presence of SimpleLanguage by using the `-Dtruffle.class.path.append` option and providing it the path to the fat language JAR.
Having the language on a separate class path ensures a strong separation between the language implementation and its embedding context (in this case, the launcher).

#### Disable Class Path Separation

*NOTE! This should only be used during development.*

For development purposes it is useful to disable the class path separation and enable having the language implementation on the application class path (for example, for testing
the internals of the language).

The Language API JAR on Maven Central exports all API packages in its module-info.
Apply the `--upgrade-module-path` option together with `-Dgraalvm.locatorDisabled=true` and this JAR to export Language API packages:
```shell
-Dgraalvm.locatorDisabled=true --module-path=<yourModulePath>:${truffle.dir} --upgrade-module-path=${truffle.dir}/truffle-api.jar
```

A sample POM using `--upgrade-module-path` to export Language API packages can be found in the [Simple Language POM.xml](https://github.com/graalvm/simplelanguage/blob/master/language/pom.xml#L58) file.

> Note: Disabling the locator effectively removes all installed languages from the module path as the locator also creates the class loader for the languages.
To still use the builtin languages add them to the module-path by pointing the module-path to all needed language homes (for example, `$GRAALVM/languages/js`).

### Other JVM Implementations

Unlike GraalVM, which includes all the dependencies needed to run a language implemented with [Truffle](../../truffle/docs/README.md), other JVM implementations need additional JARs to be present on the class path.
These are the Language API and GraalVM SDK JARs available from Maven Central.

Assuming `JAVA_HOME` points to a stock JDK installation, and that the current working directory is the `simplelanguage` directory and the Language API and GraalVM SDK JARs are present in that directory, one can execute SimpleLanguage with the following command:

```shell
$JAVA_HOME/bin/java \
    -cp graal-sdk-22.1.0.jar:truffle-api-22.1.0.jar:launcher/target/launcher-22.1.0-SNAPSHOT.jar:language/target/simplelanguage.jar \
    com.oracle.truffle.sl.launcher.SLMain language/tests/Add.sl
```
