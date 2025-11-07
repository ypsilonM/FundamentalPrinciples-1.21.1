package com.ypsi.fundamentalism.network.packets.data;

public class ClientExhaustionData {
    private static int currentExhaustion;
    private static int levelExhaustion;

    public static void setCurrentExhaustion(int value) {
        currentExhaustion = value;
    }
    public static int getCurrentExhaustion() {
        return currentExhaustion;
    }
    public static void setLevelExhaustion(int value){ levelExhaustion = value; }
    public static int getLevelExhaustion(){ return  levelExhaustion; }
}
