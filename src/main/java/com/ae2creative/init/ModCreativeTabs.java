package com.ae2creative.init;

import com.ae2creative.AE2CreativeMod;
import com.ae2creative.registry.CreativeCellRegistry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AE2CreativeMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.ae2creative"))
                    .icon(() -> CreativeCellRegistry.getIconStack())
                    .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modBus) {
        CREATIVE_TABS.register(modBus);
        modBus.addListener(ModCreativeTabs::onBuildCreativeTabContents);
    }

    private static void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == TAB.get() || event.getTabKey() == CreativeModeTabs.SEARCH) {
            CreativeCellRegistry.getCells().values().forEach(cell -> cell.ifPresent(event::accept));
        }
    }
}
