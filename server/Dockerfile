FROM golang:1.19-alpine as builder
RUN mkdir /build 
COPY ./*.go /build/
COPY go.mod /build/
COPY go.sum /build/
WORKDIR /build 
RUN apk update && apk add gcc libc-dev
RUN go build -o main .

FROM alpine
RUN adduser -S -D -H -h /app appuser
USER appuser
COPY --from=builder /build/main /app/
# ADD proxy /app/ 
# COPY run.sh /app
WORKDIR /app
EXPOSE 7999
# EXPOSE 8080
CMD ["/app/main"]