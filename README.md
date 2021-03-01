# JDA-Utilities
Discord JDA Utilities to build your own Discord bot

With this library you can easily create your discord bots with Java. This library is based on the [JDA](https://github.com/DV8FromTheWorld/JDA). 

## Features
- Command System
- Logging System with file logs
- File-based database system
- Menu-API

## Requirements
A Java project with minimum java 8. This library has already compiled stuff like the jda, jline and gson.

## How to use
Add the repository and dependency to your plugin:

Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.GlaubeKeinemDev</groupId>
    <artifactId>JDA-Utilities</artifactId>
    <version>1.2-SNAPSHOT</version>
    <scope>compile</scope>
</dependency>
```

Gradle
```xml
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
   implementation 'com.github.GlaubeKeinemDev:JDA-Utilities:1.2-SNAPSHOT'
}
```

Now you're done! You can start by creating an instance of the 
[DiscordBot](https://github.com/GlaubeKeinemDev/JDA-Utilities/blob/master/src/main/java/de/glaubekeinemdev/discordutilities/DiscordBot.java).

More information how to use the library
[Wiki](https://github.com/GlaubeKeinemDev/JDA-Utilities/wiki).
