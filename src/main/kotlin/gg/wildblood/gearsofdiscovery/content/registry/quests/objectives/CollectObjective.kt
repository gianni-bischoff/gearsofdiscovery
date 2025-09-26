package gg.wildblood.gearsofdiscovery.content.registry.quests.objectives

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gg.wildblood.gearsofdiscovery.content.registry.quests.ObjectiveDefinition
import gg.wildblood.gearsofdiscovery.content.registry.quests.ItemTarget
import net.minecraft.world.entity.player.Player

class CollectObjective(val itemTarget: ItemTarget, val quantity: Int = 1) : ObjectiveDefinition {

    companion object {
        val CODEC: Codec<CollectObjective> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<CollectObjective> ->
            instance.group(
                ItemTarget.CODEC.fieldOf("target").forGetter(CollectObjective::itemTarget),
                Codec.INT.fieldOf("amount").forGetter(CollectObjective::quantity),
            ).apply(instance, ::CollectObjective)
        }
    }

    override fun description(): String = "Collect $quantity of ${itemTarget.getDescription()}"

    override fun check(player: Player): Boolean {
        val itemCount = player.inventory.items
            .filter { itemStack -> itemTarget.matches(itemStack) }
            .sumOf { it.count }

        return itemCount >= quantity
    }

    override fun complete(player: Player): Boolean {
        return check(player)
    }
}