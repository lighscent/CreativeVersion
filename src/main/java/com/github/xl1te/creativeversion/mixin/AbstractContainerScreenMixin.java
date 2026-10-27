package com.github.xl1te.creativeversion.mixin;

import com.github.xl1te.creativeversion.CreativeVersionClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
	@Inject(method = "extractSlot", at = @At("TAIL"))
	private void creativeversion$markUnavailable(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
		if (!CreativeVersionClient.isUnavailable(slot.getItem())) {
			return;
		}
		int x = slot.x, y = slot.y;
		graphics.fill(x, y, x + 16, y + 16, 0x55FF0000);
		graphics.outline(x, y, 16, 16, 0xFFFF4040);
	}
}
