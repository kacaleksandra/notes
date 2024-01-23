/*
  Warnings:

  - You are about to drop the `User` table. If the table is not empty, all the data it contains will be lost.

*/
-- DropTable
DROP TABLE "User";

-- CreateTable
CREATE TABLE "Users" (
    "id" SERIAL NOT NULL,
    "email" TEXT NOT NULL,
    "password" TEXT NOT NULL,

    CONSTRAINT "Users_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Categories" (
    "id" SERIAL NOT NULL,
    "title" TEXT NOT NULL,
    "userId" INTEGER NOT NULL,

    CONSTRAINT "Categories_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Notes" (
    "id" SERIAL NOT NULL,
    "title" TEXT NOT NULL,
    "content" TEXT NOT NULL,
    "created_at" TIMESTAMP(3) NOT NULL,
    "userId" INTEGER NOT NULL,
    "categoryId" INTEGER NOT NULL,

    CONSTRAINT "Notes_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "Users_email_key" ON "Users"("email");

-- AddForeignKey
ALTER TABLE "Categories" ADD CONSTRAINT "Categories_userId_fkey" FOREIGN KEY ("userId") REFERENCES "Users"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Notes" ADD CONSTRAINT "Notes_userId_fkey" FOREIGN KEY ("userId") REFERENCES "Users"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Notes" ADD CONSTRAINT "Notes_categoryId_fkey" FOREIGN KEY ("categoryId") REFERENCES "Categories"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
