FROM node:latest
LABEL description="final-server"
WORKDIR /docs
RUN npm config set registry https://registry.npmmirror.com
RUN npm install -g docsify-cli@latest
EXPOSE 3000/tcp
ENTRYPOINT docsify serve .