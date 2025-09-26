package gg.wildblood.gearsofdiscovery.content.registry.quests.rewards

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gg.wildblood.gearsofdiscovery.content.registry.quests.RewardDefinition
import gg.wildblood.gearsofdiscovery.utility.extensions.ItemUtilities
import net.minecraft.world.entity.player.Player

class ItemRewardDefinition(val itemId: String, val quantity: Int = 1) : RewardDefinition {
    companion object {
        val CODEC: Codec<ItemRewardDefinition> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<ItemRewardDefinition> ->
            instance.group(
                Codec.STRING.fieldOf("item").forGetter(ItemRewardDefinition::itemId),
                Codec.INT.fieldOf("count").forGetter(ItemRewardDefinition::quantity),
            ).apply(instance, ::ItemRewardDefinition)
        }
    }

    override fun description(): String = "You will receive $quantity $itemId"

    override fun execute(player: Player): Boolean {
        return ItemUtilities.getItemById(itemId)?.let {
            player.addItem(it.defaultInstance.copyWithCount(quantity))
        } ?: return false
    }
}