package io.github.etchx.entityrange.mixin;

import io.github.etchx.entityrange.client.EntityRangeClient;
import io.github.etchx.entityrange.client.EntityRangeUtil;
import io.github.etchx.entityrange.data.HitlogIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

import static io.github.etchx.entityrange.client.EntityRangeClient.isRecording;
import static io.github.etchx.entityrange.client.EntityRangeClient.isTargetingEntity;
import static io.github.etchx.entityrange.client.EntityRangeClient.isInRange;
import static io.github.etchx.entityrange.client.EntityRangeClient.raycastHitDistance;
import static io.github.etchx.entityrange.client.EntityRangeClient.lastHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.doUpdateHit;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hideDistanceDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.hideHitDisplay;
import static io.github.etchx.entityrange.client.EntityRangeConfig.showHitsInChat;
import static io.github.etchx.entityrange.client.EntityRangeClient.hitData;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow public abstract TextRenderer getTextRenderer();

    @Unique
    private int distanceDisplayFade = 0;

    /**
     * Renders EntityRange's hit and distance displays
     */
    @Inject(method = "render", at = @At(value = "RETURN"))
    private void renderEntityRangeDisplay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        EntityRangeUtil.updateProjectileHitData(player);

        if (isRecording && hitData != null) {
            try {
                HitlogIO.addHit();
            } catch (IOException e) {
                EntityRangeClient.LOGGER.error("Failed to update hitlog file", e);
            }
        }
        hitData = null;

        // chat hit display
        if (doUpdateHit) {
            doUpdateHit = false;
            if (showHitsInChat && !hideHitDisplay) {
                player.sendMessage(Text.translatable(String.format("%.3f", lastHit)));
            }
        }

        // distance display
        if (!hideDistanceDisplay) {
            MutableText text = Text.translatable(String.format("%.3f", raycastHitDistance));
            int width = this.getTextRenderer().getWidth(text);
            if (isTargetingEntity) {
                int color = isInRange ? 0xFF0000 : 0xFFFFFF; // hardcoded colors
                context.drawText(this.getTextRenderer(),
                        text,
                        (context.getScaledWindowWidth() - width) / 2,
                        (context.getScaledWindowHeight() - 7) / 2 + 15,
                        color, true);
                distanceDisplayFade = (int)(20.0 * MinecraftClient.getInstance().options.getNotificationDisplayTime().getValue());
            } else if (distanceDisplayFade > 0) {
                int alpha = distanceDisplayFade * 256 / 10;
                if (alpha > 255) {
                    alpha = 255;
                }
                if (alpha > 0) {
                    context.drawText(this.getTextRenderer(),
                            text,
                            (context.getScaledWindowWidth() - width) / 2,
                            (context.getScaledWindowHeight() - 7) / 2 + 15,
                            ColorHelper.Argb.withAlpha(alpha, 0x555555), true);
                }
                distanceDisplayFade--;
            }
        }
        isTargetingEntity = false;

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
