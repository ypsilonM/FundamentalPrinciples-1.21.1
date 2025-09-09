package com.ypsi.fundamentalism.mixin;


import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.util.Pair;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.management.Attribute;
import java.util.ArrayList;
import java.util.List;


@Mixin(Entity.class)
public class ClientEntityMixinM{

    @Inject(method = "getTeamColor", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void changeGlowOutline(CallbackInfoReturnable<Integer> cir) {
        Entity entity = (Entity)(Object)this;
        if (entity instanceof LivingEntity living && living.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {
            if (living instanceof Player player) {
                int magicColor = getMagicAff(player);
                cir.setReturnValue(magicColor);
            }
        }
    }

    public Integer getMagicAff(Player player){
        List<Pair> list = new ArrayList<>();
        list.add(new Pair(player.getAttributeValue(AttributeRegistry.FIRE_SPELL_POWER), 0xf57c00));
        list.add(new Pair(player.getAttributeValue(AttributeRegistry.ICE_SPELL_POWER), 0x42a5f5));
        list.add(new Pair(player.getAttributeValue(AttributeRegistry.LIGHTNING_SPELL_POWER), 0x1a237e));
        list.add(new Pair(player.getAttributeValue(AttributeRegistry.EVOCATION_SPELL_POWER), 0xffffff));
        list.add(new Pair(player.getAttributeValue(AttributeRegistry.NATURE_SPELL_POWER), 0x76ff03));
        list.add(new Pair(player.getAttributeValue(AttributeRegistry.BLOOD_SPELL_POWER), 0xd50000));
        list.add(new Pair(player.getAttributeValue(AttributeRegistry.HOLY_SPELL_POWER), 0xffeb3b));
        list.add(new Pair(player.getAttributeValue(AttributeRegistry.ENDER_SPELL_POWER), 0x9c27b0));
        list.add(new Pair(player.getAttributeValue(AttributeRegistry.ELDRITCH_SPELL_POWER), 0x000000));

        double max = list.stream()
                .mapToDouble(Pair::getNumber)
                .max()
                .orElse(Double.NEGATIVE_INFINITY);

        long countMax = list.stream()
                .filter(p -> p.getNumber() == max)
                .count();

        if (countMax > 1) {
            return 0xffffff;
        }
        return list.stream()
                .filter(p -> p.getNumber() == max)
                .findFirst()
                .map(Pair::getColor)
                .orElse(0xffffff);
    }


}
