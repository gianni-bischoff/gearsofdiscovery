package gg.wildblood.gearsofdiscovery.content.registry.quests.rewards

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gg.wildblood.gearsofdiscovery.content.registry.quests.RewardDefinition
import gg.wildblood.gearsofdiscovery.content.registry.quests.ItemTarget
import net.minecraft.world.entity.player.Player

class ItemRewardDefinition(val itemTarget: ItemTarget, val quantity: Int = 1) : RewardDefinition {
    companion object {
        val CODEC: Codec<ItemRewardDefinition> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<ItemRewardDefinition> ->
            instance.group(
                ItemTarget.CODEC.fieldOf("target").forGetter(ItemRewardDefinition::itemTarget),
                Codec.INT.fieldOf("count").forGetter(ItemRewardDefinition::quantity),
            ).apply(instance, ::ItemRewardDefinition)
        }
    }

    override fun description(): String = "You will receive $quantity ${itemTarget.getDescription()}"

    override fun execute(player: Player): Boolean {
        // For rewards, we need to pick a specific item
        // If it's an item ID, use that item directly
        // If it's a tag, pick the first available item from the tag
        val targetItem = when {
            itemTarget.itemId != null -> itemTarget.getItem()
            itemTarget.itemTag != null -> {
                val matchingItems = itemTarget.getMatchingItems()
                matchingItems.firstOrNull()
            }
            else -> null
        }
        
        return targetItem?.let { item ->
            player.addItem(item.defaultInstance.copyWithCount(quantity))
        } ?: false
    }
}