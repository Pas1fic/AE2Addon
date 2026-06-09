package com.ae2creative.init;

import com.ae2creative.AE2CreativeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AE2CreativeMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ae2creative"))
                    .icon(() -> new ItemStack(Items.COAL))
                    .displayItems((parameters, output) -> output.accept(ModItems.CREATIVE_CELL_COAL.get()))
                    .build());

    public static void register(IEventBus modBus) {
        CREATIVE_TABS.register(modBus);
    }
}
