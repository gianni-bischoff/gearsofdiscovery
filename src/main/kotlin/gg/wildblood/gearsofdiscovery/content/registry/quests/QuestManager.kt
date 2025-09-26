package gg.wildblood.gearsofdiscovery.content.registry.quests

import gg.wildblood.gearsofdiscovery.utility.extensions.tryGetQuestRegistry
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.storage.WorldData
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import java.util.*

/**
 * Manages quest system operations including player quest data, progress tracking, and persistence.
 */
@EventBusSubscriber
object QuestManager {
    
    // Client-side cache for quest data
    private val clientQuestCache = mutableMapOf<UUID, PlayerQuestData>()
    
    /**
     * Gets the quest save data for the given player's level
     */
    private fun getQuestSaveData(player: Player): QuestSaveData? {
        return if (player is ServerPlayer) {
            QuestSaveData.get(player.serverLevel())
        } else {
            null
        }
    }
    
    /**
     * Gets player quest data, creating it if it doesn't exist
     */
    fun getPlayerQuestData(player: Player): PlayerQuestData {
        val saveData = getQuestSaveData(player)
        return if (saveData != null) {
            // Server-side: use saved data
            val serverData = saveData.getPlayerQuestData(player.uuid)
            // Also update client cache if this is being called from server
            clientQuestCache[player.uuid] = serverData
            serverData
        } else {
            // Client-side: use cached data, or empty data if no cache
            clientQuestCache.getOrPut(player.uuid) {
                PlayerQuestData(player.uuid)
            }
        }
    }
    
    /**
     * Updates the client-side cache with quest data
     */
    fun updateClientCache(playerId: UUID, questData: PlayerQuestData) {
        clientQuestCache[playerId] = questData
    }
    
    /**
     * Clears client cache for a player (useful for logout/login)
     */
    fun clearClientCache(playerId: UUID) {
        clientQuestCache.remove(playerId)
    }
    
    /**
     * Gets all available quests from the registry
     */
    fun getAvailableQuests(): Map<String, QuestDefinition> {
        val questRegistry = Minecraft.getInstance().tryGetQuestRegistry() ?: return emptyMap()
        return questRegistry.associateBy { it.id }
    }
    
    /**
     * Gets quests that a player can accept (meets requirements, not already accepted/completed)
     */
    fun getAcceptableQuests(player: Player): List<QuestDefinition> {
        val playerData = getPlayerQuestData(player)
        val availableQuests = getAvailableQuests()
        
        return availableQuests.values.filter { questDefinition ->
            playerData.canAcceptQuest(questDefinition)
        }
    }
    
    /**
     * Attempts to accept a quest for a player
     */
    fun acceptQuest(player: Player, questId: String): AcceptResult {
        val availableQuests = getAvailableQuests()
        val questDefinition = availableQuests[questId] ?: return AcceptResult.QUEST_NOT_FOUND
        
        val playerData = getPlayerQuestData(player)
        
        if (!playerData.canAcceptQuest(questDefinition)) {
            return when {
                playerData.hasAcceptedQuest(questId) -> AcceptResult.ALREADY_ACCEPTED
                playerData.hasCompletedQuest(questId) -> AcceptResult.ALREADY_COMPLETED
                else -> AcceptResult.REQUIREMENTS_NOT_MET
            }
        }
        
        val success = playerData.acceptQuest(questId)
        
        // Save the updated quest data
        if (success) {
            val saveData = getQuestSaveData(player)
            saveData?.setPlayerQuestData(player.uuid, playerData)
        }
        
        return if (success) AcceptResult.SUCCESS else AcceptResult.FAILED
    }
    
    /**
     * Updates quest progress for a player
     */
    fun updatePlayerProgress(player: Player): List<String> {
        val playerData = getPlayerQuestData(player)
        val availableQuests = getAvailableQuests()
        
        val newlyCompletableQuests = playerData.updateProgress(player, availableQuests)
        
        // Always save the updated quest data when there are accepted quests
        // since quest progress may have been updated even if no quests became newly completable
        if (playerData.acceptedQuests.isNotEmpty()) {
            val saveData = getQuestSaveData(player)
            saveData?.setPlayerQuestData(player.uuid, playerData)
        }
        
        return newlyCompletableQuests
    }
    
    /**
     * Gets accepted quests for a player
     */
    fun getAcceptedQuests(player: Player): Map<String, QuestInstance> {
        return getPlayerQuestData(player).getAllAcceptedQuests()
    }
    
    /**
     * Gets completed quests for a player
     */
    fun getCompletedQuests(player: Player): Set<String> {
        return getPlayerQuestData(player).completedQuests.toSet()
    }
    
    /**
     * Gets detailed progress information for a quest
     */
    fun getQuestProgress(player: Player, questId: String): QuestProgressInfo? {
        val playerData = getPlayerQuestData(player)
        val questInstance = playerData.getQuestInstance(questId) ?: return null
        val availableQuests = getAvailableQuests()
        val questDefinition = availableQuests[questId] ?: return null
        
        val objectiveStatuses = questDefinition.objectives.mapIndexed { index, objective ->
            ObjectiveStatus(
                description = objective.description(),
                isComplete = objective.check(player),
                progress = if (objective.check(player)) "Complete" else "Incomplete"
            )
        }
        
        val isComplete = questInstance.checkCompletion(questDefinition, player)
        
        return QuestProgressInfo(
            questId = questId,
            questTitle = questDefinition.title,
            questDescription = questDefinition.description,
            acceptedTime = questInstance.acceptedTime,
            isComplete = isComplete,
            completedTime = questInstance.completedTime,
            objectives = objectiveStatuses
        )
    }
    
    /**
     * Manually completes a quest for a player
     */
    fun completeQuest(player: Player, questId: String): CompleteResult {
        val playerData = getPlayerQuestData(player)
        val availableQuests = getAvailableQuests()
        val questDefinition = availableQuests[questId] ?: return CompleteResult.QUEST_NOT_FOUND
        val questInstance = playerData.getQuestInstance(questId) ?: return CompleteResult.QUEST_NOT_ACCEPTED
        
        // Check if quest can be completed
        if (!questInstance.checkCompletion(questDefinition, player)) {
            return CompleteResult.OBJECTIVES_NOT_COMPLETE
        }
        
        if (questInstance.isCompleted) {
            return CompleteResult.ALREADY_COMPLETED
        }
        
        // Use the PlayerQuestData method to complete the quest
        val success = playerData.completeQuest(questId, player, availableQuests)
        
        // Save the updated quest data
        if (success) {
            val saveData = getQuestSaveData(player)
            saveData?.setPlayerQuestData(player.uuid, playerData)
        }
        
        return if (success) CompleteResult.SUCCESS else CompleteResult.FAILED
    }
    
    /**
     * Sends a notification to the player when a quest becomes completable
     */
    private fun sendQuestCompletableNotification(player: ServerPlayer, questId: String) {
        val availableQuests = getAvailableQuests()
        val questDefinition = availableQuests[questId] ?: return
        
        val message = Component.literal("")
            .append(Component.literal("✓ Quest Complete: ").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
            .append(Component.literal(questDefinition.title).withStyle(ChatFormatting.YELLOW))
            .append(Component.literal("\n  Click here to hand in your quest: ").withStyle(ChatFormatting.GRAY))
            .append(
                Component.literal("[COMPLETE QUEST]").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                    .withStyle(Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quest complete $questId")))
            )
        
        player.sendSystemMessage(message)
    }
    
    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        if (event.entity is ServerPlayer) {
            val serverPlayer = event.entity as ServerPlayer
            // Update quest progress periodically (every 20 ticks = 1 second)
            if (serverPlayer.tickCount % 20 == 0) {
                val newlyCompletableQuests = updatePlayerProgress(serverPlayer)
                // Send notification to player about newly completable quests
                newlyCompletableQuests.forEach { questId ->
                    sendQuestCompletableNotification(serverPlayer, questId)
                }
            }
        }
    }
    
    @SubscribeEvent
    fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        // Initialize player quest data when they log in
        getPlayerQuestData(event.entity)
    }
    
    /**
     * Result of quest acceptance attempt
     */
    enum class AcceptResult {
        SUCCESS,
        QUEST_NOT_FOUND,
        ALREADY_ACCEPTED,
        ALREADY_COMPLETED,
        REQUIREMENTS_NOT_MET,
        FAILED
    }
    
    /**
     * Result of quest completion attempt
     */
    enum class CompleteResult {
        SUCCESS,
        QUEST_NOT_FOUND,
        QUEST_NOT_ACCEPTED,
        OBJECTIVES_NOT_COMPLETE,
        ALREADY_COMPLETED,
        FAILED
    }
    
    /**
     * Detailed quest progress information
     */
    data class QuestProgressInfo(
        val questId: String,
        val questTitle: String,
        val questDescription: String,
        val acceptedTime: Long,
        val isComplete: Boolean,
        val completedTime: Long?,
        val objectives: List<ObjectiveStatus>
    )
    
    /**
     * Status of a single objective
     */
    data class ObjectiveStatus(
        val description: String,
        val isComplete: Boolean,
        val progress: String
    )
}