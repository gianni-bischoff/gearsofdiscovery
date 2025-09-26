package gg.wildblood.gearsofdiscovery.content.registry.quests

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.entity.player.Player
import java.util.*

/**
 * Represents an instance of an accepted quest for a specific player.
 * Tracks progress and state of the quest.
 */
data class QuestInstance(
    val questId: String,
    val acceptedTime: Long = System.currentTimeMillis(),
    val objectiveProgress: MutableMap<Int, Boolean> = mutableMapOf(),
    val isCompleted: Boolean = false,
    val completedTime: Long? = null
) {
    
    companion object {
        val CODEC: Codec<QuestInstance> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("quest_id").forGetter(QuestInstance::questId),
                Codec.LONG.fieldOf("accepted_time").forGetter(QuestInstance::acceptedTime),
                Codec.unboundedMap(Codec.INT, Codec.BOOL).optionalFieldOf("objective_progress", mutableMapOf()).forGetter(QuestInstance::objectiveProgress),
                Codec.BOOL.optionalFieldOf("is_completed", false).forGetter(QuestInstance::isCompleted),
                Codec.LONG.optionalFieldOf("completed_time", -1L).forGetter { it.completedTime ?: -1L }
            ).apply(instance) { questId, acceptedTime, objectiveProgress, isCompleted, completedTime ->
                QuestInstance(questId, acceptedTime, objectiveProgress.toMutableMap(), isCompleted, if (completedTime == -1L) null else completedTime)
            }
        }
    }
    
    /**
     * Checks if all objectives for this quest instance are completed
     */
    fun checkCompletion(questDefinition: QuestDefinition, player: Player): Boolean {
        if (isCompleted) return true
        
        return questDefinition.objectives.mapIndexed { index, objective ->
            objective.check(player)
        }.all { it }
    }
    
    /**
     * Updates objective progress and returns whether the quest is now complete
     */
    fun updateProgress(questDefinition: QuestDefinition, player: Player): Boolean {
        questDefinition.objectives.forEachIndexed { index, objective ->
            objectiveProgress[index] = objective.check(player)
        }
        
        return checkCompletion(questDefinition, player)
    }
    
    /**
     * Marks this quest instance as completed
     */
    fun markCompleted(): QuestInstance {
        return this.copy(isCompleted = true, completedTime = System.currentTimeMillis())
    }
}