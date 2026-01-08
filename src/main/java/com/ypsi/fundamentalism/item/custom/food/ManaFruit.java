package com.ypsi.fundamentalism.item.custom.food;

import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.item.ModFoodProperties;
import com.ypsi.fundamentalism.network.packets.SyncExhaustionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import static com.ypsi.fundamentalism.event.ModEvents.getMaxExPerLevel;

public class ManaFruit extends Item {

    public ManaFruit(Rarity rarity) {
       super(new Item.Properties().food(ModFoodProperties.MANA_FRUIT).rarity(rarity));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if(!level.isClientSide && livingEntity instanceof ServerPlayer player){
            int currentExhaustion = player.getData(YpsAttachments.CURRENT_EXHAUSTION.get());
            player.setData(YpsAttachments.CURRENT_EXHAUSTION, Math.max(currentExhaustion-50, 0));
            SyncExhaustionPacket.sendToPlayer(player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.HONEY_DRINK;
    }
}