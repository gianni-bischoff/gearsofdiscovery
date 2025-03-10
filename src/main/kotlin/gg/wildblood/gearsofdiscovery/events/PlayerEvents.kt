package gg.wildblood.gearsofdiscovery.events

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.LOGGER
import gg.wildblood.gearsofdiscovery.locks.Lock
import gg.wildblood.gearsofdiscovery.locks.hasTypeLock
import net.minecraft.world.level.block.Block
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
    fun onRightClickBlockEvent(event: PlayerInteractEvent.RightClickBlock) {
        event.isCanceled = event.itemStack.hasTypeLock(Lock.Type.ITEM_USE)
    }

    @SubscribeEvent
    fun onLeftClickBlock(event: PlayerInteractEvent.LeftClickBlock) {
        event.isCanceled = getTargetBlock(event).hasTypeLock(Lock.Type.BLOCK_BREAK)
    }

    @SubscribeEvent
    fun onRightClickItemEvent(event: PlayerInteractEvent.RightClickItem) {
        event.isCanceled = event.itemStack.hasTypeLock(Lock.Type.ITEM_USE)
    }

    private fun <T> getTargetBlock(event: T) : Block where T : PlayerInteractEvent, T : ICancellableEvent {
        return event.level.getBlockState(event.pos).block
    }
}