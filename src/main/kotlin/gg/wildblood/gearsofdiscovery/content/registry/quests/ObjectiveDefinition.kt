package gg.wildblood.gearsofdiscovery.content.registry.quests

import net.minecraft.world.entity.player.Player

interface ObjectiveDefinition {
    fun description() : String
    fun check(player: Player) : Boolean
    fun complete(player: Player) : Boolean
}