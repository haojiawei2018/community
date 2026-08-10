# Hope Framework

A ready-to-use **Spring Boot backend scaffold** based on Spring Boot 2.1.10 + JDK 8. It ships with unified responses, global exception handling, JWT authentication, dynamic multi-datasource, MyBatis-Plus, Redis, pagination and Swagger, plus a complete CRUD demo so you can start writing business code immediately.

## Repository

- Homepage: https://gitee.com/hao_jiawei/java-master
- Clone: `git clone https://gitee.com/hao_jiawei/java-master.git`

## Tech Stack

| Category | Component |
| --- | --- |
| Framework | Spring Boot 2.1.10, Spring MVC |
| ORM | MyBatis-Plus 3.3.1, PageHelper |
| Datasource | Druid, dynamic-datasource |
| Database | MySQL 5.7 |
| Cache | Redis (Jedis + RedisUtil) |
| Auth | JWT (java-jwt / jjwt) + interceptor annotations |
| Docs | Swagger 2 / swagger-bootstrap-ui |
| Build | Maven multi-module |

## Modules

```
java-master
├── hope-api                     # Business API service (executable)
│   └── src/main
│       ├── java/org/hopeframework/biz/api
│       │   ├── auto             # Auth interceptor, annotations, web config
│       │   ├── config           # Swagger, ID generator, Redis, pagination
│       │   ├── controller       # REST controllers (DemoController)
│       │   ├── entity           # input / output / page objects
│       │   ├── helper           # helpers
│       │   ├── mapper           # MyBatis-Plus mappers (DemoMapper)
│       │   ├── model            # DB entities (Demo)
│       │   ├── service          # business layer (interface + impl)
│       │   └── util             # common utilities
│       └── resources
│           ├── application*.yml # dev / test / prod profiles
│           └── xml              # MyBatis XML (custom SQL example)
└── hope-dependencies            # base component libraries
    ├── hope-core                # response, exception, logging, web config
    ├── hope-utils               # common utils
    └── hope-test                # test helpers
```

## Quick Start

1. Requirements: JDK 8, Maven 3.6+, MySQL 5.7, Redis.
2. From the `backend` directory, run `../sql/community_business_v1.sql` and `../sql/V20260810_01__seed_p0_permissions.sql` (default database name: `hope`).
3. Edit `hope-api/src/main/resources/application-dev.yml` (defaults to `localhost:3306/hope`, user `root/root`).
4. Start:

```bash
mvn -pl hope-api -am spring-boot:run -Dspring-boot.run.profiles=dev
# or
mvn clean package -DskipTests
java -jar hope-api/target/hope-biz-api-1.0.0.jar --spring.profiles.active=dev
```

5. API docs: http://localhost:10003/doc.html

## Demo API

| Method | Path | Description |
| --- | --- | --- |
| GET | /demo/{id} | get by id |
| GET | /demo/list | list (filter by name/phone) |
| GET | /demo/page | paginated query |
| POST | /demo | create |
| PUT | /demo | update |
| DELETE | /demo/{id} | delete |

## Adding Business Code

Follow the demo chain: model -> mapper -> service -> controller.

## Contributing

1. Fork the repository
2. Create a Feat_xxx branch
3. Commit your code
4. Open a Pull Request

## Open Source Notes

This repository was trimmed from a business project. Original business code and sensitive configs were removed; all profiles default to local `localhost`. Feel free to Star / Fork / open Issues.
