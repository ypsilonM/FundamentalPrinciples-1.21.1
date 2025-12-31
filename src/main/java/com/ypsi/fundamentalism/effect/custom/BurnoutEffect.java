package com.ypsi.fundamentalism.effect.custom;

import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.event.ModEvents;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.extensions.IMobEffectExtension;

import java.util.Set;

@EventBusSubscriber()
public class BurnoutEffect extends MobEffect implements IMobEffectExtension {

    public BurnoutEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @SubscribeEvent
    public static void cancelCast(SpellPreCastEvent event){
        var effect = event.getEntity().getEffect(ModEffects.BURNOUT_EFFECT);
        if( effect != null ){
            Player caster = event.getEntity();
            AbstractSpell spell = SpellRegistry.getSpell(event.getSpellId());
            MagicData magicData = MagicData.getPlayerMagicData(caster);
            ModEvents.cancelCast(event, magicData, (ServerPlayer) caster, spell);
        }
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {

    }
}
