package io.github.etchx.entityrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.etchx.entityrange.client.EntityRangeClient.arrowHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.closestDistance;
import static io.github.etchx.entityrange.client.EntityRangeClient.projectileHit;
import static io.github.etchx.entityrange.client.EntityRangeClient.targetPlayer;
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
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (arrowHit && !projectileHit) {
            if (targetPlayer != null) {
                lastHit = player.distanceTo(targetPlayer);
                if (showHitsInChat && !hideHitDisplay) {
                    player.sendMessage(Text.of(String.format("%.3f", lastHit)), false);
                    targetPlayer = null;
                }
            }
            else {
                player.sendMessage(Text.of("Arrow hit but player not found"), false);
            }
            arrowHit = false;
        }
        else if (arrowHit) {
            if (showHitsInChat && !hideHitDisplay) {
                player.sendMessage(Text.of(String.format("%.3f", lastHit)), false);
            }
            arrowHit = false;
            projectileHit = false;
        }
        else if (projectileHit && targetPlayer == null && showHitsInChat && !hideHitDisplay) {
            player.sendMessage(Text.of(String.format("%.3f", lastHit)), false);
            projectileHit = false;
        }
        closestDistance = Double.MAX_VALUE;
        if (targetingEntity && !hideDistanceDisplay) {
            targetingEntity = false;
            int color = entityDistance > 3 ? 0xFFFFFF : 0xFF0000;
            ((InGameHud)(Object)this).getTextRenderer().drawWithShadow(matrixStack,
                    Text.of(String.format("%.3f", entityDistance)),
                    (this.scaledWidth - 26) / 2,
                    (this.scaledHeight - 7) / 2 + 15,
                    color);

        }
        if (!showHitsInChat && !hideHitDisplay) {
            ((InGameHud)(Object)this).getTextRenderer().drawWithShadow(matrixStack,
                    Text.of(String.format("Last hit: %.3f", lastHit)),
                    10,
                    (this.scaledHeight - 7) / 2 - 15,
                    0xFFFFFF);
        }
    }
}
