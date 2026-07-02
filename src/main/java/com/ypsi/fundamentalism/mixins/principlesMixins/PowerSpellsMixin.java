package com.ypsi.fundamentalism.mixins.principlesMixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.attachments.customAtt.AvailableSpellsAttachment;
import com.ypsi.fundamentalism.attachments.FatigueManager;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.principleGen.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

//client and server
@Mixin(AbstractSpell.class)
public abstract class PowerSpellsMixin {

    @Shadow(remap = false)
    public abstract String getSpellId();

    @ModifyReturnValue(
            method = "getSpellPower",
            at = @At("RETURN"),
            remap = false
    )
    private float multiplySpecificSpellPower(float original, int spellLevel, @Nullable Entity sourceEntity) {
        if(sourceEntity instanceof Player player) {
            String id = this.getSpellId();
            Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(id);
            float modifier = 0f;

            if(ServerConfig.PRINCIPLES_SYSTEM.get()) {
                for (String category : categories) {
                    int levelCat = PrinciplesProgressionManager.getCategoryLevel(player, category);
                    //Subcategories
                    if ((category.contains("usesShoot") || category.contains("createsAoeEntities") ||
                            category.contains("usesSummon") || category.contains("usesTargeting")) && ServerConfig.SUBCATEGORIES_HALF.get()) {
                        modifier += Util.getSubEntityModificator(levelCat, original);
                    } else {
                        modifier += Util.getModificator(levelCat, original);
                    }
                }
            }
            //modifier = Math.clamp(modifier, 0, 1000);
            int exLvl = FatigueManager.getFatigueLevel(player);
            //int exLvl = player.getData(YpsAttachments.LEVEL_EXHAUSTION.get());

            List<Double> fatiguePenalties = (List<Double>) ServerConfig.FATIGUE_SPELLPOWER.get();
            float ratio = 1;

            if(ServerConfig.FATIGUE_SYSTEM.get())
                ratio = (float) (double) fatiguePenalties.get(exLvl);

            return (original + modifier) * ratio;
        }
        return original;
    }

    @ModifyReturnValue(
            method = "getLevelFor",
            at = @At("RETURN"),
            remap = false
    )
    private int modifySpellLevel(int original, int level, @Nullable LivingEntity caster) {
        if (caster instanceof Player player) {
            AvailableSpellsAttachment av = player.getData(YpsAttachments.SPELL_LIST);
            String spellId = getSpellId();
            if (av.hasSpell(spellId)) {
                int reduction = av.getSpellLevel(spellId);
                return  Math.max(1, level - reduction);
            }
        }
        return original;
    }




}
