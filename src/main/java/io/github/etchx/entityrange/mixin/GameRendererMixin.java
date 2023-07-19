package io.github.etchx.entityrange.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.etchx.entityrange.client.EntityRangeClient.intersectPos;
import static io.github.etchx.entityrange.client.EntityRangeClient.raycastResult;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/EntityHitResult;getEntity()Lnet/minecraft/entity/Entity;"))
    private void displayPos(float tickDelta, CallbackInfo ci) {
        if (raycastResult) {
            raycastResult = false;
            Entity source = ((GameRenderer)(Object)this).getClient().cameraEntity;
            double entityDistance;
            if (source != null) {
                if (intersectPos != null) {
                    entityDistance = source.getEyePos().distanceTo(intersectPos);
                } else {
                    entityDistance = 0;
                }
                ((PlayerEntity)source).sendMessage(Text.translatable(String.valueOf(entityDistance)), true);
            }
        }
    }
}
