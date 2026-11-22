package com.haoling.binlabel;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.items.IItemHandler;

/** Mekanism のビンを見たとき、中身のアイテム名と個数をクロスヘアの下に出すクライアント専用 mod。 */
@Mod(value = BinLabelMod.MOD_ID, dist = Dist.CLIENT)
public class BinLabelMod {
    public static final String MOD_ID = "binlabel";
    private static final ResourceLocation LAYER = ResourceLocation.fromNamespaceAndPath(MOD_ID, "bin_label");

    public BinLabelMod(IEventBus modBus) {
        modBus.addListener(RegisterGuiLayersEvent.class,
                e -> e.registerAbove(VanillaGuiLayers.CROSSHAIR, LAYER, BinLabelMod::render));
    }

    private static boolean isBin(BlockState state) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return id.getNamespace().equals("mekanism") && id.getPath().endsWith("_bin");
    }

    private static void render(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.level == null || mc.screen != null) return;
        if (!(mc.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) return;
        Level level = mc.level;
        BlockPos pos = hit.getBlockPos();
        if (!isBin(level.getBlockState(pos))) return;

        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, hit.getDirection());
        if (handler == null) handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if (handler == null || handler.getSlots() < 1) return;
        ItemStack stack = handler.getStackInSlot(0);

        Component text = stack.isEmpty()
                ? Component.translatable("binlabel.empty")
                : Component.empty().append(stack.getHoverName()).append(" × " + String.format("%,d", stack.getCount()));
        int w = mc.font.width(text);
        int x = g.guiWidth() / 2 - w / 2;
        int y = g.guiHeight() / 2 + 14;
        g.fill(x - 3, y - 2, x + w + 3, y + 10, 0x80000000);
        g.drawString(mc.font, text, x, y, 0xFFFFFF, true);
    }
}
