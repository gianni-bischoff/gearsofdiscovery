package gg.wildblood.gearsofdiscovery.events

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.LOGGER
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Items
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent

@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.GAME)
object PlayerEvents {

    init {
        LOGGER.info("Player Events Registered!")
    }

    @SubscribeEvent
    fun onEntityInteractSpecific(event: PlayerInteractEvent.EntityInteractSpecific) {
        LOGGER.warn("Player Interact Event Fired!")
        if(event.itemStack.item == Items.DIRT) {
            LOGGER.info("Trying to Cancel PlayerInteractEvent!")
            event.cancellationResult = InteractionResult.FAIL
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun onRightClickBlockEvent(event: PlayerInteractEvent.RightClickBlock) {
        cancelIfNotAuthorized(event)
    }

    @SubscribeEvent
    fun onRightClickItemEvent(event: PlayerInteractEvent.RightClickItem) {
        cancelIfNotAuthorized(event)
    }

    @SubscribeEvent
    fun onLeftClickBlock(event: PlayerInteractEvent.LeftClickBlock) {
        cancelIfNotAuthorized(event)
    }

    @SubscribeEvent
    fun onEntityInteract(event: PlayerInteractEvent.EntityInteract) {
        cancelIfNotAuthorized(event)
    }

    private fun <T> cancelIfNotAuthorized(event: T) where T : PlayerInteractEvent, T : ICancellableEvent {
        if(event.itemStack.item == Items.COOKED_BEEF) {
            LOGGER.info("Cancelling PlayerInteractEvent because of dirt!")
            event.isCanceled = true
        }
    }
}