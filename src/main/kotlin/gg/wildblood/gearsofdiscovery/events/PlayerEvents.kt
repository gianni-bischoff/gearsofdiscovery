package gg.wildblood.gearsofdiscovery.events

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.LOGGER
import gg.wildblood.gearsofdiscovery.locks.ModRegistries.LOCK_REGISTRY_KEY
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.WeatheringCopper
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import java.util.stream.Stream
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

    return registry.any { it.isItemLocked(this) }
}
