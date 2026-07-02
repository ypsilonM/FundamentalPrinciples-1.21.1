package com.ypsi.fundamentalism.datagen.book.demo.mytestcategory.exhaustion;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.world.item.Items;

public class CastingEntry extends EntryProvider {
    public static final String ID = "casting_exhaustion";

    public CastingEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("page1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );

        this.pageTitle("Casting Source");
        this.pageText("""
                Casting spells will accumulate an specific amount of exhaustion points to the player's max exhaustion.\s
                """);

        this.page("page2", () -> BookTextPageModel.create()
                .withText(this.context().pageText())
        );

        this.pageText(""" 
                \n
                \n
                The amount of points will be determined by several factors as: \n
                - Mana wasted
                - User's spell power
                - User's school spell power
                - Spell rarity
                - Cast type
                - Spell's number of principles
                - Cast source. \s
                """);
    }

    @Override
    protected String entryName() {
        return "Exhaustion Modifiers";
    }

    @Override
    protected String entryDescription() {
        return "How does the body accumulates exhaustion?";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ItemRegistry.SCROLL.get());
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
