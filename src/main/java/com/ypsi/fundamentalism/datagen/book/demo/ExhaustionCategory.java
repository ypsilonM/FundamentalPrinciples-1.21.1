package com.ypsi.fundamentalism.datagen.book.demo;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.datagen.book.demo.formatting.AdvancedFormattingEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.formatting.AlwaysLockedEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.formatting.BasicFormattingEntry;
import com.klikli_dev.modonomicon.datagen.book.demo.formatting.LinkFormattingEntry;
import com.ypsi.fundamentalism.datagen.book.demo.mytestcategory.exhaustion.CastingEntry;
import com.ypsi.fundamentalism.datagen.book.demo.mytestcategory.exhaustion.DecrementingEntry;
import com.ypsi.fundamentalism.datagen.book.demo.mytestcategory.exhaustion.ExhaustionEntry;
import com.ypsi.fundamentalism.item.ModItems;

public class ExhaustionCategory extends CategoryProvider {
    public static final String ID = "exhaustion_category";

    public ExhaustionCategory(SingleBookSubProvider parent) {
        super(parent);
    }

    @Override
    protected String[] generateEntryMap() {
        return new String[]{
                "_____________________",
                "______c______________",
                "__0__________________",
                "______d______________",
                "_____________________"
        };
    }

    @Override
    protected void generateEntries() {
        var exhaustionEntry = this.add(new ExhaustionEntry(this).generate('0'));

        var advancedFormattingEntry = this.add(new CastingEntry(this).generate('c'))
                .withParent(this.parent(exhaustionEntry));

        var decrementEntry = this.add(new DecrementingEntry(this).generate('d'))
                .withParent(this.parent(exhaustionEntry));

    }

    @Override
    protected BookCategoryModel additionalSetup(BookCategoryModel category) {
        return category.withEntryToOpen(this.modLoc(ID, ExhaustionEntry.ID), true);
    }

    @Override
    protected String categoryName() {
        return "Exhaustion System";
    }

    @Override
    protected BookIconModel categoryIcon() {
        return BookIconModel.create(ModItems.FLASK);
    }

    @Override
    public String categoryId() {
        return ID;
    }
}
