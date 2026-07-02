package com.ypsi.fundamentalism.spells;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.attributes.YpsDamageTypes;
import com.ypsi.fundamentalism.util.Tags;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class YpsSchoolRegistry extends SchoolRegistry {

    private static final DeferredRegister<SchoolType> FUNDAMENTAL_SCHOOLS =
            DeferredRegister.create(SchoolRegistry.SCHOOL_REGISTRY_KEY, FundamentalPrinciples.MOD_ID);

    //Fundamentalism
    public static final ResourceLocation FUNDAMENTAL_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "fundamentalism");

    private static Supplier<SchoolType> registerSchool(SchoolType schoolType) {
        return FUNDAMENTAL_SCHOOLS.register(schoolType.getId().getPath(), () -> schoolType);
    }

    public static final Supplier<SchoolType> FUNDAMENTALISM = registerSchool(new SchoolType(
            FUNDAMENTAL_RESOURCE,
            ModTags.SCHOOL_FOCUS,
            Component.translatable("school.ypfundamentals.fundamentalism").withStyle(ChatFormatting.GOLD),
            YpsAttributes.FUNDAMENTALISM_SPELL_POWER,
            YpsAttributes.FUNDAMENTALISM_MAGIC_RESIST,
            SoundRegistry.HOLY_CAST,
            YpsDamageTypes.FUNDAMENTAL_DAMAGE
    ));



    public static void register(IEventBus eventBus){
        FUNDAMENTAL_SCHOOLS.register(eventBus);
    }

}
