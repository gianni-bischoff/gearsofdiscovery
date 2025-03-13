package gg.wildblood.gearsofdiscovery.events

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.LOGGER
import gg.wildblood.gearsofdiscovery.locks.Lock
import gg.wildblood.gearsofdiscovery.locks.hasTypeLock
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.ChatType
import net.minecraft.network.chat.Component
import net.minecraft.world.level.block.Block
import net.neoforged.bus.api.ICancellableEvent
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent
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

    @SubscribeEvent
    fun onEntityTravelToDimensionEvent(event: EntityTravelToDimensionEvent) {
        event.isCanceled = event.dimension.hasTypeLock(Lock.Type.DIMENSION_TRAVEL)
    }

    @SubscribeEvent
    fun onClientChatReceiveEvent(event: ClientChatReceivedEvent) {
        /** Here Chat Colors and Stuff.
        if(event.boundChatType?.chatType == ChatType.CHAT || !event.message.string.startsWith("<")) return
        
        val chatMessage = ChatMessage.parseMessage(event.message.string)
        event.message = Component.literal("")
            .append(Component.literal("[Admin] ").withStyle(ChatFormatting.GOLD))
            .append(Component.literal(chatMessage.displayName).withStyle(ChatFormatting.RED))
            .append(Component.literal(": ${chatMessage.message}").withStyle(ChatFormatting.WHITE))
        */
    }

    private fun <T> getTargetBlock(event: T) : Block where T : PlayerInteractEvent, T : ICancellableEvent {
        return event.level.getBlockState(event.pos).block
    }
}

data class ChatMessage(
    val displayName: String,
    val message: String
) {
    companion object {
        fun parseMessage(input: String): ChatMessage {
            val pattern = "<(.+)> (.+)".toRegex()
            val matchResult = pattern.find(input)

            return if (matchResult != null) {
                ChatMessage(
                    displayName = matchResult.groupValues[1],
                    message = matchResult.groupValues[2]
                )
            } else {
                ChatMessage("", input) // Fallback für nicht übereinstimmende Nachrichten
            }
        }
    }
}