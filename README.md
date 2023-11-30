# Url-Shortener

## Purpose

The purpose of this standalone application is to receive a URL and return a unique alphanumeric identifier for it.
Thereafter, when the server is queried with this identifier, it shall redirect the user to the original URL that was
provided.

In addition to this, there are secondary functions such as obtaining all identifiers created for a specific URL and
inquiring on the amount of redirections that have occurred for a specific identifier.

## Project

### Technological Choices

* The application is written in Java 17, the second most recent LTS (Long Term Support) version.
* The application is managed with Apache Maven, a software project management tool which handles notably the
  compilation.
* Spring Boot 3.2.0 is used to build the application, this tool facilitates the creation of a standalone,
  production-ready application with the spring framework.
* The URL API is created via the [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator) (v3.0.0), based
  on YAML specification.
    * It is design-first, the focus is on API definition rather than implementation
    * Provides a [Swagger](https://swagger.io/) interface to test defined endpoints of the URL API
* The Remote Dictionary Server [REDIS](https://redis.io/) is used as a NoSQL persistence solution.
    * Simple integration into Spring project
    * Data is stored in memory, which enables accelerated data access.

### Prerequisites

* Have Java 17 (i.e. Oracle OpenJDK version 17.0.5).
* Have Maven to handle the project.
* Have a REDIS server installed, instructions [here](https://redis.io/docs/install/install-redis/).

### Installation

* The default port for the REDIS server is `6379` and the database is `0`. if you wish to use different values, please
  update the `application.properties` file accordingly.
* Run `mvn clean install` to generate sources and compile project
* Run `UrlShortenerApplication`

### Using Application

* You can use the application with the postman collection provided
  in `src/main/resources/postman/url-shortener collection.postman_collection.json`
  **Warning**: turn off `Automatically follow redirects` in postman settings if you wish to disable the redirection.
* You can use the Swagger interface by default at `http://localhost:8080/swagger-ui/index.html`. Please note that the
  redirection doesn't work as intended via this interface.
