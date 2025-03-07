package gg.wildblood.gearsofdiscovery

import com.mojang.logging.LogUtils
import gg.wildblood.gearsofdiscovery.blocks.ModBlocks
import gg.wildblood.gearsofdiscovery.datamaps.Lock
import gg.wildblood.gearsofdiscovery.items.ModItems
import gg.wildblood.gearsofdiscovery.locks.ModRegistries.LOCK_REGISTRY_KEY
import gg.wildblood.gearsofdiscovery.tabs.ModCreativeTabs
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(GearsOfDiscoveryMod.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object GearsOfDiscoveryMod {
    const val MODID = "gearsofdiscovery"

    val LOGGER = LogUtils.getLogger();

    init {
        LOGGER.info("Hello NeoForge world!")

        ModBlocks.REGISTRY.register(MOD_BUS)
        ModItems.REGISTRY.register(MOD_BUS)
        ModCreativeTabs.REGISTRY.register(MOD_BUS)

        val container = ModLoadingContext.get().activeContainer

        container.registerConfig(ModConfig.Type.SERVER, Config.SPEC)
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("Hello! This is working!")
    }
}