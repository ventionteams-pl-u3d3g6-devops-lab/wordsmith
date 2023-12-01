#chose the latest goland image
FROM golang:latest

#create a working dir in image
WORKDIR /app

#copy content from host to container
COPY . .

#create the static dir inside the main app dir
RUN mkdir -p /app/static

#copy static folder from host to container
COPY ./static /app/static

# Build
RUN go build dispatcher.go

#expose 80 port for app
EXPOSE 80

#execute
CMD ["./dispatcher"]

