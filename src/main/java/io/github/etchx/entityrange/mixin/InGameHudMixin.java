package io.github.etchx.entityrange.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.etchx.entityrange.client.EntityRangeClient.displayDistance;
import static io.github.etchx.entityrange.client.EntityRangeClient.entityDistance;
import static io.github.etchx.entityrange.client.EntityRangeClient.lastHit;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "render", at = @At(value = "RETURN"))
    private void renderEntityRangeDisplay(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (displayDistance) {
            displayDistance = false;
            int color = entityDistance > 3 ? 0xFFFFFF : 0xFF0000;
            context.drawText(((InGameHud)(Object)this).getTextRenderer(),
                    Text.translatable(String.format("%.3f", entityDistance)),
                    (context.getScaledWindowWidth() - 22) / 2,
                    (context.getScaledWindowHeight() - 7) / 2 + 15,
                    color,false);
        }
        context.drawText(((InGameHud)(Object)this).getTextRenderer(),
                Text.translatable(String.format("Last hit: %.3f", lastHit)),
                10,
                (context.getScaledWindowHeight() - 7) / 2 - 15,
                0xFFFFFF,false);
    }
}
