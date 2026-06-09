package com.ae2creative;

import com.ae2creative.init.ModCreativeTabs;
import com.ae2creative.init.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AE2CreativeMod.MOD_ID)
public class AE2CreativeMod {
    public static final String MOD_ID = "ae2creative";

    public AE2CreativeMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModCreativeTabs.register(modBus);
        ModItems.register(modBus);
    }
}
