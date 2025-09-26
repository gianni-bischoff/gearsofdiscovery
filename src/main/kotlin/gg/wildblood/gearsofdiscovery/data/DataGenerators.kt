package gg.wildblood.gearsofdiscovery.data

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent


@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.MOD)
object DataGenerators {
    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        val existingFileHelper = event.existingFileHelper
        val lookup = event.lookupProvider

        val blockTagsProvider: ModBlockTagProvider = ModBlockTagProvider(packOutput, lookup, existingFileHelper)

        generator.addProvider(event.includeClient(), blockTagsProvider)
        generator.addProvider(event.includeServer(), ModRecipeProvider(packOutput, lookup))
        generator.addProvider(event.includeServer(), ModBlockStateProvider(packOutput, existingFileHelper))
        generator.addProvider(event.includeServer(), ModItemModelProvider(packOutput, existingFileHelper))
        generator.addProvider(event.includeServer(), ModItemTagProvider(packOutput, lookup, blockTagsProvider.contentsGetter()))
        generator.addProvider(event.includeServer(), LockDataProvider(
            generator.packOutput,
            event.lookupProvider,
            event.existingFileHelper
        ))

        generator.addProvider(event.includeServer(), ModCuriosDataProvider(packOutput, existingFileHelper, lookup))
        generator.addProvider(event.includeServer(), QuestDataProvider(packOutput, lookup, existingFileHelper))
    }
}