package com.ypsi.fundamentalism.spells;


import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.spells.blood.LacerationSpell;
import com.ypsi.fundamentalism.spells.blood.ThornSpell;
import com.ypsi.fundamentalism.spells.ender.LapsusSpell;
import com.ypsi.fundamentalism.spells.ender.PullSpell;
import com.ypsi.fundamentalism.spells.blood.BloodstreamSpell;
import com.ypsi.fundamentalism.spells.evocation.TauntSpell;
import com.ypsi.fundamentalism.spells.fire.BurningSpiritSpell;
import com.ypsi.fundamentalism.spells.fire.IgniteSpell;
import com.ypsi.fundamentalism.spells.fire.PyrokinesisSpell;
import com.ypsi.fundamentalism.spells.fire.SolSpell;
import com.ypsi.fundamentalism.spells.fundamentalism.LawOfRegressionSpell;
import com.ypsi.fundamentalism.spells.holy.HolyLightningSpell;
import com.ypsi.fundamentalism.spells.holy.SacredDiskSpell;
import com.ypsi.fundamentalism.spells.ice.ChainsSpell;
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
    public static final Supplier<AbstractSpell> FLAME_GRANT_STRENGTH = registerSpell(new BurningSpiritSpell());
    public static final Supplier<AbstractSpell> PYROKINESIS = registerSpell(new PyrokinesisSpell());
    public static final Supplier<AbstractSpell> SOL_SPELL = registerSpell(new SolSpell());
    public static final Supplier<AbstractSpell> IGNITE_SPELL = registerSpell(new IgniteSpell());

    //------------>blood<----------------
    public static final Supplier<AbstractSpell> BLOODSTREAM = registerSpell(new BloodstreamSpell());
    public static final Supplier<AbstractSpell> THORN = registerSpell(new ThornSpell());
    public static final Supplier<AbstractSpell> LACERATION = registerSpell(new LacerationSpell());

    //------------>holy<----------------
    public static final Supplier<AbstractSpell> HOLY_LIGHTNING = registerSpell(new HolyLightningSpell());
    public static final Supplier<AbstractSpell> SACRED_DISK = registerSpell(new SacredDiskSpell());
    //------------>nature<----------------
    //public static final Supplier<AbstractSpell> YGGDRASIL = registerSpell(new YggdrasilSpell());
    //------------>ender<----------------
    public static final Supplier<AbstractSpell> PULL = registerSpell(new PullSpell());
    public static final Supplier<AbstractSpell> LAPSUS = registerSpell(new LapsusSpell());
    //------------>evocation<----------------
    public static final Supplier<AbstractSpell> TAUNT = registerSpell(new TauntSpell());

    //public static final Supplier<AbstractSpell> COPYCAT = registerSpell(new CopycatSpell());
    //------------>eldritch<----------------
    //public static final Supplier<AbstractSpell> STEAL_SUMMON = registerSpell(new StealSummonSpell());
//    public static final Supplier<AbstractSpell> MIRROR = registerSpell(new VoidMirrorSpell());

    //public static final Supplier<AbstractSpell> FUNDAMENTALFIRST = registerSpell(new VoidMirrorSpell());

    //FUNDAMENTALISM
    //public static final Supplier<AbstractSpell> PROIECTUM_PRINCIPLE = registerSpell(new ProiectumSpell());

    public static final Supplier<AbstractSpell> REMEDIUM_SPELL = registerSpell(new LawOfRegressionSpell());



}
