package com.ae2creative.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

import org.jetbrains.annotations.Nullable;

public class CreativeCellItem extends Item {
    private final ResourceLocation targetId;

    public CreativeCellItem(Properties properties, ResourceLocation targetId) {
        super(properties);
        this.targetId = targetId;
    }

    public ResourceLocation getTargetId() {
        return targetId;
    }

    public ItemStack getTargetStack() {
        var item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(targetId);
        if (item == null || item == net.minecraft.world.item.Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item);
    }

    @Override
    public Component getName(ItemStack stack) {
        ItemStack target = getTargetStack();
        if (target.isEmpty()) {
            return super.getName(stack);
        }
        return Component.translatable("item.ae2creative.creative_cell.named", target.getHoverName());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemStack target = getTargetStack();
        if (!target.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.ae2creative.stores", target.getHoverName()));
        }
        tooltip.add(Component.translatable("tooltip.ae2creative.type"));
    }
}
