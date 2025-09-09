package com.ypsi.fundamentalism.item.custom.food;

import com.ypsi.fundamentalism.attachments.ModAttachments;
import com.ypsi.fundamentalism.item.ModFoodProperties;
import com.ypsi.fundamentalism.network.packets.SyncExhaustionPacket;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ManaFruit extends Item {

    public ManaFruit() {
       super(new Item.Properties().food(ModFoodProperties.MANA_FRUIT));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if(!level.isClientSide && livingEntity instanceof ServerPlayer player){
            int currentExhaustion = player.getData(ModAttachments.CURRENT_EXHAUSTION.get());
            player.setData(ModAttachments.CURRENT_EXHAUSTION, Math.clamp(currentExhaustion-10, 0, 100));
            SyncExhaustionPacket.sendToPlayer(player, player.getData(ModAttachments.CURRENT_EXHAUSTION));
        }
        return super.finishUsingItem(stack, level, livingEntity);
    }

}