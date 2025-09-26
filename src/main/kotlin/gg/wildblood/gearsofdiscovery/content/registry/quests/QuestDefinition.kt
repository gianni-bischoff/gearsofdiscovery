package gg.wildblood.gearsofdiscovery.content.registry.quests

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gg.wildblood.gearsofdiscovery.content.registry.quests.objectives.ObjectiveCodecs
import gg.wildblood.gearsofdiscovery.content.registry.quests.rewards.RewardCodecs

data class QuestDefinition(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val requirements: List<String> = listOf(),
    val objectives: List<ObjectiveDefinition> = listOf(),
    val rewards: List<RewardDefinition> = listOf(),
    val meta: Map<String, String> = mapOf()
) {
    companion object {
        val CODEC: Codec<QuestDefinition> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<QuestDefinition> ->
            instance.group(
                Codec.STRING.fieldOf("id").forGetter(QuestDefinition::id),
                Codec.STRING.fieldOf("type").forGetter(QuestDefinition::type),
                Codec.STRING.fieldOf("title").forGetter(QuestDefinition::title),
                Codec.STRING.fieldOf("description").forGetter(QuestDefinition::description),
                Codec.list(Codec.STRING).optionalFieldOf("requirements", listOf()).forGetter(QuestDefinition::requirements),
                Codec.list(ObjectiveCodecs.POLYMORPHIC_CODEC).optionalFieldOf("objectives", listOf()).forGetter(QuestDefinition::objectives),
                Codec.list(RewardCodecs.POLYMORPHIC_CODEC).optionalFieldOf("rewards", listOf()).forGetter(QuestDefinition::rewards),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("meta", mapOf()).forGetter(QuestDefinition::meta)
            ).apply(instance) { id, type, title, description, requirements, objectives, rewards, meta ->
                QuestDefinition(id, type, title, description, requirements, objectives, rewards, meta)
            }
        }
    }
}