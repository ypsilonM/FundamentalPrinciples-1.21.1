package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.ToggleReinforcementPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onKeyPress(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        if (ModKeyBinds.REINFORCE_KEY.get().consumeClick()) {
            mc.player.playSound(SoundEvents.END_PORTAL_FRAME_FILL, 0.9f, 0.7f);
            PacketDistributor.sendToServer(new ToggleReinforcementPacket());
        }
    }


    @SubscribeEvent
    public static void onRenderHUD(RenderGuiEvent.Post event) {
//        Player player = Minecraft.getInstance().player;
//        if (player == null) return;
//
//        int current = player.getEntityData().get(ModDataAccessors.CURRENT_EXHAUSTION);
//        int max = (int) player.getAttributeValue(ModAttributes.MAX_EXHAUSTION);
//
//        GuiGraphics gui = event.getGuiGraphics();
//        int x = 10;
//        int y = 10;
//        int width = 100;
//        int height = 5;
//
//        // Fondo de la barra
//        gui.fill(x, y, x + width, y + height, 0xFF555555);
//
//        // Barra de energía (verde)
//        int filledWidth = (int) ((current / (float) max) * width);
//        gui.fill(x, y, x + filledWidth, y + height, 0xFF00FF00);
//
//        // Texto
//        gui.drawString(
//                Minecraft.getInstance().font,
//                current + "/" + max,
//                x + width + 5, y - 3, 0xFFFFFF
//        );
    }

}
