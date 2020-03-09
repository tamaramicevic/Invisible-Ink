FROM node:12.13-alpine

COPY . /usr/api-server
WORKDIR /usr/api-server

RUN npm install
RUN npm install pm2 -g
RUN npm run start:prod

# if this folder doesn't exist nodejs won't start:
RUN mkdir -p /usr/api-server/dist/modules/log/logs

EXPOSE 3000

CMD ["node", "dist/main"]
