package com.ypsi.fundamentalism.spells;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.minecraft.resources.ResourceLocation;

public class Animations {
    public static ResourceLocation ANIM_RESOURCE = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "animation");

    public static final AnimationHolder IGNITE_CAST = new AnimationHolder(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "ignite"), true, true);

    public static final AnimationHolder SPIRIT_CHARGE = new AnimationHolder(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "spirit_charge"), true, false);
    public static final AnimationHolder SPIRIT_END = new AnimationHolder(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "spirit_end"), true, false);

    public static final AnimationHolder LACERATION_CHARGE = new AnimationHolder(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "laceration_charge"), true, true);
    public static final AnimationHolder LACERATION_END = new AnimationHolder(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "laceration_slash"), true, true);

    public static final AnimationHolder TONATIUH_START = new AnimationHolder(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "tonatiuh_charge"), false, true);

    public static final AnimationHolder REMEDIUM = new AnimationHolder(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "regression_law"), false, false);

    public static final AnimationHolder SAEPTUM = new AnimationHolder(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "saeptum"), false, false);


}
