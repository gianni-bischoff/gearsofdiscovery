package gg.wildblood.gearsofdiscovery

import com.mojang.logging.LogUtils
import gg.wildblood.gearsofdiscovery.blocks.ModBlocks
import gg.wildblood.gearsofdiscovery.items.ModItems
import gg.wildblood.gearsofdiscovery.tabs.ModCreativeTabs
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Consumer


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(GearsOfDiscoveryMod.MODID)
class GearsOfDiscoveryMod(modEventBus: IEventBus, modContainer: ModContainer) {
    companion object {
        const val MODID = "gearsofdiscovery"

        private val LOGGER = LogUtils.getLogger();

        val BlockRegister: DeferredRegister.Blocks = DeferredRegister.createBlocks(MODID)

        val ItemRegister: DeferredRegister.Items = DeferredRegister.createItems(MODID)

        val CreativeModeTabRegister: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID)

        // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
        @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
        object ClientModEvents {
            @SubscribeEvent
            fun onClientSetup(event: FMLClientSetupEvent?) {
                // Some client setup code
                LOGGER.info("HELLO FROM CLIENT SETUP")
                LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().user.name)
            }
        }
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

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT))

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber)

        Config.items.forEach(Consumer { item: Item ->
            LOGGER.info(
                "ITEM >> {}",
                item.toString()
            )
        })
    }

    private fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey === CreativeModeTabs.BUILDING_BLOCKS) event.accept(ModBlocks.OFFERING_BLOCK.item)
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting")
    }
}