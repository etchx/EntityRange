package io.github.etchx.entityrange.mixin;

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
    @Inject(method = "render", at = @At(value = "RETURN"))
    private void renderEntityRangeDisplay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (arrowHit && !projectileHit) {
            if (targetPlayer != null) {
                lastHit = player.distanceTo(targetPlayer);
                if (showHitsInChat && !hideHitDisplay) {
                    player.sendMessage(Text.translatable(String.format("%.3f", lastHit)));
                    targetPlayer = null;
                }
            }
            else {
                player.sendMessage(Text.translatable("Arrow hit but player not found"));
            }
            arrowHit = false;
        }
        else if (arrowHit) {
            if (showHitsInChat && !hideHitDisplay) {
                player.sendMessage(Text.translatable(String.format("%.3f", lastHit)));
            }
            arrowHit = false;
            projectileHit = false;
        }
        else if (projectileHit && targetPlayer == null && showHitsInChat && !hideHitDisplay) {
            player.sendMessage(Text.translatable(String.format("%.3f", lastHit)));
            projectileHit = false;
        }
        closestDistance = Double.MAX_VALUE;
        if (targetingEntity && !hideDistanceDisplay) {
            targetingEntity = false;
            int color = entityDistance > 3 ? 0xFFFFFF : 0xFF0000;
            context.drawText(((InGameHud)(Object)this).getTextRenderer(),
                    Text.translatable(String.format("%.3f", entityDistance)),
                    (context.getScaledWindowWidth() - 26) / 2,
                    (context.getScaledWindowHeight() - 7) / 2 + 15,
                    color,true);
        }
        if (!showHitsInChat && !hideHitDisplay) {
            context.drawText(((InGameHud) (Object) this).getTextRenderer(),
                    Text.translatable(String.format("Last hit: %.3f", lastHit)),
                    10,
                    (context.getScaledWindowHeight() - 7) / 2 - 15,
                    0xFFFFFF, true);
        }
    }
}
