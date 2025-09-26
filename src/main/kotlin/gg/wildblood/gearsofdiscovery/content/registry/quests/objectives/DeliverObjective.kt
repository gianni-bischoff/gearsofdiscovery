package gg.wildblood.gearsofdiscovery.content.registry.quests.objectives

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gg.wildblood.gearsofdiscovery.content.registry.quests.ObjectiveDefinition
import gg.wildblood.gearsofdiscovery.content.registry.quests.ItemTarget
import net.minecraft.world.entity.player.Player

class DeliverObjective(val itemTarget: ItemTarget, val quantity: Int = 1) : ObjectiveDefinition {

    companion object {
        val CODEC: Codec<DeliverObjective> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<DeliverObjective> ->
            instance.group(
                ItemTarget.CODEC.fieldOf("target").forGetter(DeliverObjective::itemTarget),
                Codec.INT.fieldOf("quantity").forGetter(DeliverObjective::quantity),
            ).apply(instance, ::DeliverObjective)
        }
    }

    override fun description(): String = "Collect and Deliver $quantity of ${itemTarget.getDescription()}"

    override fun check(player: Player): Boolean {
        val itemCount = player.inventory.items
            .filter { itemStack -> itemTarget.matches(itemStack) }
            .sumOf { it.count }

        return itemCount >= quantity
    }

    override fun complete(player: Player): Boolean {
        if (!check(player)) return false

        return player.inventory.items
            .filter { itemTarget.matches(it) }
            .fold(quantity) { remaining, stack ->
                val amountToRemove = minOf(remaining, stack.count)
                stack.shrink(amountToRemove)
                remaining - amountToRemove
            } <= 0
    }
}