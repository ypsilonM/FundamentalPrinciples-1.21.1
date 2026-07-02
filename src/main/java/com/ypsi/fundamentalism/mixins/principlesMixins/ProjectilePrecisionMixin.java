package com.ypsi.fundamentalism.mixins.principlesMixins;

import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractMagicProjectile.class)
public class ProjectilePrecisionMixin {

    @ModifyVariable(
            method = "shoot",
            at= @At("HEAD"),
            argsOnly = true,
            remap = false
    )
    private Vec3 modifyShootDirection(Vec3 original){
        AbstractMagicProjectile magicProjectile = (AbstractMagicProjectile)(Object) this;
        Entity owner = magicProjectile.getOwner();
        if(owner instanceof ServerPlayer player && !player.level().isClientSide
                && ServerConfig.ACTIVE_POTENTIA.get()
                && ServerConfig.PRINCIPLES_SYSTEM.get())
        {
            int level = PrinciplesProgressionManager.getCategoryLevel(player, Principles.POTENTIA);
            float accuracy = Util.getAccuracy(level);
            return applyAccuracyVariation(original, accuracy, player.getRandom());
        }
        return original;
    }

    private Vec3 applyAccuracyVariation(Vec3 direction, float precision, RandomSource random) {
        float inaccuracy = 1.0f - precision;
        if (inaccuracy <= 0.0f) return direction;

        float maxAngleSpread = 0.35f;
        float spread = inaccuracy * maxAngleSpread;

        float randomYaw = (random.nextFloat() - 0.5f) * spread;
        float randomPitch = (random.nextFloat() - 0.5f) * spread * 0.5f;

        Quaternionf rotation = new Quaternionf()
                .rotateY(randomYaw)
                .rotateX(randomPitch);

        Vector3f vec = new Vector3f((float)direction.x, (float)direction.y, (float)direction.z);
        vec.rotate(rotation);
        vec.normalize();

        return new Vec3(vec.x(), vec.y(), vec.z());
    }
}
