package io.github.etchx.entityrange.mixin;

import io.github.etchx.entityrange.client.EntityRangeUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.etchx.entityrange.client.EntityRangeClient.isTargetingEntity;
import static io.github.etchx.entityrange.client.EntityRangeClient.isInRange;
import static io.github.etchx.entityrange.client.EntityRangeClient.raycastHitDistance;
import static io.github.etchx.entityrange.client.EntityRangeClient.lastHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.doUpdateHit;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hideDistanceDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hideHitDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.showHitsInChat;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    /**
     * Renders EntityRange's hit and distance displays
     */
    @Inject(method = "render", at = @At(value = "RETURN"))
    private void renderEntityRangeDisplay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        EntityRangeUtil.updateProjectileHitData(player);

        // chat hit display
        if (doUpdateHit) {
            doUpdateHit = false;
            if (showHitsInChat && !hideHitDisplay) {
                player.sendMessage(Text.translatable(String.format("%.3f", lastHit)));
            }
        }

        // distance display
        if (isTargetingEntity) {
            isTargetingEntity = false;
            if (!hideDistanceDisplay) {
                int color = isInRange ? 0xFF0000 : 0xFFFFFF; // hardcoded colors
                context.drawText(((InGameHud) (Object) this).getTextRenderer(),
                        Text.translatable(String.format("%.3f", raycastHitDistance)),
                        (context.getScaledWindowWidth() - 26) / 2,
                        (context.getScaledWindowHeight() - 7) / 2 + 15,
                        color, true);
            }
        }

        // side hit display
        if (!showHitsInChat && !hideHitDisplay) {
            context.drawText(((InGameHud) (Object) this).getTextRenderer(),
                    Text.translatable(String.format("Last hit: %.3f", lastHit)),
                    10,
                    (context.getScaledWindowHeight() - 7) / 2 - 15,
                    0xFFFFFF, true);
        }
    }
}
