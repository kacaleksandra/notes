# Use the official Node.js image as the base image
FROM node:20.10.0

# Set the working directory
WORKDIR /app

# Copy files from your computer into the image
COPY . .

# Install the dependencies
RUN npm install

# Copy package.json and package-lock.json to the container
COPY package*.json ./

# Install application dependencies
RUN npm install

# Expose the port that your application will run on (Nest.js default is 3000)
EXPOSE 3000

# Define the command to run your application
CMD ["npm", "start"]