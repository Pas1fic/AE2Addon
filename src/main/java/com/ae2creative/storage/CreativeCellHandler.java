package com.ae2creative.storage;

import com.ae2creative.item.CreativeCellItem;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;

public class CreativeCellHandler implements ICellHandler {
    @Override
    public boolean isCell(ItemStack stack) {
        return stack.getItem() instanceof CreativeCellItem;
    }

    @Override
    @Nullable
    public StorageCell getCellInventory(ItemStack stack, @Nullable ISaveProvider host) {
        if (!(stack.getItem() instanceof CreativeCellItem cellItem)) {
            return null;
        }
        return new CreativeCellStorage(stack, cellItem, host);
    }
}
