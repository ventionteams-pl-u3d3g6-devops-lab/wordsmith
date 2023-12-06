#chose the latest goland image
FROM golang:latest as build

#create a working dir in image
WORKDIR /app

#copy dispatcher from host to container
COPY dispatcher.go .

# Build
RUN go build dispatcher.go

FROM alpine:latest

#create a working dir in image
WORKDIR /app

COPY --from=build /app/* /app/

#create the static dir inside the main app dir
RUN mkdir -p /app/static

#copy static folder from host to container
COPY ./static /app/static

#expose 80 port for app
EXPOSE 80

#execute
CMD ["./dispatcher"]

