package com.ypsi.fundamentalism.datagen;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

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
        basicItem(ModItems.MAGICAL_SHIELD.get());

        generateTonicModels();

        withExistingParent(ModItems.HEMOMANCER_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.IMP_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        withExistingParent(ModItems.VENEMERUS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));

//        withExistingParent(ModItems.MAGICAL_SHIELD.getId().getPath(), mcLoc("item/shield"))
//                .texture("layer0", modLoc("item/magical_shield"))
//                .override()
//                .predicate(ResourceLocation.fromNamespaceAndPath( FundamentalPrinciples.MOD_ID, "blocking"), 1)
//                .model(withExistingParent(
//                        ModItems.MAGICAL_SHIELD.getId().getPath() + "_blocking",
//                        mcLoc("item/shield_blocking"))
//                        .texture("layer0", modLoc("item/magical_shield")))
//                .end();
    }

    private void generateTonicModels() {
        String tonicPath = ModItems.TONIC.getId().getPath();
        for (int charges = 0; charges <= 10; charges++) {
            withExistingParent(tonicPath + "_" + charges, mcLoc("item/generated"))
                    .texture("layer0", modLoc("item/tonic_" + charges));
        }
    }

}
