## Install

```
npm install
bower install
```

## Run

```
npm start
```

Start in debug mode (activate debug mode for `xcarpaccio:server`):

```
DEBUG=xcarpaccio:server npm start
```

## Test

```
npm test
```

## Running within a Docker Container

# Build the image

```
  docker build -t extreme-carpaccio-server .
```

# Start the container

```
  docker run -d --name extreme-carpaccio-server -p 3000:3000 extreme-carpaccio-server
```