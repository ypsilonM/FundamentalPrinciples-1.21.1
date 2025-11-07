package com.ypsi.fundamentalism.item.custom;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.component.YpsDataComponents;
import com.ypsi.fundamentalism.item.ModFoodProperties;
import com.ypsi.fundamentalism.network.packets.SyncExhaustionPacket;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static com.ypsi.fundamentalism.event.ModEvents.getMaxExPerLevel;

public class TonicItem extends Item {
    private static final int MAX_CHARGES = 10;
    private static final int DRINK_DURATION = 32;

    public TonicItem() {
        super(new Item.Properties().food(ModFoodProperties.TONIC) .stacksTo(1));
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

        if (!level.isClientSide && livingEntity instanceof ServerPlayer serverPlayer) {
            int currentCharges = getCharges(stack);
            if (currentCharges > 0) {
                applyTonicEffect(serverPlayer);
                setCharges(stack, currentCharges - 1);

                level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                        SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5F,
                        level.random.nextFloat() * 0.1F + 0.9F);

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
        player.setData(YpsAttachments.CURRENT_EXHAUSTION, Math.max(currentExhaustion-25, 0));
        SyncExhaustionPacket.sendToPlayer(player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
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
