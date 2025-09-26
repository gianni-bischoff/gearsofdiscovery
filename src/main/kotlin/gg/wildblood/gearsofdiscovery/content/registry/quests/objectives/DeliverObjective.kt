package gg.wildblood.gearsofdiscovery.content.registry.quests.objectives

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gg.wildblood.gearsofdiscovery.content.registry.quests.ObjectiveDefinition
import gg.wildblood.gearsofdiscovery.utility.extensions.ItemUtilities
import net.minecraft.world.entity.player.Player

class DeliverObjective(val itemId: String, val quantity: Int = 1) : ObjectiveDefinition {

    companion object {
        val CODEC: Codec<DeliverObjective> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<DeliverObjective> ->
            instance.group(
                Codec.STRING.fieldOf("item").forGetter(DeliverObjective::itemId),
                Codec.INT.fieldOf("quantity").forGetter(DeliverObjective::quantity),
            ).apply(instance, ::DeliverObjective)
        }
    }

    override fun description(): String = "Collect and Deliver $quantity of $itemId"

    override fun check(player: Player): Boolean {
        val targetItem = ItemUtilities.getItemById(itemId) ?: return false

        val itemCount = player.inventory.items
            .filter { itemStack -> itemStack.`is`(targetItem) }
            .sumOf { it.count }

        return itemCount >= quantity
    }

    override fun complete(player: Player): Boolean {
        val targetItem = ItemUtilities.getItemById(itemId) ?: return false
        if (!check(player)) return false

        return player.inventory.items
            .filter { it.`is`(targetItem) }
            .fold(quantity) { remaining, stack ->
                val amountToRemove = minOf(remaining, stack.count)
                stack.shrink(amountToRemove)
                remaining - amountToRemove
            } <= 0
    }
}