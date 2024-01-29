-- DropIndex
DROP INDEX "Tokens_token_key";

-- AlterTable
ALTER TABLE "Tokens" ADD COLUMN     "id" SERIAL NOT NULL,
ADD CONSTRAINT "Tokens_pkey" PRIMARY KEY ("id");
