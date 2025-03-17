package gg.wildblood.gearsofdiscovery.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Redirect(
            method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/ExtraCodecs;intRange(II)Lcom/mojang/serialization/Codec;",
                    remap = false),
            require = 1
    )
    private static Codec<Integer> redirectIntRange(int min, int max) {
        return Codec.intRange(1, 999);
    }
}
