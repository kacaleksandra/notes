FROM node:20-alpine AS builder

# Create app directory
WORKDIR /app

# Copy only the required source files needed for build
COPY package.json package-lock.json tsconfig.json ./
COPY prisma ./prisma
COPY src ./src
COPY common ./common

# Install dependencies and build app
RUN npm ci
RUN npm run build

FROM node:20-alpine

# Install required packages
RUN apk update update
RUN apk add dumb-init

# Copy built files to target image
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/prisma ./prisma

EXPOSE 3000

# Start simple init system
ENTRYPOINT ["dumb-init", "--"]

# Initialize database configuration (in node_modules/.prisma) and start app
CMD ["sh", "-c", "npx prisma migrate deploy && node dist/src/main"]
