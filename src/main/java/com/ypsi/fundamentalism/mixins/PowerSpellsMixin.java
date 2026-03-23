package com.ypsi.fundamentalism.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.ypsi.fundamentalism.Config;
import com.ypsi.fundamentalism.attachments.AvailableSpellsAttachment;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.attachments.SpellCategoryProgression;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

    @ModifyReturnValue(method = "getSpellPower", at = @At("RETURN"), remap = false)
    private float multiplySpecificSpellPower(float original, int spellLevel, @Nullable Entity sourceEntity) {
        if(sourceEntity instanceof Player player) {
            String id = this.getSpellId();
            Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(id);
            float modifier = 0f;

            for (String category : categories) {
                int levelCat = SpellCategoryProgression.getCategoryLevel((Player) sourceEntity, category);
                    //Subcategories
                    if(category.contains("usesShoot") || category.contains("createsAoeEntities") || category.contains("usesSummon")) {
                        modifier += getSubEntityModificator(levelCat, original);
                    }else{
                        modifier += getModificator(levelCat, original);
                    }
            }
            int exLvl = player.getData(YpsAttachments.LEVEL_EXHAUSTION.get());
            List<Double> fatiguePenalties = Config.fatiguePenalties;
            float ratio = (float) (double)fatiguePenalties.get(exLvl);

            return (original + modifier) * ratio;
        }
        return original;
    }

    @ModifyReturnValue(method = "getLevelFor", at = @At("RETURN"), remap = false)
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


    private float getModificator(int level, float base){
        double basePercentage = -0.10 + (0.01*level);
        return (float) (base*basePercentage);
    }
    private float getSubEntityModificator(int level, float base){
        double basePercentage = -0.05 + (0.005*level);
        return (float) (base*basePercentage);
    }


//    private void algoxd(){
//        AvailableSpellsAttachment av = player.getData(YpsAttachments.SPELL_LIST);
//        if (av.hasSpell(spellId)) {
//            int reduction = av.getSpellLevel(spellId);
//            int oldLevel = event.getSpellLevel();
//            int newLevel = Math.max(1, oldLevel - reduction);
//            event.setSpellLevel(newLevel);
//            player.sendSystemMessage(
//                    Component.literal("Level Casted: " + newLevel).withStyle(ChatFormatting.GOLD),
//                    true
//            );
//        }
//    }

}
