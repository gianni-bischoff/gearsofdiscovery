package gg.wildblood.gearsofdiscovery.client.ui.quest

import com.mojang.blaze3d.systems.RenderSystem
import gg.wildblood.gearsofdiscovery.content.registry.quests.QuestManager
import gg.wildblood.gearsofdiscovery.utility.extensions.tryGetQuestRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.math.max
import kotlin.math.min

/**
 * Main quest tree UI screen
 */
class QuestTreeScreen : Screen(Component.literal("Quest Tree")) {
    
    private val questTree = QuestTree()
    private var cameraX = 0.0
    private var cameraY = 0.0
    private val nodeSize = 32
    private val nodeSpacing = 120
    private var isDragging = false
    private var lastMouseX = 0.0
    private var lastMouseY = 0.0
    private var hoveredNode: QuestNode? = null
    
    companion object {
        private val QUEST_NODE_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/generic_54.png")
        private const val NODE_SIZE = 32
        private const val CONNECTION_WIDTH = 2
    }
    
    override fun init() {
        super.init()
        refreshQuestData()
    }
    
    /**
     * Refreshes quest data from server and rebuilds the quest tree
     */
    private fun refreshQuestData() {
        // Request fresh quest data from server
        val player = minecraft?.player
        if (player != null) {
            // Send command to get current quest progress - this will sync server data
            minecraft?.player?.connection?.sendCommand("quest progress")
        }
        
        // Build the quest tree from available quests
        val questRegistry = Minecraft.getInstance().tryGetQuestRegistry()
        if (questRegistry != null) {
            val questMap = questRegistry.associateBy { it.id }
            questTree.buildTree(questMap)
            
            // Update quest statuses based on player data
            if (player != null) {
                val acceptedQuests = QuestManager.getAcceptedQuests(player).keys
                val completedQuests = QuestManager.getCompletedQuests(player)
                questTree.updateStatuses(player, acceptedQuests, completedQuests)
            }
        }
        
        // Center camera on the quest tree
        val bounds = questTree.getBounds()
        cameraX = -(bounds.minX + bounds.width / 2.0)
        cameraY = -(bounds.minY + bounds.height / 2.0)
    }
    
    override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        // Override to prevent the default blurred background
        // Render our own dark background instead
        guiGraphics.fill(0, 0, width, height, 0x80000000.toInt())
    }
    
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        // Render background first
        renderBackground(guiGraphics, mouseX, mouseY, partialTick)
        
        // Set up camera transform
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()
        poseStack.translate(width / 2.0 + cameraX, height / 2.0 + cameraY, 0.0)
        
        // Render connections between nodes
        renderConnections(guiGraphics)
        
        // Render quest nodes
        hoveredNode = null
        questTree.getAllNodes().forEach { node ->
            val screenX = node.x
            val screenY = node.y
            
            // Check if mouse is hovering over this node
            val worldMouseX = mouseX - width / 2 - cameraX
            val worldMouseY = mouseY - height / 2 - cameraY
            
            if (worldMouseX >= screenX - NODE_SIZE / 2 && worldMouseX <= screenX + NODE_SIZE / 2 &&
                worldMouseY >= screenY - NODE_SIZE / 2 && worldMouseY <= screenY + NODE_SIZE / 2) {
                hoveredNode = node
            }
            
            renderQuestNode(guiGraphics, node, screenX, screenY)
        }
        
        poseStack.popPose()
        
        // Render tooltip for hovered node (outside camera transform)
        hoveredNode?.let { node ->
            renderTooltip(guiGraphics, node, mouseX, mouseY)
        }
        
        // Don't call super.render() to avoid blur effects
    }
    
    private fun renderConnections(guiGraphics: GuiGraphics) {
        questTree.getAllNodes().forEach { node ->
            // Draw lines to all dependents
            questTree.getDependents(node.questDefinition.id).forEach { dependent ->
                val startX = node.x
                val startY = node.y + NODE_SIZE / 2
                val endX = dependent.x
                val endY = dependent.y - NODE_SIZE / 2
                
                // Choose color based on connection status
                val color = when {
                    dependent.status == QuestNode.QuestStatus.COMPLETED -> 0xFF00FF00.toInt() // Green
                    dependent.status == QuestNode.QuestStatus.AVAILABLE -> 0xFFFFFF00.toInt() // Yellow
                    dependent.status == QuestNode.QuestStatus.ACCEPTED -> 0xFF00FFFF.toInt() // Cyan
                    else -> 0xFF666666.toInt() // Gray
                }
                
                drawLine(guiGraphics, startX, startY, endX, endY, color)
            }
        }
    }
    
    private fun drawLine(guiGraphics: GuiGraphics, x1: Int, y1: Int, x2: Int, y2: Int, color: Int) {
        // Simple line drawing - can be improved with actual line rendering
        val dx = x2 - x1
        val dy = y2 - y1
        val steps = max(kotlin.math.abs(dx), kotlin.math.abs(dy))
        
        if (steps == 0) return
        
        val xStep = dx.toFloat() / steps
        val yStep = dy.toFloat() / steps
        
        for (i in 0..steps) {
            val x = (x1 + xStep * i).toInt()
            val y = (y1 + yStep * i).toInt()
            guiGraphics.fill(x - 1, y - 1, x + 1, y + 1, color)
        }
    }
    
    private fun renderQuestNode(guiGraphics: GuiGraphics, node: QuestNode, x: Int, y: Int) {
        val left = x - NODE_SIZE / 2
        val top = y - NODE_SIZE / 2
        val right = x + NODE_SIZE / 2
        val bottom = y + NODE_SIZE / 2
        
        // Background color based on quest status
        val backgroundColor = when (node.status) {
            QuestNode.QuestStatus.COMPLETED -> 0xFF228B22.toInt() // Forest Green
            QuestNode.QuestStatus.READY_TO_COMPLETE -> 0xFF32CD32.toInt() // Lime Green
            QuestNode.QuestStatus.ACCEPTED -> 0xFF4169E1.toInt()  // Royal Blue
            QuestNode.QuestStatus.AVAILABLE -> 0xFFFFD700.toInt() // Gold
            QuestNode.QuestStatus.LOCKED -> 0xFF696969.toInt()    // Dim Gray
        }
        
        // Draw node background
        guiGraphics.fill(left, top, right, bottom, backgroundColor)
        
        // Draw border
        val borderColor = if (node == hoveredNode) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
        guiGraphics.fill(left - 1, top - 1, right + 1, top, borderColor) // Top
        guiGraphics.fill(left - 1, bottom, right + 1, bottom + 1, borderColor) // Bottom
        guiGraphics.fill(left - 1, top, left, bottom, borderColor) // Left
        guiGraphics.fill(right, top, right + 1, bottom, borderColor) // Right
        
        // Draw quest icon
        val iconItem = getQuestIcon(node.questDefinition.icon)
        if (iconItem != null) {
            guiGraphics.renderItem(iconItem, left + 8, top + 8)
        }
        
        // Draw status indicator (small circle in corner)
        val indicatorSize = 6
        val indicatorX = right - indicatorSize
        val indicatorY = top
        val indicatorColor = when (node.status) {
            QuestNode.QuestStatus.COMPLETED -> 0xFF00FF00.toInt() // Bright Green
            QuestNode.QuestStatus.READY_TO_COMPLETE -> 0xFF00FF80.toInt() // Bright Lime Green
            QuestNode.QuestStatus.ACCEPTED -> 0xFF0080FF.toInt()  // Bright Blue
            QuestNode.QuestStatus.AVAILABLE -> 0xFFFFFF00.toInt() // Bright Yellow
            QuestNode.QuestStatus.LOCKED -> 0xFFFF0000.toInt()    // Red
        }
        guiGraphics.fill(indicatorX, indicatorY, indicatorX + indicatorSize, indicatorY + indicatorSize, indicatorColor)
    }
    
    private fun getQuestIcon(iconId: String?): ItemStack? {
        if (iconId.isNullOrEmpty()) return ItemStack(Items.BOOK) // Default icon
        
        val resourceLocation = ResourceLocation.tryParse(iconId) ?: return ItemStack(Items.BOOK)
        val item = BuiltInRegistries.ITEM.get(resourceLocation)
        return if (item != Items.AIR) ItemStack(item) else ItemStack(Items.BOOK)
    }
    
    private fun renderTooltip(guiGraphics: GuiGraphics, node: QuestNode, mouseX: Int, mouseY: Int) {
        val tooltip = mutableListOf<Component>()
        
        // Quest title
        tooltip.add(Component.literal(node.questDefinition.title))
        
        // Quest status
        val statusText = when (node.status) {
            QuestNode.QuestStatus.COMPLETED -> "Completed"
            QuestNode.QuestStatus.READY_TO_COMPLETE -> "Ready to Complete - Right-click to deliver"
            QuestNode.QuestStatus.ACCEPTED -> "In Progress"
            QuestNode.QuestStatus.AVAILABLE -> "Available - Left-click to accept"
            QuestNode.QuestStatus.LOCKED -> "Locked"
        }
        tooltip.add(Component.literal("Status: $statusText"))
        
        // Quest description
        tooltip.add(Component.literal(node.questDefinition.description))
        
        // Dependencies
        if (node.dependencies.isNotEmpty()) {
            tooltip.add(Component.literal("Requires:"))
            node.dependencies.forEach { depId ->
                tooltip.add(Component.literal("  - $depId"))
            }
        }
        
        // Objectives
        if (node.questDefinition.objectives.isNotEmpty()) {
            tooltip.add(Component.literal("Objectives:"))
            node.questDefinition.objectives.forEach { objective ->
                tooltip.add(Component.literal("  - ${objective.description()}"))
            }
        }
        
        guiGraphics.renderTooltip(font, tooltip.map { it.visualOrderText }, mouseX, mouseY)
    }
    
    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) { // Left click
            hoveredNode?.let { node ->
                // Always try to accept quest - let server validate
                val player = minecraft?.player
                if (player != null) {
                    // Send quest accept command
                    minecraft?.player?.connection?.sendCommand("quest accept ${node.questDefinition.id}")
                    // Refresh the screen after a brief delay to allow command processing
                    // Schedule refresh for next tick
                    minecraft?.tell {
                        init()
                    }
                }
                return true
            }
            
            // Start dragging
            isDragging = true
            lastMouseX = mouseX
            lastMouseY = mouseY
            return true
        } else if (button == 1) { // Right click
            hoveredNode?.let { node ->
                // Always try to complete quest - let server validate and provide feedback
                val player = minecraft?.player
                if (player != null) {
                    // Send quest complete command
                    minecraft?.player?.connection?.sendCommand("quest complete ${node.questDefinition.id}")
                    // Refresh the screen after a brief delay to allow command processing
                    // Schedule refresh for next tick
                    minecraft?.tell {
                        init()
                    }
                }
                return true
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button)
    }
    
    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button == 0) {
            isDragging = false
            return true
        }
        return super.mouseReleased(mouseX, mouseY, button)
    }
    
    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (isDragging && button == 0) {
            cameraX += deltaX
            cameraY += deltaY
            return true
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }
    
    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        // Zoom functionality could be added here
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }
    
    override fun isPauseScreen(): Boolean = false
}