Config server allows other service to use the configs that were shipped by this server. 
In order to be able to receive the configs form the server client should have included this in their `application.properties`:

```
spring.config.import=configserver:http://localhost:8082
```

Config server has stores all config files that are ready to be shipped in the `resources/config` folder. That folder can contain some files:
1. `application.properties` --> is shipped to every service in the first order
2. `<service-name>.properties` --> is being shipped only to the named service, WILL override general config file properties with the same key from the config server `application.properties`
