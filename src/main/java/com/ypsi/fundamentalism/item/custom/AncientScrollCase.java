package com.ypsi.fundamentalism.item.custom;

import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AncientScrollCase extends Item {
    public static List<Principles> principles = new ArrayList<Principles>();
    static {
        principles.add(Principles.CONCENTRATIO);
        principles.add(Principles.POTENTIA);
        principles.add(Principles.VITALE);
        principles.add(Principles.LOCUS);
        principles.add(Principles.REPETITIO);
        principles.add(Principles.APPARITIO);
        principles.add(Principles.PERTINACIA);
        principles.add(Principles.EXPANSIO);
        principles.add(Principles.MOTUS);
        principles.add(Principles.PERCEPTIO);
        principles.add(Principles.REMEDIUM);
        principles.add(Principles.AUGERE);
        principles.add(Principles.CERTUM);
    }

    public AncientScrollCase(Properties properties) {
        super(properties
                .stacksTo(1)
        );
    }

    private static String getPrincipleType(){
        int i = new Random().nextInt(0, 13);
        return principles.get(i).toString();
    }



    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
        Player clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer != null){
            clientPlayer.playSound(
                    SoundEvents.COPPER_BULB_BREAK, 1, 0.6F
            );
        }
        if (!level.isClientSide) {
            //Principles principle = Principles.valueOf(stack.get(YpsDataComponents.NOTE_TYPE));

            int currentLvl;
            Principles principle;

            do {

                principle = Principles.valueOf(getPrincipleType());
                currentLvl = PrinciplesProgressionManager.getCategoryLevel(player, principle);

            }while(currentLvl == 20 && !PrinciplesProgressionManager.isFullLeveled(player));

            int xpAmount = currentLvl<15?
                    100:
                    (int)((Util.getExpForPrincipleLevel(currentLvl+1))*0.10);

            PrinciplesProgressionManager.addCategoryExperience(
                    player, PrinciplesProgressionManager.getTechnicalName(principle),
                    xpAmount);

            if(!player.isCreative())
                stack.shrink(1);

        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Component component = Component.translatable("tooltip.ypfundamentals.scroll_case_description1");
        tooltipComponents.add(component);
        tooltipComponents.add(Component.translatable("tooltip.ypfundamentals.scroll_case_description2"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    //    @Override
//    public @NotNull Component getName(@NotNull ItemStack itemStack) {
//        return Component.literal(itemStack.get(YpsDataComponents.NOTE_TYPE)+" ancient note");
//    }

}
