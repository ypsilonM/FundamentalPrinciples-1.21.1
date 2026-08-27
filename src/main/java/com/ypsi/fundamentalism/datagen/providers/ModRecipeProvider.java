package com.ypsi.fundamentalism.datagen.providers;

import com.ypsi.fundamentalism.item.ModFluids;
import com.ypsi.fundamentalism.item.ModItems;
import io.redspace.ironsspellbooks.recipe_types.alchemist_cauldron.BrewAlchemistCauldronRecipe;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.loading.ModDirTransformerDiscoverer;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.fluids.FluidStack;
import software.bernie.geckolib.event.GeoRenderEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.redspace.ironsspellbooks.datagen.IronRecipeProvider.cauldronTwoWayInteraction;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ARCANE_MIXTURE.get())
                .requires(ItemRegistry.ARCANE_ESSENCE.get())
                .requires(Items.BOWL)
                .requires(Items.GLOW_BERRIES)
                .requires(Items.SPIDER_EYE)
                .unlockedBy("has_essence", has(ItemRegistry.ARCANE_ESSENCE.get())).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BREWING, ModItems.TEST_TUBE.get())
                .requires(Items.GLASS_PANE)
                .unlockedBy("has_glass", has(Items.GLASS)).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MANA_FRUIT.get())
                .requires(Items.GOLDEN_APPLE)
                .requires(ModItems.PITCHER_EXTRACT)
                .requires(ItemRegistry.CINDER_ESSENCE.get())
                .unlockedBy("has_cinder_essence", has(ItemRegistry.CINDER_ESSENCE.get())).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.TONIC.get())
                .pattern(" C ")
                .pattern("IBI")
                .pattern("IMI")
                .define('C', ItemRegistry.CINDER_ESSENCE.get())
                .define('I', ItemRegistry.ARCANE_INGOT.get())
                .define('B', Items.GLASS_BOTTLE)
                .define('M', ItemRegistry.MITHRIL_SCRAP.get())
                .unlockedBy("has_cinder", has(ItemRegistry.CINDER_ESSENCE.get())).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.FLASK.get())
                .pattern(" A ")
                .pattern("IBI")
                .pattern("IPI")
                .define('A', ItemRegistry.ARCANE_ESSENCE.get())
                .define('I', Items.IRON_INGOT)
                .define('B', Items.GLASS_BOTTLE)
                .define('P', Items.BLAZE_POWDER)
                .unlockedBy("default_powder", has(Items.BLAZE_POWDER)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.NULLIFIER.get())
                .pattern("NOC")
                .pattern("OSO")
                .pattern("WON")
                .define('N', Items.NETHERITE_INGOT)
                .define('O', ItemRegistry.PROTECTION_UPGRADE_ORB.get())
                .define('C', ItemRegistry.DIVINE_SOULSHARD.get())
                .define('S', Items.NETHER_STAR)
                .define('W', ItemRegistry.WEAPON_PARTS.get())
                .unlockedBy("weapon_part_null", has(ItemRegistry.WEAPON_PARTS.get())).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SPELLBOOK_COVER.get())
                .pattern("LL ")
                .pattern("SI ")
                .pattern("LL ")
                .define('L', Items.LEATHER)
                .define('S', Items.STRING)
                .define('I', Items.ITEM_FRAME)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NOVICE_SPELLBOOK_COVER.get())
                .pattern("AAG")
                .pattern("ACG")
                .pattern("GGA")
                .define('C', ModItems.SPELLBOOK_COVER)
                .define('A', ItemRegistry.ARCANE_ESSENCE.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_cover", has(ModItems.SPELLBOOK_COVER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ADEPT_SPELLBOOK_COVER.get())
                .pattern("ASI")
                .pattern("SCS")
                .pattern("ISA")
                .define('C', ModItems.SPELLBOOK_COVER)
                .define('A', ItemRegistry.ARCANE_ESSENCE.get())
                .define('I', ItemRegistry.ARCANE_INGOT.get())
                .define('S', Items.AMETHYST_SHARD)
                .unlockedBy("has_cover", has(ModItems.SPELLBOOK_COVER))
                .save(recipeOutput);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SORCERER_SPELLBOOK_COVER.get())
                .pattern("AEI")
                .pattern("ECE")
                .pattern("IEA")
                .define('C', ModItems.SPELLBOOK_COVER)
                .define('A', ItemRegistry.ARCANE_ESSENCE.get())
                .define('I', ItemRegistry.MITHRIL_INGOT.get())
                .define('E', Items.ECHO_SHARD)
                .unlockedBy("has_cover", has(ModItems.SPELLBOOK_COVER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SCHOLAR_SPELLBOOK_COVER.get())
                .pattern("AQI")
                .pattern("QCA")
                .pattern("IAQ")
                .define('C', ModItems.SPELLBOOK_COVER)
                .define('A', ItemRegistry.CINDER_ESSENCE.get())
                .define('I', Items.NETHERITE_INGOT)
                .define('Q', Items.QUARTZ)
                .unlockedBy("has_cover", has(ModItems.SPELLBOOK_COVER))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ARCHMAGE_SPELLBOOK_COVER.get())
                .pattern("ADI")
                .pattern("DCA")
                .pattern("IAD")
                .define('C', ModItems.SPELLBOOK_COVER)
                .define('A', ItemRegistry.CINDER_ESSENCE.get())
                .define('I', ItemRegistry.PYRIUM_INGOT.get())
                .define('D', ItemRegistry.DRAGONSKIN.get())
                .unlockedBy("has_cover", has(ModItems.SPELLBOOK_COVER))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, ModItems.HIRSUTE_NECKLACE)
                .requires(ModItems.URSIDAE_FUR.get(), 1)
                .requires(ItemRegistry.HEAVY_CHAIN.get(), 1)
                .unlockedBy("has_heavy_chain", has(ItemRegistry.HEAVY_CHAIN.get()))
                .save(recipeOutput);



        BrewAlchemistCauldronRecipe.builder()
                .withInput(new FluidStack(Fluids.WATER, 1000))
                .withReagent(ModItems.ARCANE_MIXTURE.get())
                .withResult(ModFluids.ARCANE_MIXTURE, 1000)
                .save(recipeOutput);

        BrewAlchemistCauldronRecipe.builder()
                .withInput(new FluidStack(Fluids.WATER, 1000))
                .withReagent(Items.PITCHER_PLANT)
                .withResult(ModFluids.PITCHER_EXTRACT, 1000)
                .save(recipeOutput);

        cauldronRecipientInteraction(recipeOutput, ModItems.LUMINAIRE_EXTRACT, ModFluids.ARCANE_MIXTURE);
        cauldronRecipientInteraction(recipeOutput, ModItems.PITCHER_EXTRACT, ModFluids.PITCHER_EXTRACT);




//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ORB.get(), 9)
//                .requires(YpsBlocks.MANA_BLOCK)
//                .unlockedBy("has_mana_block", has(YpsBlocks.MANA_BLOCK)).save(recipeOutput);
//
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ORB.get(), 18)
//                .requires(YpsBlocks.MAGIC_BLOCK)
//                .unlockedBy("has_magic_block", has(YpsBlocks.MAGIC_BLOCK))
//                .save(recipeOutput, "ypfundamentals:orb_from_magic_block");
//
//        oreSmelting(recipeOutput,MANA_SMELTABLES, RecipeCategory.MISC, ModItems.ORB.get(), 0.25f,200, "mana");
//        oreBlasting(recipeOutput,MANA_SMELTABLES, RecipeCategory.MISC, ModItems.ORB.get(), 0.25f,100, "mana");
    }

    public static void cauldronRecipientInteraction(RecipeOutput output, Holder<Item> item, Holder<Fluid> fluid) {
        cauldronTwoWayInteraction(output, item, Holder.direct(ModItems.TEST_TUBE.get()), fluid, 250);
    }
}
