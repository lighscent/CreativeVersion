package com.github.xl1te.creativeversion.mixin;

import com.github.xl1te.creativeversion.CreativeVersionClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiHotbarMixin {
	@Inject(method = "extractSlot", at = @At("TAIL"), require = 0)
	private void creativeversion$markHotbar(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker delta, Player player, ItemStack stack, int seed, CallbackInfo ci) {
		if (!CreativeVersionClient.isUnavailable(stack)) {
			return;
		}
		graphics.fill(x, y, x + 16, y + 16, 0x55FF0000);
		graphics.outline(x, y, 16, 16, 0xFFFF4040);
	}
}
