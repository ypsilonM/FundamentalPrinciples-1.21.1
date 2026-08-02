package com.ypsi.fundamentalism.item.custom;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class SpellbookCover extends Item {

    public SpellbookCover(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.ypfundamentals.cover_description"));
        if(Screen.hasAltDown()) {
            tooltipComponents.add(Component.translatable("tooltip.ypfundamentals.cover_description2"));
        }else{
            tooltipComponents.add(Component.translatable("tooltip.ypfundamentals.cover_alt"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
