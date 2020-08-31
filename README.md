# Food Delivery
A demo backend for food delivery which is written in Java 11 with Gradle build tool and uses MySQL as database.

### Setup Database

- Download MySQL docker image:

```shell script
docker pull mysql:5.7
```

- Start MySQL with root password and a local folder for persisting MySQL data. 
Replace `YOUR_LOCAL_FOLDER` with your local folder and `YOUR_PASSWORD` with your password.

```shell script
docker run -d -p 3306:3306 -v YOUR_LOCAL_FOLDER:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=YOUR_PASSWORD --name mysql mysql:5.7 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

### Create Database Schema

Change to `sql/` directory, run the following command to create database schema:

```shell script
docker exec -i mysql mysql -uroot -pYOUR_PASSWORD < schema.sql
```

### Install JDK

Install JDK 11 if you don't have. 
Please follow the document on [AdoptOpenJDK](https://adoptopenjdk.net/) to install.

### Build App

Under the project root directory, run the following command, 
it will create an jar(eg: `fd-0.0.1-SNAPSHOT.jar`) file in `build/libs/`:

```shell script
./gradlew build -x test
```

### Create App Docker image

[Jib](https://github.com/GoogleContainerTools/jib) is used to create Java docker image:

```shell script
./gradlew jibDockerBuild
```

You can find the image `food-delivery` by:

```shell script
docker images
```

### Load Raw Data to Database

There are two raw data sources: 
- restaurant_with_menu: https://gist.githubusercontent.com/seahyc/b9ebbe264f8633a1bf167cc6a90d4b57/raw/021d2e0d2c56217bad524119d1c31419b2938505/restaurant_with_menu.json
- users_with_purchase_history: https://gist.githubusercontent.com/seahyc/de33162db680c3d595e955752178d57d/raw/785007bc91c543f847b87d705499e86e16961379/users_with_purchase_history.json

If the raw data sources changed, please use `RESTAURANT_DATA_URL` and `USER_DATA_URL` as environment variables to pass correct URLs to Docker, eg: 

RESTAURANT_DATA_URL=https://.../restaurant_with_menu.json

USER_DATA_URL=https://.../users_with_purchase_history.json

Run the following command to extract, transform and load data to database, set the environment variable `LOAD_DB_DATA` as:
- `1`: to load raw restaurant data only.
- `2`: to load raw user data only.
- `3`: to load both restaurant and user data.

```shell script
docker run -i -e DB_URL=mysql:3306 -e LOAD_DB_DATA=3 --rm --link mysql:mysql --name food-delivery food-delivery
```

Or you can run the app directly by:

```shell script
LOAD_DB_DATA=3 java -jar build/libs/fd-0.0.1-SNAPSHOT.jar
```

### Run App

Run the app by the following command:

```shell script
docker run -d -p 8080:8080 -e DB_URL=mysql:3306 --link mysql:mysql --name food-delivery food-delivery
```

### API Doc

- After starting the app, you can browse the REST API doc on `http://localhost:8080/swagger-ui.html`.
- Or there is an Open API yaml document under `apiDoc/`, you can use any Swagger Viewer to view the document.