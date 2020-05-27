# Time Manager

## Information

REST API to register working time in projects.

The system was developed with:

* Java 8
* Spring Boot
* Maven
* H2 as database

It would be easy to configure the project to other relational databases like Postgresql or MySql.

To open the project in an IDE it is also necessary the lombok Plugin installed in the IDE.

A Swagger documentation can be seen at:

~~~
http://localhost:8080/swagger-ui.html
~~~

## To run the application

After building it with Maven, run the jar inside the target directory:

~~~
java -jar timemanager-1.0.0.jar
~~~

The following user already exists for tests:

~~~
login: admin
password: admin1
~~~

Security is provided using JWT, so the http header with the Authentication Bearer is necessary:

GET http://localhost:8080/api/v1/user/1

```javascript
{
  "id": 1,
  "name": "Admin",
  "email": "admin@timemanager.com.br",
  "login": "admin"
}
```
