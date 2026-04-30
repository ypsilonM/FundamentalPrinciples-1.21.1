package com.ypsi.fundamentalism.item.custom.food;

import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.attachments.FatigueManager;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.item.ModFoodProperties;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;


public class ManaFruit extends Item {

    public ManaFruit(Rarity rarity) {
       super(new Item.Properties().food(ModFoodProperties.MANA_FRUIT).rarity(rarity));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if(!level.isClientSide && livingEntity instanceof ServerPlayer player){
            if(ServerConfig.fatigueSystem) {
                int currentExhaustion = FatigueManager.getFatigueAmount(player);
                FatigueManager.setFatigueAmount(player, Math.max(currentExhaustion - 50, 0));
            }
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.HONEY_DRINK;
    }
}