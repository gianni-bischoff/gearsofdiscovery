package gg.wildblood.gearsofdiscovery.network

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.config.BaseSaveData
import gg.wildblood.gearsofdiscovery.content.ModRegistries.LOCK_REGISTRY_KEY
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.IPayloadContext
import kotlin.jvm.optionals.getOrNull

@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.MOD)
object ModNetwork {
    @SubscribeEvent
    fun onRegisterPayloadHandler(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1")
        registrar.playToClient(
            UnlockList.TYPE,
            UnlockList.STREAM_CODEC,
            ModClientPayloadHandler::syncUnlockList
        )
    }
}

object ModClientPayloadHandler {
    fun serverAddUnlock(player: Player, vararg unlockNames: String) {
        if(player.server == null) return;

        val saveData = player.server?.let { BaseSaveData.get(it.overworld()) }

        saveData?.addUnlock(unlockNames)

        updateUnlocks(saveData!!)
    }

    fun serverRemoveUnlock(player: Player, vararg unlockNames: String) {
        if(player.server == null) return;

        val saveData = player.server?.let { BaseSaveData.get(it.overworld()) }

        saveData?.removeUnlock(unlockNames)

        updateUnlocks(saveData!!)
    }

    fun updateUnlocksForPlayer(player: Player) {
        if(player.server == null) return;

        val saveData = player.server?.let { BaseSaveData.get(it.overworld()) }

        if (saveData != null) {
            PacketDistributor.sendToAllPlayers(UnlockList(saveData.unlockList))
        } else {
            GearsOfDiscoveryMod.LOGGER.info("Server Save Data is null!")
        }
    }

    private fun updateUnlocks(saveData: BaseSaveData) {
        val registry = Minecraft.getInstance() // Perfect example of full trust Programming.
            .connection
            ?.registryAccess()
            ?.registry(LOCK_REGISTRY_KEY)
            ?.getOrNull()

        registry?.forEach { it.enabled = !saveData.unlockList.contains(it.name) }

        PacketDistributor.sendToAllPlayers(UnlockList(saveData.unlockList))
    }


    fun syncUnlockList(unlockList: UnlockList, context: IPayloadContext) {
        println("${context.player().name} has Packet received.")
        context.enqueueWork {
            val registry = Minecraft.getInstance() // Perfect example of full trust Programming.
                .connection
                ?.registryAccess()
                ?.registry(LOCK_REGISTRY_KEY)
                ?.getOrNull()

            registry?.forEach { it.enabled = !unlockList.lockNames.contains(it.name)}
        }.exceptionally {
            context.disconnect(Component.literal("You are gay"))
            null
        }
    }
}