package gg.wildblood.gearsofdiscovery.locks

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.DataPackRegistryEvent


@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.MOD)
object ModRegistries {
    val LOCK_REGISTRY_KEY: ResourceKey<Registry<Lock>> =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(GearsOfDiscoveryMod.MODID, "locks"))

    @SubscribeEvent
    fun registerDatapackRegistries(event: DataPackRegistryEvent.NewRegistry) {
        event.dataPackRegistry(
            LOCK_REGISTRY_KEY,
            Lock.CODEC,
            Lock.CODEC,
        )
    }
}