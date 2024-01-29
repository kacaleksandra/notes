-- CreateTable
CREATE TABLE "Tokens" (
    "token" TEXT NOT NULL,
    "userId" INTEGER NOT NULL
);

-- CreateTable
CREATE TABLE "Reminders" (
    "id" SERIAL NOT NULL,
    "date" TIMESTAMP(3) NOT NULL,
    "userId" INTEGER NOT NULL,
    "noteId" INTEGER NOT NULL,

    CONSTRAINT "Reminders_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "Tokens_token_key" ON "Tokens"("token");

-- AddForeignKey
ALTER TABLE "Tokens" ADD CONSTRAINT "Tokens_userId_fkey" FOREIGN KEY ("userId") REFERENCES "Users"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Reminders" ADD CONSTRAINT "Reminders_userId_fkey" FOREIGN KEY ("userId") REFERENCES "Users"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Reminders" ADD CONSTRAINT "Reminders_noteId_fkey" FOREIGN KEY ("noteId") REFERENCES "Notes"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
