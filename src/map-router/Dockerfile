FROM node:latest

WORKDIR /react-vite-app

EXPOSE 5173

COPY package.json package-lock.json ./

RUN npm install --silent

COPY . ./

CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]