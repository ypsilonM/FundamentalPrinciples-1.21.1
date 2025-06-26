package com.ypsi.fundamentalism.fatigueData;

public class PlayerCounter implements IPlayerCounter{
    private int value = 0;

    @Override
    public int get() {
        return value;
    }

    @Override
    public void set(int value) {
        this.value = value;
    }

    @Override
    public void increment() {
        value++;
    }

    @Override
    public void decrement() {
        value--;
    }
}
