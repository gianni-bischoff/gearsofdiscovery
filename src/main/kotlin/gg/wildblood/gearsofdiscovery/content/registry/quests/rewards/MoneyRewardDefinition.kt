package gg.wildblood.gearsofdiscovery.content.registry.quests.rewards

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gg.wildblood.gearsofdiscovery.content.registry.quests.RewardDefinition
import net.minecraft.world.entity.player.Player

class MoneyRewardDefinition(val amount: Int) : RewardDefinition {
    companion object {
        val CODEC: Codec<MoneyRewardDefinition> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<MoneyRewardDefinition> ->
            instance.group(
                Codec.INT.fieldOf("amount").forGetter(MoneyRewardDefinition::amount),
            ).apply(instance, ::MoneyRewardDefinition)
        }
        
        init {
            RewardCodecs.register(CODEC, "money_reward", MoneyRewardDefinition::class)
        }
    }

    override fun description(): String = "You will receive $amount coins"

    override fun execute(player: Player): Boolean {
        // TODO: Implement money system integration
        // For now, just return true as a placeholder
        return true
    }
}