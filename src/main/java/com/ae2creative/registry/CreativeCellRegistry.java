package com.ae2creative.registry;

import com.ae2creative.AE2CreativeMod;
import com.ae2creative.item.CreativeCellItem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CreativeCellRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AE2CreativeMod.MOD_ID);

    private static final Map<ResourceLocation, RegistryObject<CreativeCellItem>> CELLS = new LinkedHashMap<>();

    private CreativeCellRegistry() {
    }

    public static void registerCells(List<ResourceLocation> targetItems) {
        for (ResourceLocation targetId : targetItems) {
            String cellId = toCellId(targetId);
            if (CELLS.containsKey(targetId)) {
                continue;
            }

            RegistryObject<CreativeCellItem> registered = ITEMS.register(cellId,
                    () -> new CreativeCellItem(new Item.Properties().stacksTo(1), targetId));
            CELLS.put(targetId, registered);
            AE2CreativeMod.LOGGER.info("Registered creative cell {} for {}", cellId, targetId);
        }
    }

    public static String toCellId(ResourceLocation itemId) {
        String path = itemId.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)
                ? itemId.getPath()
                : itemId.getNamespace() + "_" + itemId.getPath();
        return "creative_cell_" + path.replace('/', '_').replace('.', '_');
    }

    public static ResourceLocation getCellItemId(ResourceLocation targetId) {
        return new ResourceLocation(AE2CreativeMod.MOD_ID, toCellId(targetId));
    }

    public static ItemStack getIconStack() {
        for (ResourceLocation targetId : CELLS.keySet()) {
            Item item = BuiltInRegistries.ITEM.get(getCellItemId(targetId));
            if (item != null && item != Items.AIR) {
                return new ItemStack(item);
            }
        }
        return ItemStack.EMPTY;
    }

    public static List<CreativeCellItem> getAllItems() {
        List<CreativeCellItem> items = new ArrayList<>();
        for (RegistryObject<CreativeCellItem> cell : CELLS.values()) {
            cell.ifPresent(items::add);
        }
        return Collections.unmodifiableList(items);
    }

    public static List<ItemStack> getAllStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (RegistryObject<CreativeCellItem> cell : CELLS.values()) {
            cell.ifPresent(item -> stacks.add(new ItemStack(item)));
        }
        if (stacks.isEmpty()) {
            for (ResourceLocation targetId : CELLS.keySet()) {
                Item item = BuiltInRegistries.ITEM.get(getCellItemId(targetId));
                if (item != null && item != Items.AIR) {
                    stacks.add(new ItemStack(item));
                }
            }
        }
        return Collections.unmodifiableList(stacks);
    }

    public static Map<ResourceLocation, RegistryObject<CreativeCellItem>> getCells() {
        return Collections.unmodifiableMap(CELLS);
    }
}
