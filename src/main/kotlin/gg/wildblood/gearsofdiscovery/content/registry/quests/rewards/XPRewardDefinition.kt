package gg.wildblood.gearsofdiscovery.content.registry.quests.rewards

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gg.wildblood.gearsofdiscovery.content.registry.quests.RewardDefinition
import net.minecraft.world.entity.player.Player

class XPRewardDefinition(val quantity: Int = 1) : RewardDefinition {
    companion object {
        val CODEC: Codec<XPRewardDefinition> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<XPRewardDefinition> ->
            instance.group(
                Codec.INT.fieldOf("quantity").forGetter(XPRewardDefinition::quantity),
            ).apply(instance, ::XPRewardDefinition)
        }
        init {
            RewardCodecs.register(CODEC, "xp_reward", XPRewardDefinition::class)
        }
    }

    override fun description(): String = "Gain $quantity experience points"

    override fun execute(player: Player): Boolean {
        player.giveExperiencePoints(quantity)
        return true
    }
}