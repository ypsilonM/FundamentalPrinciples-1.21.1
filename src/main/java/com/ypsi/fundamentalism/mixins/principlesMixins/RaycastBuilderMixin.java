package com.ypsi.fundamentalism.mixins.principlesMixins;

import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.util.RaycastBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RaycastBuilder.class)
public abstract class RaycastBuilderMixin {

    @Shadow (remap = false) @Final private Entity originEntity;
    @Shadow (remap = false) private Vec3 start;
    @Shadow (remap = false) private Vec3 end;
    @Shadow (remap = false) private float bbInflation;

    @Inject(
            method = "performRaycast",
            at = @At("HEAD"),
            remap = false
    )
    private void adjustRaycastRange(CallbackInfoReturnable<HitResult> cir) {
        if (originEntity instanceof ServerPlayer player) {
            double originalDistance = start.distanceTo(end);
            // PERCEPTIO
            int perceptioLevel = PrinciplesProgressionManager.getCategoryLevel(player, Principles.PERCEPTIO);

            float finalRange = ServerConfig.ACTIVE_PERCEPTIO.get() && ServerConfig.PRINCIPLES_SYSTEM.get()?
                    Util.getTotalPerceptioRange(perceptioLevel): 0;
            float baseDetectionRange = ServerConfig.ACTIVE_PERCEPTIO.get() && ServerConfig.PRINCIPLES_SYSTEM.get()?
                    8f + (float) originalDistance / 5f : (float) originalDistance;
            finalRange += baseDetectionRange;

            // LOCUS
            if (bbInflation > 0 && ServerConfig.ACTIVE_LOCUS.get() && ServerConfig.PRINCIPLES_SYSTEM.get()) {
                int locusLevel = PrinciplesProgressionManager.getCategoryLevel(player, Principles.LOCUS);
                finalRange = (float) Util.returnLocusDistance(locusLevel, finalRange);
            }
            Vec3 direction = end.subtract(start).normalize();
            this.end = start.add(direction.scale(finalRange));

        }
    }

}
