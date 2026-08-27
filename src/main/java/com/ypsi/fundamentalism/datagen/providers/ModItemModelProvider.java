package com.ypsi.fundamentalism.datagen.providers;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import top.theillusivec4.curios.api.CuriosApi;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, FundamentalPrinciples.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.MANA_FRUIT.get());
        basicItem(ModItems.ARCANE_MIXTURE.get());
        basicItem(ModItems.TEST_TUBE.get());
        basicItem(ModItems.PITCHER_EXTRACT.get());
        basicItem(ModItems.LUMINAIRE_EXTRACT.get());
        basicItem(ModItems.FLASK.get());
        basicItem(ModItems.URSIDAE_FUR.get());

        basicItem(ModItems.ANCIENT_SCROLL_CASE.get());
        basicItem(ModItems.SPELLBOOK_COVER.get());
        basicItem(ModItems.NOVICE_SPELLBOOK_COVER.get());
        basicItem(ModItems.ADEPT_SPELLBOOK_COVER.get());
        basicItem(ModItems.SORCERER_SPELLBOOK_COVER.get());
        basicItem(ModItems.SCHOLAR_SPELLBOOK_COVER.get());
        basicItem(ModItems.ARCHMAGE_SPELLBOOK_COVER.get());

        basicItem(ModItems.HIRSUTE_NECKLACE.get());

        generateTonicModels();

        withExistingParent(ModItems.HEMOMANCER_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.IMP_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.VENEMERUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.RUNEAR_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.CHERRY_BIRD_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));

        registerCustomSwordModel();

    }


    private void generateTonicModels() {
        String tonicPath = ModItems.TONIC.getId().getPath();
        for (int charges = 0; charges <= 10; charges++) {
            withExistingParent(tonicPath + "_" + charges, mcLoc("item/generated"))
                    .texture("layer0", modLoc("item/tonic/ton_" + charges));
        }
    }
    private void registerCustomSwordModel() {
        String swordPath = ModItems.NULLIFIER.getId().getPath();
        ItemModelBuilder swordModel = withExistingParent(swordPath, mcLoc("item/handheld"))
                .texture("layer0", modLoc("item/" + swordPath));

        swordModel.transforms()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                .rotation(50, 90, 135)
                .translation(0.5f, -7.5f,4.5f)
                .scale(1.0f, 1.0f, 1.0f)
                .end()
                .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND)
                .rotation(50, -90, -135)
                .translation(0.5f, -7.5f,4.5f)
                .scale(1.0f, 1.0f, 1.0f)
                .end();
    }
}
