package gg.wildblood.gearsofdiscovery.events

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.LOGGER
import gg.wildblood.gearsofdiscovery.content.commands.LockCommand
import gg.wildblood.gearsofdiscovery.content.commands.QuestTestCommand
import gg.wildblood.gearsofdiscovery.config.Config
import gg.wildblood.gearsofdiscovery.content.ModDataAttachments
import gg.wildblood.gearsofdiscovery.network.ModClientPayloadHandler
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import java.util.*

@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.GAME)
object ServerEvents {

    @SubscribeEvent
    fun onPlayerJoinEvent(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity as ServerPlayer

        ModClientPayloadHandler.updateUnlocksForPlayer(player)

        val firstJoin = player.getData(ModDataAttachments.FIRST_JOIN)
        val lastJoinDate = Date(player.getData(ModDataAttachments.LAST_JOIN))

        LOGGER.info("Player ${event.entity.name} Joined! First Join: $firstJoin, Last Join: $lastJoinDate")
        if(firstJoin) {
            Config.startingEquipment.forEach { item ->
                LOGGER.info("Adding item ${item.hoverName.string} to player ${event.entity.name}")
                player.addItem(item)
            }
            LOGGER.info("Player ${event.entity.name} has been given their starting equipment.")

            player.sendSystemMessage(Component.literal("You have been given your Starter equipment!\n${Config.startingEquipment.joinToString("\n") { "- ${it.hoverName.string} x${it.count}" }}"))
            player.setData(ModDataAttachments.FIRST_JOIN, false)
        }

        if(!lastJoinDate.isSameDay(Date())) {
            Config.dailyEquipment.forEach {
                val item = it.copy()
                LOGGER.info("Adding item ${item.hoverName.string} to player ${event.entity.name}")
                player.addItem(item)
            }
            LOGGER.info("Player ${event.entity.name} has been given their daily reward.")

            player.sendSystemMessage(Component.literal("You have been given your Daily reward!\n${Config.dailyEquipment.joinToString("\n") { "- ${it.hoverName.string} x${it.count}" }}"))

            player.setData(ModDataAttachments.LAST_JOIN, Date().time)
        }

    }

    @SubscribeEvent
    fun onRegisterCommandsEvent(event: RegisterCommandsEvent) {
        LockCommand.register(event.dispatcher)
        QuestTestCommand.register(event.dispatcher)
    }
}

private fun Date.isSameDay(targetDate: Date): Boolean {
    val selfCalender = Calendar.getInstance()
    val targetCalender = Calendar.getInstance()
    selfCalender.time = this
    targetCalender.time = targetDate

    return selfCalender.get(Calendar.DAY_OF_YEAR) == targetCalender.get(Calendar.DAY_OF_YEAR)
}