package com.ae2creative.init;

import com.ae2creative.AE2CreativeMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AE2CreativeMod.MOD_ID);

    public static final RegistryObject<Item> CREATIVE_CELL_COAL = ITEMS.register("creative_cell_coal",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
