package com.ae2creative.client;

import com.ae2creative.AE2CreativeMod;
import com.ae2creative.item.CreativeCellItem;
import com.ae2creative.registry.CreativeCellRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

@Mod.EventBusSubscriber(modid = AE2CreativeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class CreativeCellClient {
    public static final ResourceLocation CELL_MODEL_ID =
            new ResourceLocation(AE2CreativeMod.MOD_ID, "item/creative_storage_cell");

    // ModelResourceLocation used for all creative cell items.
    public static final ModelResourceLocation CELL_MODEL =
            new ModelResourceLocation(new ResourceLocation(AE2CreativeMod.MOD_ID, "creative_storage_cell"), "inventory");

    private CreativeCellClient() {
    }

    @SubscribeEvent
    public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        try {
            event.register(CELL_MODEL_ID);
        } catch (Exception e) {
            AE2CreativeMod.LOGGER.error("Failed to register additional creative cell model {}", CELL_MODEL_ID, e);
        }
    }

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        try {
            BakedModel sharedModel = event.getModels().get(CELL_MODEL_ID);
            if (sharedModel == null) {
                AE2CreativeMod.LOGGER.error("Shared creative cell model not found: {}", CELL_MODEL_ID);
                return;
            }

            event.getModels().put(CELL_MODEL, sharedModel);
            int mapped = 0;
            for (Item item : CreativeCellRegistry.getAllItems()) {
                try {
                    ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
                    if (itemId == null) {
                        AE2CreativeMod.LOGGER.warn("Skipping creative cell item with missing registry name: {}", item);
                        continue;
                    }
                    ModelResourceLocation cellKey = new ModelResourceLocation(itemId, "inventory");
                    event.getModels().put(cellKey, sharedModel);
                    mapped++;
                } catch (Exception inner) {
                    AE2CreativeMod.LOGGER.error("Failed to map creative cell model for item {}", item, inner);
                }
            }
            AE2CreativeMod.LOGGER.debug("Mapped {} creative cell items to shared model {}", mapped, CELL_MODEL_ID);
        } catch (Exception e) {
            AE2CreativeMod.LOGGER.error("Exception in onModifyBakingResult for creative cell models", e);
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                ItemModelShaper shaper = Minecraft.getInstance()
                        .getItemRenderer()
                        .getItemModelShaper();

                int mapped = 0;
                for (Item item : CreativeCellRegistry.getAllItems()) {
                    try {
                        shaper.register(item, CELL_MODEL);
                        mapped++;
                    } catch (Exception inner) {
                        AE2CreativeMod.LOGGER.error("Failed to register item {} with ItemModelShaper", item, inner);
                    }
                }
                try {
                    shaper.rebuildCache();
                } catch (Exception inner) {
                    AE2CreativeMod.LOGGER.error("Failed to rebuild ItemModelShaper cache after creative cell registration", inner);
                }
                AE2CreativeMod.LOGGER.info("Registered {} creative cell items with ItemModelShaper", mapped);
            } catch (Exception e) {
                AE2CreativeMod.LOGGER.error("Exception in creative cell client setup", e);
            }
        });
    }

    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        try {
            Item[] items = CreativeCellRegistry.getAllItems().toArray(new Item[0]);
            event.getItemColors().register((stack, tintIndex) -> {
                if (tintIndex != 0) {
                    return 0xFFFFFFFF;
                }
                if (!(stack.getItem() instanceof CreativeCellItem creativeCellItem)) {
                    return 0xFFFFFFFF;
                }
                try {
                    return getTintColor(creativeCellItem.getTargetId());
                } catch (Exception inner) {
                    AE2CreativeMod.LOGGER.error("Failed to calculate tint for creative cell item {}", creativeCellItem, inner);
                    return 0xFFFFFFFF;
                }
            }, items);
        } catch (Exception e) {
            AE2CreativeMod.LOGGER.error("Exception while registering creative cell item colors", e);
        }
    }

    private static int getTintColor(ResourceLocation targetId) {
        String key = targetId.toString();
        CRC32 crc = new CRC32();
        crc.update(key.getBytes(StandardCharsets.UTF_8));
        long hash = crc.getValue() & 0xFFFFFFFFL;

        double goldenRatio = 0.618033988749895;
        double hueD = (hash * goldenRatio) % 1.0;

        int sBits = (int) ((hash >> 8) & 0xFF);
        int bBits = (int) ((hash >> 16) & 0xFF);

        float saturation = 0.35f + (sBits / 255f) * 0.45f; // 0.35 - 0.8
        float brightness = 0.75f + (bBits / 255f) * 0.25f; // 0.75 - 1.0

        return hsbToRgb((float) hueD, saturation, brightness);
    }

    private static int hsbToRgb(float hue, float saturation, float brightness) {
        if (saturation == 0f) {
            int gray = (int) (brightness * 255f + 0.5f);
            return 0xFF000000 | (gray << 16) | (gray << 8) | gray;
        }
        float h = (hue - (float) Math.floor(hue)) * 6f;
        int i = (int) h;
        float f = h - i;
        float p = brightness * (1f - saturation);
        float q = brightness * (1f - saturation * f);
        float t = brightness * (1f - saturation * (1f - f));
        float r, g, b;
        switch (i) {
            case 0 -> {
                r = brightness;
                g = t;
                b = p;
            }
            case 1 -> {
                r = q;
                g = brightness;
                b = p;
            }
            case 2 -> {
                r = p;
                g = brightness;
                b = t;
            }
            case 3 -> {
                r = p;
                g = q;
                b = brightness;
            }
            case 4 -> {
                r = t;
                g = p;
                b = brightness;
            }
            default -> {
                r = brightness;
                g = p;
                b = q;
            }
        }
        return 0xFF000000 | ((int) (r * 255f) << 16) | ((int) (g * 255f) << 8) | (int) (b * 255f);
    }
}
