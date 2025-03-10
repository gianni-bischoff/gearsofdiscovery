package gg.wildblood.gearsofdiscovery.events

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.LOGGER
import gg.wildblood.gearsofdiscovery.commands.LockCommand
import gg.wildblood.gearsofdiscovery.network.ModClientPayloadHandler
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent

@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.GAME)
object ServerEvents {

    @SubscribeEvent
    fun onPlayerJoinEvent(event: PlayerEvent.PlayerLoggedInEvent) {
        LOGGER.info("Player ${event.entity.name} Joined!")

        ModClientPayloadHandler.updateUnlocksForPlayer(event.entity as Player)

    }

    @SubscribeEvent
    fun onRegisterCommandsEvent(event: RegisterCommandsEvent) {
        LockCommand.register(event.dispatcher)
    }
}