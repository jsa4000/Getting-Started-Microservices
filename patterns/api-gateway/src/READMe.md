# Api Gateway

- Static content provided by API Gateway

        http://localhost:8080
        
    > This static content might be deployed in a separated server (to scale independently) such as S3 or another object storage with cached-proxy support
http://localhost:8080/actuator/health

- Test local services (not exposed outside the VPC or network)

  - Customer

        http://localhost:8081/swagger-ui.html
        http://localhost:8081/actuator/health
     
  - Product   
    
        http://localhost:8082/swagger-ui.html
        http://localhost:8082/actuator/health
    
  - Auth  
    
        http://localhost:8083/swagger-ui.html
        http://localhost:8083/actuator/health

- Test **API Gateway** **routes** to previous services

        http://localhost:8080/api/customer/swagger-ui.html
        http://localhost:8080/api/product/swagger-ui.html
        http://localhost:8080/api/auth/swagger-ui.html
        http://localhost:8080/api/order/swagger-ui.html

- Test **Load Balance** between instances
        
        http://dockerhost:8080/api/customer/actuator/info

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