# Welcome to Notes backend!

Our **Notes** application backend is built on **Nest.js**, a powerful Node.js framework, with **Prisma** handling the database operations. The use of Nest.js provides a structured and efficient foundation, while Prisma ensures seamless database interactions. This combination results in a scalable, maintainable backend that enhances the overall performance of the application.

## Technologies

- Nest.js
- Prisma
- Passport & bcryptjs
- Swagger & redoc

## Documentation

The documentation has been created using Swagger and ReDoc, ensuring clarity and accessibility.

#### Swagger Documentation

To explore the detailed API documentation, navigate to [localhost:3000/api#/](http://localhost:3000/api#/). Swagger offers an interactive interface that allows you to understand, test, and integrate with our API effortlessly.

<p align="center" >
<img width="500" alt="image" src="https://github.com/kacaleksandra/notes/assets/49205215/ff03e935-ea73-451e-b82c-4c67c01c4caf">
</p>

#### ReDoc Documentation

For a more streamlined and visually appealing documentation experience, visit localhost:3000/docs. ReDoc simplifies the presentation of the API documentation, making it easier for developers to consume and implement.

<p align="center" >
<img width="500" alt="image" src="https://github.com/kacaleksandra/notes/assets/49205215/b03db0b2-9fd6-42f3-a7a3-ac3143634d34">
</p>

## How to open project?

First, you have to make sure that you added `.env` file and it looks like this:

DATABASE_URL="url-to-your-database"
JWT_SECRET="your-jwt-secret"

To open project:

1. use `npm install` to download dependencies
2. launch project with `npm run start:dev`
3. use `npm prisma migrate deploy` to add migrations to your local db

# Examples

A very useful [HTTPie](https://httpie.io) tool is used in examples below.

### Create user

```
http POST http://localhost:3000/api/users \
    email=user2137@gmail.com \
    password=Tiger123
```

### Sign in

```
http POST http://localhost:3000/api/auth/login \
  email=user2137@gmail.com \
  password=Tiger123
```

> [!NOTE]
> Copy `accessToken` from the response as it's required for all other API interactions:

### Create category

> [!WARNING]
> Creating a category will not be necessary to create a note if #5 is fixed.

```
$ http POST http://localhost:3000/api/categories \
    Authorization:"Bearer $accessToken"
    title="Songs"
```

> [!NOTE]
> Copy `categoryId` from the response as it's required when creating a note

### Create note

> [!WARNING]
> Format of "categoryId" will change if #5 is fixed.

```
$ http POST http://localhost:3000/api/notes \
    Authorization:'Bearer $accessToken' \
    title="Snoooop dog" \
    content="These palm trees they sway. Each night and each day." \
    categoryId:=1
```
