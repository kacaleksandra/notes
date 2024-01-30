FROM node:20-alpine AS builder

# Create app directory
WORKDIR /app

# A wildcard is used to ensure both package.json AND package-lock.json are copied
COPY package*.json ./
COPY prisma ./prisma/

# Install app dependencies
RUN npm install

COPY . .

# Build app
RUN npm run build

FROM node:20-alpine

# Install required packages
RUN apt-get update
RUN apt-get install dumb-init

# Copy built files to target image
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/tsconfig.json ./
COPY --from=builder /app/prisma ./prisma

EXPOSE 3000

# Start simple init system
ENTRYPOINT ["dumb-init", "--"]

# Initialize database configuration (in node_modules/.prisma) and start app
CMD ["bash", "-c", "npx prisma migrate deploy && node dist/src/main"]
