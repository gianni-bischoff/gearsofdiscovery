package gg.wildblood.gearsofdiscovery.content

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.content.registry.Lock
import gg.wildblood.gearsofdiscovery.content.registry.quests.QuestDefinition
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
    
    val QUEST_REGISTRY_KEY: ResourceKey<Registry<QuestDefinition>> =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(GearsOfDiscoveryMod.MODID, "quests"))

    @SubscribeEvent
    fun registerDatapackRegistries(event: DataPackRegistryEvent.NewRegistry) {
        event.dataPackRegistry(
            LOCK_REGISTRY_KEY,
            Lock.CODEC,
            Lock.CODEC,
        )
        
        event.dataPackRegistry(
            QUEST_REGISTRY_KEY,
            QuestDefinition.CODEC,
            QuestDefinition.CODEC,
        )
    }
}