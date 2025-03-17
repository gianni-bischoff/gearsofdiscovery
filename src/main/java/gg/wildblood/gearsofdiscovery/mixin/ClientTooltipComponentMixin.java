package gg.wildblood.gearsofdiscovery.mixin;

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod;
import gg.wildblood.gearsofdiscovery.content.items.BackpackTooltip;
import gg.wildblood.gearsofdiscovery.content.items.ClientBackpackTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Example Mixin to demonstrate that Mixins should be written in Java.
// It is _technically_ possible to write them in Kotlin but only if you understand
// how the Kotlin compiler works internally and what bytecode it produces and are fully
// aware of that at all times. The general advice is: JUST USE JAVA FOR MIXINS

// Marked as abstract so all the extends and implements clauses don't need to be "followed".
// Those clauses are added to get easy access to things without needing to @Shadow them.
@Mixin(ClientTooltipComponent.class)
public interface ClientTooltipComponentMixin {

    @Inject(
            method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void gearsofdiscovery$create(TooltipComponent visualTooltipComponent, CallbackInfoReturnable<ClientTooltipComponent> cir) {
        System.out.println("Example Mixin ran rn (modid: " + GearsOfDiscoveryMod.MODID + ")");

        if (visualTooltipComponent instanceof BackpackTooltip backpackTooltip) {
            cir.setReturnValue(new ClientBackpackTooltipComponent(backpackTooltip.contents()));
        }

    }
}
