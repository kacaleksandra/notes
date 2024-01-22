FROM node:20.10 AS builder

# Create app directory
WORKDIR /app

COPY package*.json ./
COPY prisma ./prisma/

# Install app dependencies
RUN npm install

COPY . .

RUN npm run build


FROM node:20.10

COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/dist ./dist
# copy prisma directory
COPY --from=builder /app/prisma ./prisma

EXPOSE 3000
CMD [  "npm", "run", "start:migrate:prod" ]
