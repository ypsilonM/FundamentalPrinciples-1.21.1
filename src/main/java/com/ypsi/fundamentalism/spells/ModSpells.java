package com.ypsi.fundamentalism.spells;


import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.spells.blood.BloodMoonSpell;
import com.ypsi.fundamentalism.spells.blood.ThornSpell;
import com.ypsi.fundamentalism.spells.eldritch.VoidMirrorSpell;
import com.ypsi.fundamentalism.spells.ender.LapsusSpell;
import com.ypsi.fundamentalism.spells.ender.PullSpell;
import com.ypsi.fundamentalism.spells.evocation.CopycatSpell;
import com.ypsi.fundamentalism.spells.blood.BloodstreamSpell;
import com.ypsi.fundamentalism.spells.evocation.TauntSpell;
import com.ypsi.fundamentalism.spells.fire.FlameStrengthSpell;
import com.ypsi.fundamentalism.spells.fire.PyrokinesisSpell;
import com.ypsi.fundamentalism.spells.fire.SolSpell;
import com.ypsi.fundamentalism.spells.holy.HolyLightningSpell;
import com.ypsi.fundamentalism.spells.ice.ChainsSpell;
import com.ypsi.fundamentalism.spells.nature.YggdrasilSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSpells {

    public static final DeferredRegister<AbstractSpell> YPSPELLS =
            DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, FundamentalPrinciples.MOD_ID);

    public static void register(IEventBus eventBus){
        YPSPELLS.register(eventBus);
    }
    public static Supplier<AbstractSpell> registerSpell (AbstractSpell spell){
        return YPSPELLS.register(spell.getSpellName(), () -> spell);
    }
    //------------>ice<----------------
    public static final Supplier<AbstractSpell> CHAINS = registerSpell(new ChainsSpell());
    //------------>fire<----------------
    public static final Supplier<AbstractSpell> FLAME_GRANT_STRENGTH = registerSpell(new FlameStrengthSpell());
    public static final Supplier<AbstractSpell> PYROKINESIS = registerSpell(new PyrokinesisSpell());
    public static final Supplier<AbstractSpell> SOL_SPELL = registerSpell(new SolSpell());
    //------------>blood<----------------
    public static final Supplier<AbstractSpell> BLOODSTREAM = registerSpell(new BloodstreamSpell());
    public static final Supplier<AbstractSpell> THORN = registerSpell(new ThornSpell());
//    public static final Supplier<AbstractSpell> BLOOD_MOON_SPELL = registerSpell(new BloodMoonSpell());
    //------------>holy<----------------
    public static final Supplier<AbstractSpell> HOLY_LIGHTNING = registerSpell(new HolyLightningSpell());
    //------------>nature<----------------
    //public static final Supplier<AbstractSpell> YGGDRASIL = registerSpell(new YggdrasilSpell());
    //------------>ender<----------------
    public static final Supplier<AbstractSpell> PULL = registerSpell(new PullSpell());
    public static final Supplier<AbstractSpell> LAPSUS = registerSpell(new LapsusSpell());
    //------------>evocation<----------------
    public static final Supplier<AbstractSpell> TAUNT = registerSpell(new TauntSpell());
    //public static final Supplier<AbstractSpell> COPYCAT = registerSpell(new CopycatSpell());
    //------------>eldritch<----------------


}
