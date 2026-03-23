package com.ypsi.fundamentalism.item.custom;

import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.component.YpsDataComponents;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.item.ModFoodProperties;
import com.ypsi.fundamentalism.particle.ModParticles;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FlaskItem extends Item implements IExhaustionConsumable{
    private static final int MAX_CHARGES = 5;
    private static final int DRINK_DURATION = 32;

    public FlaskItem(Rarity rarity) {
        super(new Item.Properties().food(ModFoodProperties.TONIC).stacksTo(1).rarity(rarity));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int currentCharges = getCharges(stack);

        if (currentCharges > 0 && player.canEat(true)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {

        if (level instanceof ServerLevel serverLevel && livingEntity instanceof ServerPlayer serverPlayer) {
            int currentCharges = getCharges(stack);
            if (currentCharges > 0) {
                applyTonicEffect(serverPlayer);
                setCharges(stack, currentCharges - 1);

                level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                        SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5F,
                        level.random.nextFloat() * 0.1F + 0.9F);

                serverLevel.sendParticles(
                        ParticleTypes.WAX_OFF,
                        livingEntity.getX(),
                        livingEntity.getY() + livingEntity.getBbHeight() * 0.5,
                        livingEntity.getZ(),
                        12,
                        0,
                        0.1,
                        0,
                        0.5
                );

                serverPlayer.getCooldowns().addCooldown(this, 40);
            }
        }

        return stack;
    }
    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return DRINK_DURATION;
    }

    private void applyTonicEffect(ServerPlayer player) {
        int currentExhaustion = player.getData(YpsAttachments.CURRENT_EXHAUSTION.get());
        player.setData(YpsAttachments.CURRENT_EXHAUSTION, Math.max(currentExhaustion-15, 0));
        //SyncExhaustionPacket.sendToPlayer(player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
        player.addEffect(
                new MobEffectInstance(ModEffects.SOOTHE_EFFECT, (20)*45, 3, false, true, true)
        );
    }

    public int getCharges(ItemStack stack) {
        Integer charges = stack.get(YpsDataComponents.TONIC_CHARGES);
        return charges != null ? charges : 0;
    }

    public void setCharges(ItemStack stack, int charges) {
        int actualCharges = Math.min(charges, MAX_CHARGES);
        stack.set(YpsDataComponents.TONIC_CHARGES, actualCharges);
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(actualCharges));
    }

    public boolean recharge(ItemStack stack) {
        int currentCharges = getCharges(stack);
        if (currentCharges < MAX_CHARGES) {
            setCharges(stack, currentCharges + 1);
            return true;
        }
        return false;
    }

    public boolean isFull(ItemStack stack) {
        return getCharges(stack) >= MAX_CHARGES;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getCharges(stack) < MAX_CHARGES;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round((float)getCharges(stack) / MAX_CHARGES * 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float charges = (float)getCharges(stack) / MAX_CHARGES;
        int r = (int)(255 * (1 - charges));
        int g = (int)(255 * charges);
        return (r << 16) | (g << 8);
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        setCharges(stack, 0);
        return stack;
    }
}
