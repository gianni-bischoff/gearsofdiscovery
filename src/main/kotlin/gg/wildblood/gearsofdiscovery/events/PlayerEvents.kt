package gg.wildblood.gearsofdiscovery.events

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent

/**
@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID)
object PlayerEvents {
    @SubscribeEvent
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        event.entity.health = 2f
    }

    fun register() {}
}**/