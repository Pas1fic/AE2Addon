package com.ae2creative;

import com.ae2creative.config.CellConfig;
import com.ae2creative.init.ModCreativeTabs;
import com.ae2creative.registry.CreativeCellRegistry;
import com.ae2creative.storage.CreativeCellHandler;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.api.storage.StorageCells;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(AE2CreativeMod.MOD_ID)
public class AE2CreativeMod {
    public static final String MOD_ID = "ae2creative";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public AE2CreativeMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        CellConfig.load();
        CreativeCellRegistry.registerCells(CellConfig.getItems());
        CreativeCellRegistry.ITEMS.register(modBus);
        ModCreativeTabs.register(modBus);

        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            StorageCells.addCellHandler(new CreativeCellHandler());
            CreativeCellRegistry.getCells().forEach((targetId, cell) -> {
                if (cell.isPresent() && cell.get().getTargetStack().isEmpty()) {
                    LOGGER.warn("Creative cell {} targets missing item {}", cell.getId(), targetId);
                }
            });
        });
    }
}
