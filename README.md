# Spring Kotlin Postgres

<!-- [![Run on Google Cloud](https://deploy.cloud.run/button.png)](https://deploy.cloud.run) -->

## Local Dev

Start the server:
```
./gradlew bootRun
```

Use the server:
```
# Create a "bar"
curl -v -X POST localhost:8080/bars -H 'Content-Type: application/json' -d '{"name": "Test"}'

# Get the "bars"
curl localhost:8080/bars
```

Run the tests:
```
./gradlew test
```

Create container & run with docker:
```
./gradlew bootBuildImage

docker run --rm -ePOSTGRES_PASSWORD=password -p5432:5432 --name my-postgres postgres:13.3

docker run -it --network host \
  -eSPRING_R2DBC_URL=r2dbc:postgresql://localhost/postgres \
  -eSPRING_R2DBC_USERNAME=postgres \
  -eSPRING_R2DBC_PASSWORD=password \
  spring-kotlin-postgres \
  init

# psql
docker exec -it my-postgres psql -U postgres

docker run -it --network host \
  -eSPRING_R2DBC_URL=r2dbc:postgresql://localhost/postgres \
  -eSPRING_R2DBC_USERNAME=postgres \
  -eSPRING_R2DBC_PASSWORD=password \
  spring-kotlin-postgres
```

[http://localhost:8080/bars](http://localhost:8080/bars)

