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
//        Entity entity = (Entity)(Object)this;
//        if (!isGlowEffectContext()) {
//            return;
//        }
//        if (entity instanceof LivingEntity living && living.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {
//            if (living instanceof Player player) {
//                int magicColor = getMagicAff(player);
//                cir.setReturnValue(magicColor);
//            }
//        }
    }
    private boolean isGlowEffectContext() {
//        // Verificar que estamos en el contexto correcto para evitar afectar el inventario
//        Minecraft mc = Minecraft.getInstance();
//
//        // 1. No aplicar si estamos en cualquier pantalla
//        if (mc.screen != null) {
//            return false;
//        }
//
//        // 2. No aplicar si no hay mundo (contexto de GUI)
//        if (mc.level == null) {
//            return false;
//        }
//
//        // 3. Verificar el stack de llamadas para asegurarnos que es para glow
//        try {
//            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
//            for (int i = 0; i < Math.min(stack.length, 8); i++) {
//                String methodName = stack[i].getMethodName();
//                String className = stack[i].getClassName();
//
//                // Buscar métodos específicos de rendering de glow/outline
//                if (methodName.contains("glow") ||
//                        methodName.contains("Outline") ||
//                        methodName.contains("renderGlow") ||
//                        className.contains("Outline")) {
//                    return true;
//                }
//
//                // Evitar contextos de GUI/inventario
//                if (className.contains("Screen") ||
//                        className.contains("Gui") ||
//                        methodName.contains("renderEntityInInventory")) {
//                    return false;
//                }
//            }
//        } catch (Exception e) {
//            return false; // En caso de error, mejor no modificar
//        }
//
//        // Por defecto, no modificar para evitar problemas
//        return false;
        return true;
    }



}
