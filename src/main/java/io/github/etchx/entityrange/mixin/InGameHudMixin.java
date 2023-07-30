package io.github.etchx.entityrange.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.etchx.entityrange.client.EntityRangeClient.targetingEntity;
import static io.github.etchx.entityrange.client.EntityRangeClient.entityDistance;
import static io.github.etchx.entityrange.client.EntityRangeClient.lastHit;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hideDistanceDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hideHitDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.showHitsInChat;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void renderEntityRangeDisplay(MatrixStack matrixStack, float f, CallbackInfo ci) {
        if (targetingEntity && !hideDistanceDisplay) {
            targetingEntity = false;
            int color = entityDistance > 3 ? 0xFFFFFF : 0xFF0000;
            ((InGameHud)(Object)this).getTextRenderer().draw(matrixStack,
                    Text.of(String.format("%.3f", entityDistance)),
                    (this.scaledWidth - 22) / 2,
                    (this.scaledHeight - 7) / 2 + 15,
                    color);

        }
        if (!showHitsInChat && !hideHitDisplay) {
            ((InGameHud)(Object)this).getTextRenderer().draw(matrixStack,
                    Text.of(String.format("Last hit: %.3f", lastHit)),
                    10,
                    (this.scaledHeight - 7) / 2 - 15,
                    0xFFFFFF);
        }
    }
}
