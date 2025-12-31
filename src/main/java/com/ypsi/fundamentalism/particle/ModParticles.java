package com.ypsi.fundamentalism.particle;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, FundamentalPrinciples.MOD_ID);

    public static final Supplier<SimpleParticleType> REINFORCEMENT_PARTICLE =
            PARTICLE_TYPES.register("reinforce_particles", () -> new SimpleParticleType(true));
    public static final Supplier<SimpleParticleType> CONSTELLATION_PARTICLE  =
            PARTICLE_TYPES.register("constellation_particle", () -> new SimpleParticleType(true));

    //public static final Supplier<SimpleParticleType> SOL_PARTICLE =
            //PARTICLE_TYPES.register("sol_particle", () -> new SimpleParticleType(true));



    public static void register(IEventBus eventBus){
        PARTICLE_TYPES.register(eventBus);
    }
}
