# Api Gateway

- Static content provided by API Gateway

        http://localhost:8080

    > This static content might be deployed in a separated server (to scale independently) such as S3 or another object storage with cached-proxy support

- Test local services (not exposed outside the VPC or network)

  - Customer

        http://localhost:8081/swagger-ui.html
        http://localhost:8081/actuator/health

  - Catalog

        http://localhost:8082/swagger-ui.html
        http://localhost:8082/actuator/health

  - Auth  

        http://localhost:8083/swagger-ui.html
        http://localhost:8083/actuator/health

- Test **API Gateway** **routes** to previous services

        http://localhost:8080/api/customer/swagger-ui.html
        http://localhost:8080/api/catalog/swagger-ui.html
        http://localhost:8080/api/auth/swagger-ui.html
        http://localhost:8080/api/order/swagger-ui.html

- Test **Load Balance** between instances

        http://dockerhost:8080/api/customer/actuator/info

- Authenticate Gateway Rest API

    http://localhost:8080/oauth/token

- Generate JKS keystore

        keytool -genkey -keyalg RSA -alias keystore -keystore keystore.jks -storepass password -validity 360 -keysize 2048

- Create ``Customers``

```json
{
  "address": {
    "city": "Madrid",
    "country": "Spain",
    "houseNumber": 4,
    "state": "Madrid",
    "streetAddress": "Calle Gran Via",
    "zipCode": "28065"
  },
  "email": "jsa4000@gmail.com",
  "firstName": "Javier",
  "lastName": "Santos"
}
```


## Improvements

- Mem-Cache: use of local and distributed cache. i.e enumerable data (non-changed eventually) such as roles, catalog, users, etc...
- Check user Roles and Authorities for securing resources in REST API endpoints.
- TLS for secure channel connection.
- Exception handling and logging enhancements
- Metrics for Prometheus: micrometer default, custom metrics (via AOP), etc..
- Event Driven development, DDD
- Testing: integration test, validation test (cocomo), performance tests (jmeter, gantling)