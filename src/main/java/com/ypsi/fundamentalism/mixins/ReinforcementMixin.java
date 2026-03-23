package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class ReinforcementMixin {
    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true, remap = false)
    private void onGetTeamColor(CallbackInfoReturnable<Integer> cir) {
        Entity entity = (Entity) (Object) this;

        if (entity instanceof Player player &&
                player.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {

            int color = Utils.packRGB(Util.getElementalColor(player));
            cir.setReturnValue(color);
        }
    }
}
