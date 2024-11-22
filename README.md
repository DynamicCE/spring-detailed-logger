# Spring Detailed Logger

[![Maven Central](https://img.shields.io/maven-central/v/io.github.dynamicce/spring-detailed-logger.svg)](https://search.maven.org/artifact/io.github.dynamicce/spring-detailed-logger)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17%2B-blue)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

A simple and powerful library that provides detailed method logging capabilities for Spring Boot applications.

## 🚀 Features

- Detailed method call logging
- Automatic performance metrics tracking
- HTTP request details (IP, headers)
- Memory usage information
- Correlation ID support
- Spring profile information
- Exception handling and logging
- Emoji-enhanced readable log format

## 📦 Installation

Add the Maven dependency:

```xml
<dependency>
    <groupId>io.github.dynamicce</groupId>
    <artifactId>spring-detailed-logger</artifactId>
    <version>1.0.4</version>
</dependency>


## 🔧 Usage

1. Add the @LogDetails annotation to the methods you want to log:

@RestController
public class UserController {

    @LogDetails
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

}

2. The log output will look like this:

🎯 Method: getUser
📝 Parameters: id=1
⏱️ Execution Time: 145ms
🌐 IP: 192.168.1.1
🔑 Correlation ID: abc123
💾 Memory Usage: 24MB
🔧 Profile: development

## ⚙️ Configuration

You can customize the following settings in your application.properties or application.yml:

spring:
detailed-logger:
enabled: true
include-memory-info: true
include-profile-info: true
correlation-id-header: X-Correlation-ID

## 🤝 Contributing

1. Fork this repository
2. Create your feature branch (git checkout -b feature/amazing-feature)
3. Commit your changes (git commit -m 'feat: Add amazing feature')
4. Push to the branch (git push origin feature/amazing-feature)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

Thank you for using this library! If you have any issues or suggestions, please feel free to reach out through GitHub Issues.
