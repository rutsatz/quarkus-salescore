# salescore project

## Testing Online
You can use the link below to test the application:

[https://salescore.herokuapp.com/swagger/](https://salescore.herokuapp.com/swagger/)

## Testing locally

See the steps below to run the application on your local machine.

### Requirements

You will need to have the tools below installed locally:

- Java 11
- Docker

### Downloading the project source code

Clone the Git repository below:

[https://github.com/rutsatz/quarkus-salescore](https://github.com/rutsatz/quarkus-salescore)


### Running the project locally

Inside the project folder, execute the code below:

```shell script
./gradlew quarkusDev
```

### Accessing the API locally

With the project running, you can test the API by accessing the following address:

[http://localhost:8080/swagger/](http://localhost:8080/swagger/)


> **_NOTE:_** If you prefer to use Postman, you can use the collection below that comes with the examples ready to run locally. [Download Collection](https://www.getpostman.com/collections/b1cf4487283530534d76)


### Compiling the project

Use the following command to package the project:

```shell script
./gradlew build
```


### Creating a container

```shell script
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/salescore-jvm .
```

### Running the container

```shell script
docker run -i --rm -p 8080:8080 quarkus/salescore-jvm
```

## About the project

This project was developed using Java with the Quarkus framework and MongoDB for database.

## Supporting a very high access load

Considering that the statistics endpoints will be accessed a lot, we could change the current
architecture of the project to something as shown in the diagram below:

![salescore_diagram](https://user-images.githubusercontent.com/14064725/112550842-c1c20800-8d9e-11eb-9e6c-eb4971785583.png)


In this diagram, we separate the statistics service into a separate service. It receives events about
sales made and updates its own database. In this way, we are able to isolate the high access load in a
way that does not affect the operation of the other services and we can have an updated report in real time.
