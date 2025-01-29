# Spring Dynamic Property Source Example

This example application demonstrates how to use the Spring Boot EnumerablePropertySource and EnvironmentPostProcessor classes to load properties dynamically at application startup from an external source (in this case an Oracle Database server).

The use of dynamic property sources allows applications to manage and rotate properties from external services, such as secure key vaults, file servers, or databases for increased application configurability and security.

For an in-depth walkthough, see the following guide: [Dynamically load Spring properties from external repositories](https://medium.com/@anders.swanson.93/dynamically-load-spring-properties-from-external-locations-3ef644b42035).

## Prerequisites

- Java 21+, Gradle