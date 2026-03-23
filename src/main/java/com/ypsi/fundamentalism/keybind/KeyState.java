package com.ypsi.fundamentalism.keybind;

import net.minecraft.client.KeyMapping;

public class KeyState {
    private final KeyMapping keyMapping;
    private boolean wasPressed = false;
    private boolean wasReleased = false;
    private boolean isDown = false;
    private long lastPressTime = 0;
    private static final long DOUBLE_CLICK_TIME = 250;

    public KeyState(KeyMapping keyMapping) {
        this.keyMapping = keyMapping;
    }

    public void update() {
        boolean currentlyDown = keyMapping.isDown();

        if (currentlyDown && !isDown) {
            wasPressed = true;
            wasReleased = false;
            lastPressTime = System.currentTimeMillis();
        } else if (!currentlyDown && isDown) {
            wasPressed = false;
            wasReleased = true;
        } else {
            wasPressed = false;
            wasReleased = false;
        }
        isDown = currentlyDown;
    }

    public boolean wasPressed() {
        return wasPressed;
    }

    public boolean wasReleased() {
        return wasReleased;
    }

    public boolean isDown() {
        return isDown;
    }

    public boolean isDoubleClick() {
        if (!wasPressed) return false;
        return (System.currentTimeMillis() - lastPressTime) < DOUBLE_CLICK_TIME;
    }

    public KeyMapping getKeyMapping() {
        return keyMapping;
    }
}
