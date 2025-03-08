package gg.wildblood.gearsofdiscovery.events

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.LOGGER
import gg.wildblood.gearsofdiscovery.locks.ModRegistries.LOCK_REGISTRY_KEY
import gg.wildblood.gearsofdiscovery.network.ModClientPayloadHandler
import gg.wildblood.gearsofdiscovery.network.UnlockList
import net.minecraft.client.Minecraft
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import kotlin.jvm.optionals.getOrNull

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
        cancelIfLocked(event)
    }

    @SubscribeEvent
    fun onRightClickItemEvent(event: PlayerInteractEvent.RightClickItem) {
        when(event.itemStack.item) {
            Items.STICK -> ModClientPayloadHandler.serverAddUnlock(event.entity,"fish")
            Items.FISHING_ROD -> ModClientPayloadHandler.serverRemoveUnlock(event.entity,"fish")
        }

        cancelIfLocked(event)
    }

    @SubscribeEvent
    fun onLeftClickBlock(event: PlayerInteractEvent.LeftClickBlock) {
        cancelIfLocked(event)
    }

    @SubscribeEvent
    fun onEntityInteract(event: PlayerInteractEvent.EntityInteract) {
        cancelIfLocked(event)
    }

    private fun <T> cancelIfLocked(event: T) where T : PlayerInteractEvent, T : ICancellableEvent {
        event.isCanceled = event.itemStack.isLocked()
    }
}

fun ItemStack.isLocked() : Boolean {
    val registry = Minecraft.getInstance()
        .connection
        ?.registryAccess()
        ?.registry(LOCK_REGISTRY_KEY)
        ?.getOrNull()
        ?: return false

    val isLocked = registry.any { it.isItemLocked(this) }

    println("${this.item.getName(this)} ${if(isLocked) "is" else "is not"} locked!")

    return isLocked
}
