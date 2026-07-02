package com.ypsi.fundamentalism.datagen.book.demo.mytestcategory.exhaustion;

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import com.ypsi.fundamentalism.item.ModItems;
import io.redspace.ironsspellbooks.registries.ItemRegistry;

public class AmountEntry extends EntryProvider {
    public static final String ID = "amount_entry";

    public AmountEntry(CategoryProviderBase parent) {
        super(parent);
    }


    @Override
    protected void generatePages() {
        this.page("page1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Exhaustion Stages");
        this.pageText("""
                A magic user can pass through 5 different exhaustion stages. Each one with a different max capacity of exhaustion pts:
                - Stage 0: 50 pts
                - Stage 1: 100 pts
                - Stage 2: 200 pts
                - Stage 3: 100 pts
                - Stage 4: 50 pts
                """);

        this.page("page2", () -> BookTextPageModel.create()
                .withText(this.context().pageText())
        );
        this.pageText("""
                In order to decrement the exhaustion stage, you have to reach 0 exhaustion pts in a level and must not accumulate any exhaustion point during 5 seconds.
                The default value for exhaustion regen is 1 pt per second.
                """);

        this.page("page3", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Exhaustion Penalties");
        this.pageText("""
                Spellcasting is affected by the exhaustion stage. A higher stage will give you a greater debuff:
                - Stage 0: -0% Spell power
                - Stage 1: -10% Spell power
                - Stage 2: -25% Spell power
                - Stage 3: -50% Spell power
                - Stage 4: -80% Spell power
                """);

        this.page("page4", () -> BookTextPageModel.create()
                .withText(this.context().pageText())
        );
        this.pageText("""
                Another kind of debuff involves a higher mana consumption for Certum Spells:
                - Stage 0: +% Spell power
                - Stage 1: -10% Spell power
                - Stage 2: -25% Spell power
                - Stage 3: -50% Spell power
                - Stage 4: -80% Spell power
                """);

    }

    @Override
    protected String entryName() {
        return "Exhaustion Stages and Debuffs";
    }

    @Override
    protected String entryDescription() {
        return "Describes the stages of exhaustion and penalties applied.";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ItemRegistry.FIREFLY_JAR_ITEM.get());
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
