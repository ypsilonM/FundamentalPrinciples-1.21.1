package com.ypsi.fundamentalism.mixins.principlesMixins;

import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.attachments.SpellCategoryProgression;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerRecasts;
import io.redspace.ironsspellbooks.capabilities.magic.RecastInstance;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(PlayerRecasts.class)
public abstract class PlayerRecastsMixin {

    @Shadow(remap = false)
    private Map<String, RecastInstance> recastLookup;
    @Shadow(remap = false)
    private ServerPlayer serverPlayer;

    @Shadow(remap = false)
    public boolean isRecastActive(RecastInstance recastInstance) {
        return false;
    }
    @Shadow(remap = false)
    public void syncToPlayer(RecastInstance recastInstance) {}

    @Inject(method = "addRecast", at = @At("HEAD"), cancellable = true, remap = false)
    private void onAddRecastWithExtra(RecastInstance recastInstance, MagicData magicData, CallbackInfoReturnable<Boolean> cir) {
        if (serverPlayer != null) {

            Player player = serverPlayer;
            String spellId = recastInstance.getSpellId();
            Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spellId);
            double probability = 0;
            int totalRecasts = recastInstance.getTotalRecasts();

            if (categories.contains("hasRecasts")) {
                int categoryLevel = SpellCategoryProgression.getCategoryLevel(player, Principles.REPETITIO);
                probability = Util.getRecastAddChance(categoryLevel);
            }
            boolean isAdded = serverPlayer.getRandom().nextDouble()< probability;
            if (isAdded && totalRecasts>2) {
                RecastInstance modifiedRecast = new RecastInstance(
                        recastInstance.getSpellId(),
                        recastInstance.getSpellLevel(),
                        recastInstance.getTotalRecasts() + 1,
                        recastInstance.getTicksToLive(),
                        recastInstance.getCastSource(),
                        recastInstance.getCastData()
                );

                var existingRecastInstance = recastLookup.get(modifiedRecast.getSpellId());

                if (!isRecastActive(existingRecastInstance)) {
                    magicData.getPlayerCooldowns().removeCooldown(modifiedRecast.getSpellId());
                    recastLookup.put(modifiedRecast.getSpellId(), modifiedRecast);
                    syncToPlayer(modifiedRecast);
                    cir.setReturnValue(true);
                    cir.cancel();
                }
            }
        }
    }


}
