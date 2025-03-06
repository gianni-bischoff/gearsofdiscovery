package gg.wildblood.gearsofdiscovery

import com.mojang.logging.LogUtils
import gg.wildblood.gearsofdiscovery.blocks.ModBlocks
import gg.wildblood.gearsofdiscovery.items.ModItems
import gg.wildblood.gearsofdiscovery.tabs.ModCreativeTabs
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.registries.DeferredRegister
import org.slf4j.event.Level
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

@Mod(GearsOfDiscoveryMod.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
object GearsOfDiscoveryMod {
    const val MODID = "gearsofdiscovery"

    private val LOGGER = LogUtils.getLogger();

    val BlockRegister: DeferredRegister.Blocks = DeferredRegister.createBlocks(MODID)

    val ItemRegister: DeferredRegister.Items = DeferredRegister.createItems(MODID)

    val CreativeModeTabRegister: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID)


    init {
        LOGGER.info("Hello NeoForge world!")

        ModBlocks.REGISTRY.register(MOD_BUS)
        ModItems.REGISTRY.register(MOD_BUS)
        ModCreativeTabs.REGISTRY.register(MOD_BUS)

        val obj = runForDist(
            clientTarget = {
                MOD_BUS.addListener(::onClientSetup)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(::onServerSetup)
                "test"
            })

       /**

        modContainer.registerExtensionPoint(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory { _, screen ->
                ConfigurationScreen(modContainer, screen)
            }
        )**/
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.info("Initializing client...")
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.info("Server starting...")
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("Hello! This is working!")
    }
}