package com.ypsi.fundamentalism.keybind;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ModKeyBinds {
    public static final Supplier<KeyMapping> REINFORCE_KEY =
            registerKey(
                    "reinforce",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Y,
                    "key.category.fundamentalism");
    public static final Supplier<KeyMapping> SELECTION_KEY =
            registerKey(
                    "selection",
                    KeyConflictContext.IN_GAME,
                    InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_U,
                    "key.category.fundamentalism");


    private static Supplier<KeyMapping> registerKey(String name, KeyConflictContext conflictContext, InputConstants.Type inputType, int keyCode, String category) {
        return Suppliers.memoize(() -> new KeyMapping(
                "key.fundamentalism." + name,
                conflictContext,
                inputType,
                keyCode,
                category
        ))::get;
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ModKeyBinds::registerKeybinds);
    }

    private static void registerKeybinds(RegisterKeyMappingsEvent event) {
        event.register(REINFORCE_KEY.get());
        event.register(SELECTION_KEY.get());
    }
}
