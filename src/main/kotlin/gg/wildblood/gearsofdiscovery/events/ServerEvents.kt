package gg.wildblood.gearsofdiscovery.events

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.LOGGER
import gg.wildblood.gearsofdiscovery.content.commands.LockCommand
import gg.wildblood.gearsofdiscovery.config.Config
import gg.wildblood.gearsofdiscovery.content.ModDataAttachments
import gg.wildblood.gearsofdiscovery.network.ModClientPayloadHandler
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import java.util.*

@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.GAME)
object ServerEvents {

    @SubscribeEvent
    fun onPlayerJoinEvent(event: PlayerEvent.PlayerLoggedInEvent) {
        LOGGER.info("Player ${event.entity.name} Joined!")

        val player = event.entity as ServerPlayer

        ModClientPayloadHandler.updateUnlocksForPlayer(player)

        val firstJoin = player.getData(ModDataAttachments.FIRST_JOIN)
        val lastJoinDate = Date(player.getData(ModDataAttachments.LAST_JOIN))

        if(firstJoin) {
            Config.startingEquipment.forEach { item ->
                player.addItem(item)
            }
            player.setData(ModDataAttachments.FIRST_JOIN, false)
        }

        if(!lastJoinDate.isSameDay(Date())) {
            player.addItem(Items.EMERALD.defaultInstance)

            player.setData(ModDataAttachments.LAST_JOIN, Date().time)
        }
    }

    @SubscribeEvent
    fun onRegisterCommandsEvent(event: RegisterCommandsEvent) {
        LockCommand.register(event.dispatcher)
    }
}

private fun Date.isSameDay(targetDate: Date): Boolean {
    val selfCalender = Calendar.getInstance()
    val targetCalender = Calendar.getInstance()
    selfCalender.time = this
    targetCalender.time = targetDate

    return selfCalender.get(Calendar.YEAR) == targetCalender.get(Calendar.YEAR) &&
            selfCalender.get(Calendar.DAY_OF_YEAR) == targetCalender.get(Calendar.DAY_OF_YEAR)
}