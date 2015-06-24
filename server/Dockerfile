FROM ubuntu:14.04

RUN apt-get update  -qy
RUN apt-get install -qy git \
                        build-essential \
                        curl

RUN curl -sL https://deb.nodesource.com/setup | sudo bash -

RUN apt-get install -qy nodejs
RUN npm install -g bower

COPY . extreme-carpaccio-server
WORKDIR extreme-carpaccio-server

RUN npm install
RUN bower install --allow-root

EXPOSE 3000

CMD npm start