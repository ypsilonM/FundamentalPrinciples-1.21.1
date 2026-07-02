package com.ypsi.fundamentalism.datagen.book.demo.mytestcategory.exhaustion;

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider;
import com.klikli_dev.modonomicon.api.datagen.EntryBackground;
import com.klikli_dev.modonomicon.api.datagen.EntryProvider;
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel;
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel;
import com.mojang.datafixers.util.Pair;
import com.ypsi.fundamentalism.item.ModItems;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class DecrementingEntry extends EntryProvider {
    public static final String ID = "decrementing_exhaustion";

    public DecrementingEntry(CategoryProvider parent) {
        super(parent);
    }

    @Override
    protected void generatePages() {
        this.page("page1", () -> BookTextPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Decrementing Exhaustion");
        this.pageText("""
                The body itself is responsible for dispelling body's fatigue through time, but there are ways of helping it.\s
                """);

//        this.page("page2", () -> BookTextPageModel.create()
//                .withText(this.context().pageText())
//                .withTitle(this.context().pageTitle())
//        );
        this.page("spotlight1", () -> BookSpotlightPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withItem(Ingredient.of(ModItems.LUMINAIRE_EXTRACT))
        );
        this.pageTitle("Luminaire Extract");
        this.pageText("""
                This special liquid enhances the fluctuation of mana impurities off the body. \s
                """);

        this.page("craftingTestTube", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1("ypfundamentals:test_tube")
                .withTitle1(this.context().pageTitle())
                .withText(this.context().pageText())
        );
        this.pageTitle("Test tube craft");
        this.pageText("""
                In order to contain luminaire extract you'll first need a test tube.
                """);

        this.page("craftingArcaneMixture", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1("ypfundamentals:arcane_mixture")
                .withText(this.context().pageText())
                .withTitle1(this.context().pageTitle())
        );
        this.pageTitle("Arcane Mixture");
        this.pageText("""
                To get luminaire extract you must first craft an arcane mixture. Then, luminaire extract could be acquired through the brewing stand, using the arcane mixture as the ingredient and test tubes as the containers. No other ingredients are needed.
                """);

        this.page("spotlight3", () -> BookSpotlightPageModel.create()
                .withTitle(this.context().pageTitle())
                .withText(this.context().pageText())
                .withItem(Ingredient.of(ModItems.FLASK))
        );
        this.pageTitle("Recipients");
        this.pageText("""
                There are two recipients that could contain the quality of Luminaire Extract: \s
                - The Flask can store 5 uses and on consumption grants 45s of Soothe III effect and decrement 15 exhaustion pts immediately.
                - The Tonic can store 10 uses and on consumption grants 45s of Soothe VI effect and decrement 25 exhaustion pts immediately.\n
                Soothe effect will boost the exhaustion recovery.
                """);

        this.page("craftingFlask", () -> BookCraftingRecipePageModel.create()
                .withRecipeId1("ypfundamentals:flask")
                .withRecipeId2("ypfundamentals:tonic")
                //.withText(this.context().pageText())
                .withTitle1(this.context().pageTitle())
        );
        this.pageTitle("Flask");
        this.add("tonicDescription","Tonic Recipe");

        this.page("fillingContainers", () -> BookTextPageModel.create()
                .withText(this.context().pageText())
        );
        this.pageText("""
                To refill a container you must hold it with one of your hands and hold with the opposite hand a test tube filled with luminare extract. 
                Then right click and the test tube will be emptied.
                """);

//        this.page("craftingTonic", () -> BookCraftingRecipePageModel.create()
//
//                //.withText(this.context().pageText())
//                .withTitle1(this.context().pageTitle())
//        );
//        this.pageTitle("Tonic Recipe");


    }

    @Override
    protected String entryName() {
        return "Decrementing Exhaustion";
    }

    @Override
    protected String entryDescription() {
        return "How does the body get rid of exhaustion?";
    }

    @Override
    protected Pair<Integer, Integer> entryBackground() {
        return EntryBackground.DEFAULT;
    }

    @Override
    protected BookIconModel entryIcon() {
        return BookIconModel.create(ModItems.ARCANE_MIXTURE);
    }

    @Override
    protected String entryId() {
        return ID;
    }
}
