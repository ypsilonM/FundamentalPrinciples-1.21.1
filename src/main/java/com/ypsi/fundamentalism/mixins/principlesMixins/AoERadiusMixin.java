package com.ypsi.fundamentalism.mixins.principlesMixins;

import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import io.redspace.ironsspellbooks.entity.spells.EchoingStrikeEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AoeEntity.class)
public class AoERadiusMixin {

    @Unique
    private boolean principleModification = false;

    @ModifyArg(method = "setRadius", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"), index = 0, remap = false)
    private float modifyRadius(float value){
        if(principleModification || !ServerConfig.expansioActive || !ServerConfig.principlesSYSTEM){
            return value;
        }

        AoeEntity magicProjectile = (AoeEntity) (Object) this;
        Entity owner = magicProjectile.getOwner();

        if(magicProjectile  instanceof EchoingStrikeEntity) {
            return value;
        }

        if(owner instanceof ServerPlayer player && !player.level().isClientSide){
            int level = PrinciplesProgressionManager.getCategoryLevel(player, Principles.EXPANSIO);
            float multiplier = Util.getVolumeMultiplier(level);
            value*=multiplier;
            principleModification = true;
        }



        return value;
    }

}
