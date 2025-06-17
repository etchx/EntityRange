package io.github.etchx.entityrange.mixin;

import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootContext.class)
public interface LootContextInvoker {
    /**
     * Accessor to package-private constructor of LootContext
     */
    @Invoker(value = "<init>")
    static LootContext callLootContext(LootContextParameterSet parameters, Random random, RegistryEntryLookup.RegistryLookup lookup) {
        return null;
    }
}
