package com.ae2creative.storage;

import com.ae2creative.item.CreativeCellItem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;

public class CreativeCellStorage implements StorageCell {
    private final ItemStack cellStack;
    private final CreativeCellItem cellItem;
    @Nullable
    private final AEKey boundKey;
    @Nullable
    private final ISaveProvider host;

    public CreativeCellStorage(ItemStack cellStack, CreativeCellItem cellItem, @Nullable ISaveProvider host) {
        this.cellStack = cellStack;
        this.cellItem = cellItem;
        this.host = host;
        ItemStack target = cellItem.getTargetStack();
        this.boundKey = target.isEmpty() ? null : AEItemKey.of(target);
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount <= 0 || boundKey == null || !boundKey.equals(what)) {
            return 0;
        }
        return amount;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (amount <= 0 || boundKey == null || !boundKey.equals(what)) {
            return 0;
        }
        return amount;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (boundKey != null) {
            out.add(boundKey, Long.MAX_VALUE);
        }
    }

    @Override
    public Component getDescription() {
        return cellStack.getHoverName();
    }

    @Override
    public CellState getStatus() {
        return boundKey != null ? CellState.FULL : CellState.EMPTY;
    }

    @Override
    public double getIdleDrain() {
        return 0.5;
    }

    @Override
    public boolean canFitInsideCell() {
        return false;
    }

    @Override
    public void persist() {
        if (host != null) {
            host.saveChanges();
        }
    }
}
