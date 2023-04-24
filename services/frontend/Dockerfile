FROM node:lts-alpine as build-stage

WORKDIR /app
COPY ./fis/package*.json ./

RUN npm update -g npm
RUN npm install
RUN npx browserslist@latest --update-db

CMD ["npm", "run", "dev"]
