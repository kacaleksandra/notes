FROM node:20.10.0 AS build

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm install

COPY . .

RUN npx prisma generate

RUN npm run build

FROM node:20-slim AS runtime

RUN apt update && apt install libssl-dev dumb-init -y --no-install-recommends

WORKDIR /app

COPY --from=build /app/dist ./dist
COPY --from=build /app/.env .env
COPY --from=build /app/package.json .
COPY --from=build /app/package-lock.json .

RUN npm install --omit=dev

COPY --from=build /app/node_modules/.prisma/client ./node_modules/.prisma/client

EXPOSE 3000

CMD ["dumb-init", "node", "dist/main.js"]

