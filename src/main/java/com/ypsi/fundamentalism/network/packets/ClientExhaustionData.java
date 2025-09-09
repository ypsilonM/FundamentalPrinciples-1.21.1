package com.ypsi.fundamentalism.network.packets;

public class ClientExhaustionData {
    private static int currentExhaustion;
    private static int maxExhaustion;

    public static void setCurrentExhaustion(int value) {
        currentExhaustion = value;
    }

    public static void setMaxExhaustion(int value) {
        maxExhaustion = value;
    }

    public static int getCurrentExhaustion() {
        return currentExhaustion;
    }

    public static int getMaxExhaustion() {
        return maxExhaustion;
    }
}
