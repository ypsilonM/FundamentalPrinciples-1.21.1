package com.ypsi.fundamentalism.util;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class Tags {

    public static final TagKey<Item> FUNDAMENTAL_FOCUS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "fundamental_focus"));

}
