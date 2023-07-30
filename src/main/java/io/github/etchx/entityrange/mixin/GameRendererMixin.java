package io.github.etchx.entityrange.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.etchx.entityrange.client.EntityRangeClient.targetingEntity;
import static io.github.etchx.entityrange.client.EntityRangeClient.hitPos;
import static io.github.etchx.entityrange.client.EntityRangeClient.entityDistance;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/EntityHitResult;getPos()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d calcDistance(EntityHitResult instance) {
        targetingEntity = true;
        Vec3d newPos = instance.getPos();
        if (hitPos == null || newPos.distanceTo(hitPos) > 0.0001) {
            MinecraftClient client = ((GameRenderer) (Object) this).getClient();
            Entity source = client.cameraEntity;
            entityDistance = source.getEyePos().distanceTo(newPos);
            //client.player.sendMessage(Text.translatable(String.format("%.3f", entityDistance)), true);
            hitPos = newPos;
        }
        return newPos;
    }
}
