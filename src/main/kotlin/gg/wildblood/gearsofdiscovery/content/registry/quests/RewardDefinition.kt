package gg.wildblood.gearsofdiscovery.content.registry.quests

import net.minecraft.world.entity.player.Player

interface RewardDefinition {
    fun description() : String
    fun execute(player: Player) : Boolean
}