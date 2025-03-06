package gg.wildblood.gearsofdiscovery

import com.mojang.logging.LogUtils
import gg.wildblood.gearsofdiscovery.blocks.ModBlocks
import gg.wildblood.gearsofdiscovery.items.ModItems
import gg.wildblood.gearsofdiscovery.tabs.ModCreativeTabs
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.registries.DeferredRegister

@Mod(GearsOfDiscoveryMod.MODID)
class GearsOfDiscoveryMod(modEventBus: IEventBus, modContainer: ModContainer) {
    companion object {
        const val MODID = "gearsofdiscovery"

        private val LOGGER = LogUtils.getLogger();

        val BlockRegister: DeferredRegister.Blocks = DeferredRegister.createBlocks(MODID)

        val ItemRegister: DeferredRegister.Items = DeferredRegister.createItems(MODID)

        val CreativeModeTabRegister: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID)
    }

    init {
        modEventBus.addListener(::commonSetup)

        ItemRegister.register(modEventBus)

        CreativeModeTabRegister.register(modEventBus)

        BlockRegister.register(modEventBus)

        NeoForge.EVENT_BUS.register(this)

        modEventBus.addListener(::addCreative)

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC)

        ModBlocks.register()

        ModItems.register()

        ModCreativeTabs.register()

        modContainer.registerExtensionPoint(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory { _, screen ->
                ConfigurationScreen(modContainer, screen)
            }
        )
    }

    private fun commonSetup(event: FMLCommonSetupEvent) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP")
    }

    private fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey === CreativeModeTabs.BUILDING_BLOCKS) event.accept(ModBlocks.OFFERING_BLOCK.item)
    }
}