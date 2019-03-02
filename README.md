dragonspires
============
dragonspires is an old online game from the 90s to early 2000s. This repository is a fork of the [original project](https://sourceforge.net/projects/jdspire/) with minor changes so that it builds and runs.

Getting Started
===============

[Gradle](https://gradle.org/) is used to build dragonspires. Gradle and dragonspires' both run on Java, so download and install the [JDK or JRE](https://www.oracle.com/technetwork/java/javase/downloads/index.html) first. There are two primary projects: the [client](client) and the [server](server).

First build the server:

Open a command prompt or terminal in the repository directory.

```bash
$ gradlew server:build
```

Then extract the server package `server\build\distributions\server.zip`. Run `bin\server.bat`.

Then either build or run the client:

```bash
$ gradlew client:run
```

When the client opens, change the server to "Localhost (test)" and press the "New Game" button.

Hosting
=======
The server can be hosted on a remote computer. Either replace host for the "US West (main)" server or add another server to the client.
