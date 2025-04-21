package gg.wildblood.gearsofdiscovery

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.LOGGER
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory

@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object GearsOfDiscoveryModClient {
    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    @SubscribeEvent
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.info("Initializing client...")

        val container = ModLoadingContext.get().activeContainer

        //CuriosRendererRegistry.register(ModItems.MINERS_BACKPACK) { BackpackRenderer() }

        container.registerExtensionPoint(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory { _, screen ->
                ConfigurationScreen(container, screen)
            }
        )
    }

    @SubscribeEvent
    private fun onRegisterMenuScreens(event: RegisterMenuScreensEvent) {
    }

}