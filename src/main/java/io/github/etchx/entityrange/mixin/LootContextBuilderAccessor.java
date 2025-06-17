package io.github.etchx.entityrange.mixin;

import net.minecraft.loot.context.LootContext.Builder;
import net.minecraft.loot.context.LootContextParameterSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Builder.class)
public interface LootContextBuilderAccessor {
    @Accessor("parameters")
    LootContextParameterSet getParameters();
}
