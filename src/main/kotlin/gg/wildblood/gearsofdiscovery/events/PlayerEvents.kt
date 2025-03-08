package gg.wildblood.gearsofdiscovery.events

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.LOGGER
import gg.wildblood.gearsofdiscovery.datamaps.Lock
import gg.wildblood.gearsofdiscovery.datamaps.asString
import gg.wildblood.gearsofdiscovery.locks.ModRegistries.LOCK_REGISTRY_KEY
import gg.wildblood.gearsofdiscovery.network.ModClientPayloadHandler
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import kotlin.jvm.optionals.getOrNull

@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.GAME)
object PlayerEvents {

    init {
        LOGGER.info("Player Events Registered!")
    }

    @SubscribeEvent
    fun onRightClickBlockEvent(event: PlayerInteractEvent.RightClickBlock) {
        event.isCanceled = targetBlockLocked(event) || event.itemStack.isLocked()
    }

    @SubscribeEvent
    fun onLeftClickBlock(event: PlayerInteractEvent.LeftClickBlock) {
        event.isCanceled = targetBlockLocked(event) || event.itemStack.isLocked()
    }

    @SubscribeEvent
    fun onRightClickItemEvent(event: PlayerInteractEvent.RightClickItem) {
        when(event.itemStack.item) {
            Items.STICK -> ModClientPayloadHandler.serverAddUnlock(event.entity,"fish")
            Items.FISHING_ROD -> ModClientPayloadHandler.serverRemoveUnlock(event.entity,"fish")
        }

        event.isCanceled = event.itemStack.isLocked()
    }

    @SubscribeEvent
    fun onEntityInteract(event: PlayerInteractEvent.EntityInteract) {
        event.isCanceled = event.itemStack.isLocked()
    }

    private fun <T> targetBlockLocked(event: T) : Boolean where T : PlayerInteractEvent, T : ICancellableEvent {
        return event.level.getBlockState(event.pos).block.isLocked()
    }
}

fun ItemStack.isLocked() : Boolean {
    val registry = Minecraft.getInstance()
        .connection
        ?.registryAccess()
        ?.registry(LOCK_REGISTRY_KEY)
        ?.getOrNull()
        ?: return false

    val isLocked = registry.activeLocksOf(Lock.Type.ITEM_USE).any { it.isItemLocked(this) }

    println("${this.item.getName(this)} ${if(isLocked) "is" else "is not"} locked!")

    return isLocked
}

fun Registry<Lock>.activeLocksOf(type: Lock.Type): List<Lock> {
    return this.filter { it.type == type }.filter { it.enabled }
}

fun Block.isLocked() : Boolean {
    val registry = Minecraft.getInstance()
        .connection
        ?.registryAccess()
        ?.registry(LOCK_REGISTRY_KEY)
        ?.getOrNull()
        ?: return false

    return registry.activeLocksOf(Lock.Type.INTERACT_WITH).any { it.items.contains(BuiltInRegistries.BLOCK.getKey(this).asString()) }
}
