package gg.wildblood.gearsofdiscovery.content.registry.quests

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.entity.player.Player
import java.util.*

/**
 * Manages quest data for a specific player, including accepted and completed quests.
 */
data class PlayerQuestData(
    val playerId: UUID,
    val acceptedQuests: MutableMap<String, QuestInstance> = mutableMapOf(),
    val completedQuests: MutableSet<String> = mutableSetOf()
) {
    
    companion object {
        val CODEC: Codec<PlayerQuestData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("player_id").forGetter { it.playerId.toString() },
                Codec.unboundedMap(Codec.STRING, QuestInstance.CODEC).optionalFieldOf("accepted_quests", mutableMapOf()).forGetter(PlayerQuestData::acceptedQuests),
                Codec.list(Codec.STRING).optionalFieldOf("completed_quests", listOf()).forGetter { it.completedQuests.toList() }
            ).apply(instance) { playerId, acceptedQuests, completedQuests ->
                PlayerQuestData(UUID.fromString(playerId), acceptedQuests.toMutableMap(), completedQuests.toMutableSet())
            }
        }
    }
    
    /**
     * Checks if the player has accepted a specific quest
     */
    fun hasAcceptedQuest(questId: String): Boolean {
        return acceptedQuests.containsKey(questId)
    }
    
    /**
     * Checks if the player has completed a specific quest
     */
    fun hasCompletedQuest(questId: String): Boolean {
        return completedQuests.contains(questId)
    }
    
    /**
     * Accepts a new quest if the player hasn't already accepted or completed it
     */
    fun acceptQuest(questId: String): Boolean {
        if (hasAcceptedQuest(questId) || hasCompletedQuest(questId)) {
            return false
        }
        
        acceptedQuests[questId] = QuestInstance(questId)
        return true
    }
    
    /**
     * Updates progress for all accepted quests and detects newly completable ones
     */
    fun updateProgress(player: Player, questRegistry: Map<String, QuestDefinition>): List<String> {
        val newlyCompletableQuests = mutableListOf<String>()
        
        acceptedQuests.keys.toList().forEach { questId ->
            val questInstance = acceptedQuests[questId] ?: return@forEach
            val questDefinition = questRegistry[questId] ?: return@forEach
            
            val wasCompletable = questInstance.checkCompletion(questDefinition, player)
            val updatedInstance = questInstance.copy()
            updatedInstance.updateProgress(questDefinition, player)
            val isNowCompletable = updatedInstance.checkCompletion(questDefinition, player)
            
            // Store the updated instance back to the map
            acceptedQuests[questId] = updatedInstance
            
            // If quest became completable for the first time, add to notification list
            if (!wasCompletable && isNowCompletable && !updatedInstance.isCompleted) {
                newlyCompletableQuests.add(questId)
            }
        }
        
        return newlyCompletableQuests
    }
    
    /**
     * Manually completes a quest, applying rewards and moving it to completed list
     */
    fun completeQuest(questId: String, player: Player, questRegistry: Map<String, QuestDefinition>): Boolean {
        val questInstance = acceptedQuests[questId] ?: return false
        val questDefinition = questRegistry[questId] ?: return false
        
        // Check if quest can actually be completed
        if (!questInstance.checkCompletion(questDefinition, player) || questInstance.isCompleted) {
            return false
        }
        
        // Mark as completed and move to completed list
        completedQuests.add(questId)
        acceptedQuests.remove(questId)

        questDefinition.objectives.forEach { objective ->
            objective.complete(player)
        }
        
        // Apply rewards
        questDefinition.rewards.forEach { reward ->
            reward.execute(player)
        }
        
        return true
    }
    
    /**
     * Gets the quest instance for a specific quest ID
     */
    fun getQuestInstance(questId: String): QuestInstance? {
        return acceptedQuests[questId]
    }
    
    /**
     * Gets all accepted quest instances
     */
    fun getAllAcceptedQuests(): Map<String, QuestInstance> {
        return acceptedQuests.toMap()
    }
    
    /**
     * Checks if player can accept a quest based on requirements
     */
    fun canAcceptQuest(questDefinition: QuestDefinition): Boolean {
        // Already accepted or completed
        if (hasAcceptedQuest(questDefinition.id) || hasCompletedQuest(questDefinition.id)) {
            return false
        }
        
        // Check requirements - all required quests must be completed
        return questDefinition.requirements.all { requirement ->
            val questId = requirement.removePrefix("quest:")
            hasCompletedQuest(questId)
        }
    }
}