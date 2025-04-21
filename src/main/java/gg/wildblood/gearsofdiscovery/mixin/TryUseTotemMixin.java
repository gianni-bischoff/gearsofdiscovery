package gg.wildblood.gearsofdiscovery.mixin;

import gg.wildblood.gearsofdiscovery.content.items.HyperSpeedTotem;
import gg.wildblood.gearsofdiscovery.network.HyperSpeedTotemTriggerEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(LivingEntity.class)
public class TryUseTotemMixin {

    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void checkTotemDeathProtection(DamageSource source, CallbackInfoReturnable<Boolean> callbackInfo) {
        LivingEntity self = (LivingEntity)(Object)this;

        if(source.is(DamageTypes.FLY_INTO_WALL) || source.is(DamageTypes.FALL)) {
            AtomicReference<ItemStack> totem = new AtomicReference<>();

            self.getAllSlots().forEach(itemStack -> {
                if(itemStack.getItem() instanceof HyperSpeedTotem) {
                    totem.set(itemStack);
                }
            });

            if(totem.get() == null) return;

            totem.get().shrink(1);

            self.setHealth(2.0F);
            self.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 2));
            self.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 3));
            self.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 10, 5));

            if(self instanceof ServerPlayer t) {
                PacketDistributor.sendToPlayer(t, new HyperSpeedTotemTriggerEvent(true));
            }

            callbackInfo.setReturnValue(true);
        }
    }
}
