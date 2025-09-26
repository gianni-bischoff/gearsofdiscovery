package gg.wildblood.gearsofdiscovery.client.ui.quest

import gg.wildblood.gearsofdiscovery.content.registry.quests.QuestDefinition
import gg.wildblood.gearsofdiscovery.content.registry.quests.QuestManager
import net.minecraft.world.entity.player.Player
import java.util.*

/**
 * Represents a single quest node in the quest tree
 */
data class QuestNode(
    val questDefinition: QuestDefinition,
    val x: Int,
    val y: Int,
    val dependencies: List<String> = listOf(),
    val dependents: MutableList<String> = mutableListOf(),
    val status: QuestStatus = QuestStatus.LOCKED
) {
    enum class QuestStatus {
        LOCKED,           // Dependencies not met
        AVAILABLE,        // Can be accepted
        ACCEPTED,         // Currently in progress
        READY_TO_COMPLETE, // Objectives completed, ready for delivery
        COMPLETED         // Finished
    }
}

/**
 * Represents the entire quest tree structure
 */
class QuestTree {
    private val nodes = mutableMapOf<String, QuestNode>()
    private val rootNodes = mutableListOf<String>()
    
    /**
     * Builds the quest tree from available quest definitions
     */
    fun buildTree(quests: Map<String, QuestDefinition>): QuestTree {
        nodes.clear()
        rootNodes.clear()
        
        // First pass: Create all nodes
        quests.values.forEach { quest ->
            val dependencies = quest.requirements
                .filter { it.startsWith("quest:") }
                .map { it.removePrefix("quest:") }
            
            nodes[quest.id] = QuestNode(
                questDefinition = quest,
                x = 0, // Will be calculated in layout phase
                y = 0, // Will be calculated in layout phase
                dependencies = dependencies,
                status = QuestNode.QuestStatus.LOCKED
            )
        }
        
        // Second pass: Build dependency relationships and find root nodes
        nodes.forEach { (questId, node) ->
            if (node.dependencies.isEmpty()) {
                rootNodes.add(questId)
            } else {
                // Add this quest as a dependent to its dependencies
                node.dependencies.forEach { depId ->
                    nodes[depId]?.dependents?.add(questId)
                }
            }
        }
        
        // Third pass: Calculate positions using a simple tree layout
        calculateLayout()
        
        return this
    }
    
    /**
     * Updates quest statuses based on player data
     */
    fun updateStatuses(player: Player, acceptedQuests: Set<String>, completedQuests: Set<String>) {
        nodes.forEach { (questId, node) ->
            val newStatus = when {
                completedQuests.contains(questId) -> QuestNode.QuestStatus.COMPLETED
                acceptedQuests.contains(questId) -> {
                    // Check if the accepted quest is ready for completion
                    val progressInfo = QuestManager.getQuestProgress(player, questId)
                    if (progressInfo?.isComplete == true) {
                        QuestNode.QuestStatus.READY_TO_COMPLETE
                    } else {
                        QuestNode.QuestStatus.ACCEPTED
                    }
                }
                canAcceptQuest(questId, completedQuests) -> QuestNode.QuestStatus.AVAILABLE
                else -> QuestNode.QuestStatus.LOCKED
            }
            
            nodes[questId] = node.copy(status = newStatus)
        }
    }
    
    /**
     * Checks if a quest can be accepted based on completed dependencies
     */
    private fun canAcceptQuest(questId: String, completedQuests: Set<String>): Boolean {
        val node = nodes[questId] ?: return false
        return node.dependencies.all { depId -> completedQuests.contains(depId) }
    }
    
    /**
     * Simple tree layout algorithm - positions nodes in layers
     */
    private fun calculateLayout() {
        val layers = mutableMapOf<Int, MutableList<String>>()
        val visited = mutableSetOf<String>()
        
        // Calculate layer for each node (depth from root)
        fun calculateLayer(questId: String): Int {
            if (visited.contains(questId)) return 0
            visited.add(questId)
            
            val node = nodes[questId] ?: return 0
            val maxDepLayer = if (node.dependencies.isEmpty()) {
                0
            } else {
                node.dependencies.maxOf { depId -> calculateLayer(depId) + 1 }
            }
            
            layers.computeIfAbsent(maxDepLayer) { mutableListOf() }.add(questId)
            return maxDepLayer
        }
        
        // Calculate layers for all nodes
        nodes.keys.forEach { calculateLayer(it) }
        
        // Position nodes within each layer
        val nodeSpacingX = 120
        val nodeSpacingY = 80
        val layerHeight = 60
        
        layers.forEach { (layer, questIds) ->
            val startX = -(questIds.size - 1) * nodeSpacingX / 2
            questIds.forEachIndexed { index, questId ->
                val node = nodes[questId]!!
                nodes[questId] = node.copy(
                    x = startX + index * nodeSpacingX,
                    y = layer * (layerHeight + nodeSpacingY)
                )
            }
        }
    }
    
    /**
     * Gets all quest nodes
     */
    fun getAllNodes(): Collection<QuestNode> = nodes.values
    
    /**
     * Gets a specific quest node
     */
    fun getNode(questId: String): QuestNode? = nodes[questId]
    
    /**
     * Gets all root nodes (quests with no dependencies)
     */
    fun getRootNodes(): List<QuestNode> = rootNodes.mapNotNull { nodes[it] }
    
    /**
     * Gets the dependencies of a quest as nodes
     */
    fun getDependencies(questId: String): List<QuestNode> {
        val node = nodes[questId] ?: return emptyList()
        return node.dependencies.mapNotNull { nodes[it] }
    }
    
    /**
     * Gets the dependents of a quest as nodes
     */
    fun getDependents(questId: String): List<QuestNode> {
        val node = nodes[questId] ?: return emptyList()
        return node.dependents.mapNotNull { nodes[it] }
    }
    
    /**
     * Gets the total bounds of the quest tree for UI sizing
     */
    fun getBounds(): QuestTreeBounds {
        if (nodes.isEmpty()) return QuestTreeBounds(0, 0, 0, 0)
        
        val minX = nodes.values.minOf { it.x }
        val maxX = nodes.values.maxOf { it.x }
        val minY = nodes.values.minOf { it.y }
        val maxY = nodes.values.maxOf { it.y }
        
        return QuestTreeBounds(minX, minY, maxX, maxY)
    }
}

/**
 * Represents the bounds of the quest tree
 */
data class QuestTreeBounds(
    val minX: Int,
    val minY: Int,
    val maxX: Int,
    val maxY: Int
) {
    val width: Int get() = maxX - minX
    val height: Int get() = maxY - minY
}