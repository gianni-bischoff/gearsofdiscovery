package gg.wildblood.gearsofdiscovery.content.registry.quests.objectives

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gg.wildblood.gearsofdiscovery.content.registry.quests.ObjectiveDefinition
import gg.wildblood.gearsofdiscovery.utility.extensions.ItemUtilities
import net.minecraft.world.entity.player.Player

class CollectObjective(val itemId: String, val quantity: Int = 1) : ObjectiveDefinition {

    companion object {
        val CODEC: Codec<CollectObjective> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<CollectObjective> ->
            instance.group(
                Codec.STRING.fieldOf("target").forGetter(CollectObjective::itemId),
                Codec.INT.fieldOf("amount").forGetter(CollectObjective::quantity),
            ).apply(instance, ::CollectObjective)
        }
    }

    override fun description(): String = "Collect $quantity of $itemId"

    override fun check(player: Player): Boolean {
        val targetItem = ItemUtilities.getItemById(itemId) ?: return false

        val itemCount = player.inventory.items
            .filter { itemStack -> itemStack.`is`(targetItem) }
            .sumOf { it.count }

        return itemCount >= quantity
    }

    override fun complete(player: Player): Boolean {
        return check(player)
    }
}