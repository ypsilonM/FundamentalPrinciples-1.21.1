package com.ypsi.fundamentalism.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public class SpellBookLevelUpEvent extends Event {
    private final Player player;
    private final ItemStack spellBook;
    private final int oldLevel;
    private final int newLevel;

    public SpellBookLevelUpEvent(Player player, ItemStack spellBook, int oldLevel, int newLevel) {
        this.player = player;
        this.spellBook = spellBook;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getSpellBook() {
        return spellBook;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }
}
