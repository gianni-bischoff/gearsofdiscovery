package gg.wildblood.gearsofdiscovery

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.LOGGER
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent

@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.DEDICATED_SERVER])
object GearsOfDiscoveryModServer {
    /**
     * This is used for initializing server specific
     * Fired on the mod specific event bus.
     */
    @SubscribeEvent
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.info("Server starting...")
    }
}