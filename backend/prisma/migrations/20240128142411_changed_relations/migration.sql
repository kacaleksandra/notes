/*
  Warnings:

  - You are about to drop the column `categoryId` on the `Notes` table. All the data in the column will be lost.

*/
-- DropForeignKey
ALTER TABLE "Notes" DROP CONSTRAINT "Notes_categoryId_fkey";

-- AlterTable
ALTER TABLE "Notes" DROP COLUMN "categoryId";

-- CreateTable
CREATE TABLE "NoteCategories" (
    "noteId" INTEGER NOT NULL,
    "categoryId" INTEGER NOT NULL,

    CONSTRAINT "NoteCategories_pkey" PRIMARY KEY ("noteId","categoryId")
);

-- CreateTable
CREATE TABLE "_CategoriesToNotes" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL
);

-- CreateIndex
CREATE UNIQUE INDEX "_CategoriesToNotes_AB_unique" ON "_CategoriesToNotes"("A", "B");

-- CreateIndex
CREATE INDEX "_CategoriesToNotes_B_index" ON "_CategoriesToNotes"("B");

-- AddForeignKey
ALTER TABLE "NoteCategories" ADD CONSTRAINT "NoteCategories_noteId_fkey" FOREIGN KEY ("noteId") REFERENCES "Notes"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "NoteCategories" ADD CONSTRAINT "NoteCategories_categoryId_fkey" FOREIGN KEY ("categoryId") REFERENCES "Categories"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_CategoriesToNotes" ADD CONSTRAINT "_CategoriesToNotes_A_fkey" FOREIGN KEY ("A") REFERENCES "Categories"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_CategoriesToNotes" ADD CONSTRAINT "_CategoriesToNotes_B_fkey" FOREIGN KEY ("B") REFERENCES "Notes"("id") ON DELETE CASCADE ON UPDATE CASCADE;
