package com.ypsi.fundamentalism.datagen.book.demo.mytestcategory.exhaustion;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.Items;

public class ExhaustionEntry extends EntryProvider {
    public static final String ID = "root_exhaustion";

    public ExhaustionEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("page1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );

        this.pageTitle("Spell Exhaustion");
        this.pageText("""
                \s
                Spellcasting is a practice in which magical users make use of the internal mana as a resource, 
                but that's not all the cost of casting spells.   \s
                """);

        this.page("page2", () -> BookTextPageModel.create()
                .withText(this.context().pageText())
        );

        this.pageText("""
                \s\s
                The body itself uses the mana and magical knowledge to transform mana into a more complex form,
                but it also gets clogged by mana impurities, inhibiting mana output. \s
                """
        );

    }

    @Override
    protected String entryName() {
        return "What is Spell Exhaustion?";
    }

    @Override
    protected String entryDescription() {
        return "Introducing exhaustion system";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(Items.PAPER);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
