package gg.wildblood.gearsofdiscovery

import com.mojang.logging.LogUtils
import gg.wildblood.gearsofdiscovery.config.Config
import gg.wildblood.gearsofdiscovery.content.*
import gg.wildblood.gearsofdiscovery.content.ModItemDataComponents
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
        ModBlocks.REGISTRY.register(MOD_BUS)
        ModItems.REGISTRY.register(MOD_BUS)
        ModCreativeTabs.REGISTRY.register(MOD_BUS)

        ModVillager.VILLAGER_PROFESSION.register(MOD_BUS)
        ModVillager.POI_TYPES.register(MOD_BUS)
        ModSounds.SOUND_EVENTS.register(MOD_BUS)

        ModItemDataComponents.REGISTRY.register(MOD_BUS)
        ModMenuTypes.REGISTRY.register(MOD_BUS)
        ModDataAttachments.ATTACHMENT_TYPE.register(MOD_BUS)

        val container = ModLoadingContext.get().activeContainer

        container.registerConfig(ModConfig.Type.SERVER, Config.SPEC)
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("Hello! This is working!")
    }
}