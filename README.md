#Docker commands for client

docker build -t client_sample:dev .
docker run -v ${PWD}:/app -v /app/node_modules -p 3000:3000 --rm client_sample:dev

#Docker commands for server

docker build -t myorg/myapp .
docker run -p 8080:8080 myorg/myapp

