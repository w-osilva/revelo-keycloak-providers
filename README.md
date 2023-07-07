# Keycloak - Google Auth Provider

Providers are extensions that allow us to customize and extend Keycloak's capabilities. 
They are implemented as Java classes that are packaged as JAR files and deployed to the `providers` directory of the Keycloak server.

## Providers available in this repository
- [Google Provider](./google/README.md)


## Development

It was developed using:
- OpenJDK 11.0.17
- Maven 3.9.3

*We recommend using the same versions to avoid any issues.*

To make it easier to get started, we suggest using [ASDF](https://asdf-vm.com/#/core-manage-asdf-vm) to manage your Java and Maven versions.


## Build

To build the provider binary clone the repository and run the following command:

```bash
mvn clean install
```

The `jar` file will be created in the `target` directory of each provider. Example:

```
google
├── src
├── target
│   ├── keycloak-google-provider-1.0.0.jar  <--- JAR file
├── README.md
├── pom.xml
```

## References
Keycloak Documentation

- [Service Provider Interfaces (SPI)](https://www.keycloak.org/docs/latest/server_development/#_providers)

- [Configuring providers
](https://www.keycloak.org/server/configuration-provider)

- [Authentication SPI](https://www.keycloak.org/docs/latest/server_development/#_auth_spi)


Custom Providers

- [Apple Identity Provider for Keycloak](https://github.com/klausbetz/apple-identity-provider-keycloak)

- [Keycloak-vk](https://github.com/zhkazarosian/keycloak-vk)