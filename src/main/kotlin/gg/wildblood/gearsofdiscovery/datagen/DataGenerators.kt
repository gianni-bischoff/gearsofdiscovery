package gg.wildblood.gearsofdiscovery.datagen

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.datamaps.Lock
import gg.wildblood.gearsofdiscovery.locks.ModRegistries.LOCK_REGISTRY_KEY
import net.minecraft.core.HolderGetter
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion.MOD_ID


@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.MOD)
object DataGenerators {

    private val EXAMPLE_CONFIGURED_FEATURE: ResourceKey<Lock> = ResourceKey.create(
        LOCK_REGISTRY_KEY,
        ResourceLocation.fromNamespaceAndPath(MOD_ID, "example")
    )

    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        val existingFileHelper = event.existingFileHelper
        val lookup = event.lookupProvider

        generator.addProvider(event.includeServer(), ModRecipeProvider(packOutput, lookup))
        generator.addProvider(event.includeServer(), LockDataProvider(
            generator.packOutput,
            event.lookupProvider,
            event.existingFileHelper
        ))
    }
}