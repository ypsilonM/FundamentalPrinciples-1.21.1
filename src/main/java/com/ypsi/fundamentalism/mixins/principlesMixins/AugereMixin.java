package com.ypsi.fundamentalism.mixins.principlesMixins;

import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Utils.class)
public abstract class AugereMixin {
    @Inject(method = "getWeaponDamage", at = @At("RETURN"), cancellable = true, remap = false)
    private static void modifyAugerePotentiation(LivingEntity entity, CallbackInfoReturnable<Float> cir){
        float original = cir.getReturnValue();
        if(original > 0 && entity instanceof Player player){
            int augereLvl = PrinciplesProgressionManager.getCategoryLevel(player, Principles.AUGERE);
            cir.setReturnValue(original + Util.getAdditionalWeaponDamage(augereLvl));
        }
    }
}
