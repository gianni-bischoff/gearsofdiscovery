package gg.wildblood.gearsofdiscovery.client

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.client.ui.quest.QuestTreeScreen
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import org.lwjgl.glfw.GLFW

@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientEvents {
    
    lateinit var OPEN_QUEST_TREE: KeyMapping
    
    @SubscribeEvent
    fun onRegisterKeyMappings(event: RegisterKeyMappingsEvent) {
        OPEN_QUEST_TREE = KeyMapping(
            "key.gearsofdiscovery.open_quest_tree",
            GLFW.GLFW_KEY_K,
            "key.categories.gearsofdiscovery"
        )
        event.register(OPEN_QUEST_TREE)
    }
}

@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.CLIENT])
object ClientGameEvents {
    
    @SubscribeEvent
    fun onKeyInput(event: InputEvent.Key) {
        val minecraft = Minecraft.getInstance()
        
        if (minecraft.screen == null && ClientEvents.OPEN_QUEST_TREE.consumeClick()) {
            // Open the quest tree screen
            minecraft.setScreen(QuestTreeScreen())
        }
    }
}