package com.ae2creative.recipe;

import com.ae2creative.AE2CreativeMod;
import com.ae2creative.item.CreativeCellItem;
import com.ae2creative.registry.CreativeCellRegistry;

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod.EventBusSubscriber(modid = AE2CreativeMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CreativeCellRecipes {
    @SuppressWarnings("unchecked")
    private static final ResourceKey<Registry<Recipe<?>>> RECIPE_REGISTRY_KEY =
            (ResourceKey<Registry<Recipe<?>>>) (ResourceKey<?>) ResourceKey.createRegistryKey(
                    new ResourceLocation("minecraft", "recipe"));

    private static final ResourceLocation CREATIVE_ENERGY_CELL =
            new ResourceLocation("appliedenergistics2", "creative_energy_cell");
    private static final ResourceLocation[] STORAGE_CELLS = {
            new ResourceLocation("appliedenergistics2", "item_storage_cell_1k"),
            new ResourceLocation("appliedenergistics2", "item_storage_cell_4k"),
            new ResourceLocation("appliedenergistics2", "item_storage_cell_16k"),
            new ResourceLocation("appliedenergistics2", "item_storage_cell_64k"),
            new ResourceLocation("appliedenergistics2", "item_storage_cell_256k")
    };

    private CreativeCellRecipes() {
    }

    @SubscribeEvent
    public static void onRegisterRecipes(RegisterEvent event) {
        if (!event.getRegistryKey().equals(RECIPE_REGISTRY_KEY)) {
            return;
        }

        Item energyCell = BuiltInRegistries.ITEM.get(CREATIVE_ENERGY_CELL);
        if (energyCell == null) {
            AE2CreativeMod.LOGGER.error("Could not find AE2 creative energy cell for recipe generation");
            return;
        }

        Ingredient energyIngredient = Ingredient.of(energyCell);
        Ingredient storageIngredient = Ingredient.of(getStorageCellItems());

        for (var entry : CreativeCellRegistry.getCells().entrySet()) {
            if (!entry.getValue().isPresent()) {
                continue;
            }

            CreativeCellItem cellItem = entry.getValue().get();
            ResourceLocation targetId = entry.getKey();
            ItemStack result = new ItemStack(cellItem);
            ItemStack targetStack = new ItemStack(BuiltInRegistries.ITEM.get(targetId));

            NonNullList<Ingredient> ingredients = NonNullList.create();
            ingredients.add(energyIngredient);
            ingredients.add(Ingredient.of(targetStack));
            ingredients.add(storageIngredient);

            ResourceLocation recipeId = new ResourceLocation(
                    AE2CreativeMod.MOD_ID, CreativeCellRegistry.toCellId(targetId));

            ShapelessRecipe recipe = new ShapelessRecipe(
                    recipeId,
                    recipeId.toString(),
                    CraftingBookCategory.MISC,
                    result,
                    ingredients);

            event.register(RECIPE_REGISTRY_KEY, recipeId, () -> recipe);
        }
    }

    private static ItemLike[] getStorageCellItems() {
        ItemLike[] items = new ItemLike[STORAGE_CELLS.length];
        for (int i = 0; i < STORAGE_CELLS.length; i++) {
            items[i] = BuiltInRegistries.ITEM.get(STORAGE_CELLS[i]);
        }
        return items;
    }
}
