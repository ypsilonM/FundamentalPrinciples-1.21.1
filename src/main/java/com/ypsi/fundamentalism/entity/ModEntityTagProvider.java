package com.ypsi.fundamentalism.entity;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import javax.swing.text.html.parser.Entity;
import java.util.concurrent.CompletableFuture;

public class ModEntityTagProvider extends EntityTypeTagsProvider {

    public ModEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FundamentalPrinciples.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(EntityTypeTags.UNDEAD).add(ModEntities.HEMOMANCER.get());
        this.tag(EntityTypeTags.SENSITIVE_TO_SMITE).add(ModEntities.HEMOMANCER.get());
        this.tag(EntityTypeTags.SKELETONS).add(ModEntities.HEMOMANCER.get());
        this.tag(EntityTypeTags.WITHER_FRIENDS).add(ModEntities.HEMOMANCER.get());
        this.tag(EntityTypeTags.INVERTED_HEALING_AND_HARM).add(ModEntities.HEMOMANCER.get());
        this.tag(EntityTypeTags.IGNORES_POISON_AND_REGEN).add(ModEntities.HEMOMANCER.get());

        this.tag(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS).add(ModEntities.VENEMERUS.get());
        this.tag(EntityTypeTags.IGNORES_POISON_AND_REGEN).add(ModEntities.VENEMERUS.get());
        this.tag(EntityTypeTags.ARTHROPOD).add(ModEntities.VENEMERUS.get());
    }
}
