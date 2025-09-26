package gg.wildblood.gearsofdiscovery.content.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import gg.wildblood.gearsofdiscovery.content.registry.quests.QuestManager
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import java.text.SimpleDateFormat
import java.util.*

object QuestCommand {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("quest")
                .executes { context -> showQuestMenu(context); 0 }
                .then(Commands.literal("list")
                    .executes { context -> listAvailableQuests(context); 0 }
                )
                .then(Commands.literal("accept")
                    .then(Commands.argument("quest", StringArgumentType.word())
                        .executes { context -> acceptQuest(context); 0 }
                    )
                )
                .then(Commands.literal("complete")
                    .then(Commands.argument("quest", StringArgumentType.word())
                        .executes { context -> completeQuest(context); 0 }
                    )
                )
                .then(Commands.literal("progress")
                    .executes { context -> showAcceptedQuests(context); 0 }
                    .then(Commands.argument("quest", StringArgumentType.word())
                        .executes { context -> showQuestProgress(context); 0 }
                    )
                )
                .then(Commands.literal("completed")
                    .executes { context -> showCompletedQuests(context); 0 }
                )
        )
    }
    
    private fun showQuestMenu(context: CommandContext<CommandSourceStack>) {
        val player = context.source.playerOrException
        
        val message = Component.literal("")
            .append(Component.literal("╔═══════════════════════════════════════╗\n").withStyle(ChatFormatting.GOLD))
            .append(Component.literal("║              QUEST SYSTEM             ║\n").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
            .append(Component.literal("╠═══════════════════════════════════════╣\n").withStyle(ChatFormatting.GOLD))
            .append(Component.literal("║ Available Commands:                   ║\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("║                                       ║\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("║ ").withStyle(ChatFormatting.WHITE))
            .append(
                Component.literal("/quest list").withStyle(ChatFormatting.GREEN)
                    .withStyle(Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quest list")))
            )
            .append(Component.literal(" - Show available quests ║\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("║ ").withStyle(ChatFormatting.WHITE))
            .append(
                Component.literal("/quest progress").withStyle(ChatFormatting.AQUA)
                    .withStyle(Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quest progress")))
            )
            .append(Component.literal(" - Show accepted quests║\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("║ ").withStyle(ChatFormatting.WHITE))
            .append(
                Component.literal("/quest complete").withStyle(ChatFormatting.GOLD)
                    .withStyle(Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quest complete ")))
            )
            .append(Component.literal(" - Hand in completed quest║\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("║ ").withStyle(ChatFormatting.WHITE))
            .append(
                Component.literal("/quest completed").withStyle(ChatFormatting.YELLOW)
                    .withStyle(Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quest completed")))
            )
            .append(Component.literal(" - Show completed quests║\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("║                                       ║\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("╚═══════════════════════════════════════╝").withStyle(ChatFormatting.GOLD))
        
        context.source.sendSuccess({ message }, false)
    }
    
    private fun listAvailableQuests(context: CommandContext<CommandSourceStack>) {
        val player = context.source.playerOrException
        val acceptableQuests = QuestManager.getAcceptableQuests(player)
        
        if (acceptableQuests.isEmpty()) {
            val message = Component.literal("No quests available for acceptance at this time.")
                .withStyle(ChatFormatting.YELLOW)
            context.source.sendSuccess({ message }, false)
            return
        }
        
        val message = Component.literal("")
            .append(Component.literal("Available Quests (${acceptableQuests.size}):\n\n").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
        
        acceptableQuests.forEach { quest ->
            val questComponent = Component.literal("${quest.title}\n")
                .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                .append(Component.literal("  ${quest.description}\n").withStyle(ChatFormatting.WHITE))
                .append(Component.literal("  Type: ${quest.type.displayName}\n").withStyle(ChatFormatting.GRAY))
            
            // Show requirements if any
            if (quest.requirements.isNotEmpty()) {
                questComponent.append(Component.literal("  Requirements: ${quest.requirements.joinToString(", ")}\n").withStyle(ChatFormatting.DARK_GRAY))
            }
            
            // Show objectives
            if (quest.objectives.isNotEmpty()) {
                questComponent.append(Component.literal("  Objectives:\n").withStyle(ChatFormatting.AQUA))
                quest.objectives.forEach { objective ->
                    questComponent.append(Component.literal("    • ${objective.description()}\n").withStyle(ChatFormatting.AQUA))
                }
            }
            
            // Show rewards
            if (quest.rewards.isNotEmpty()) {
                questComponent.append(Component.literal("  Rewards:\n").withStyle(ChatFormatting.GOLD))
                quest.rewards.forEach { reward ->
                    questComponent.append(Component.literal("    • ${reward.description()}\n").withStyle(ChatFormatting.YELLOW))
                }
            }
            
            // Accept button
            questComponent.append(
                Component.literal("  [ACCEPT QUEST]").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                    .withStyle(Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quest accept ${quest.id}")))
            ).append(Component.literal("\n\n"))
            
            message.append(questComponent)
        }
        
        context.source.sendSuccess({ message }, false)
    }
    
    private fun acceptQuest(context: CommandContext<CommandSourceStack>) {
        val player = context.source.playerOrException
        val questId = context.getArgument("quest", String::class.java)
        
        val result = QuestManager.acceptQuest(player, questId)
        
        val message = when (result) {
            QuestManager.AcceptResult.SUCCESS -> {
                Component.literal("Successfully accepted quest: $questId!")
                    .withStyle(ChatFormatting.GREEN)
            }
            QuestManager.AcceptResult.QUEST_NOT_FOUND -> {
                Component.literal("Quest '$questId' not found!")
                    .withStyle(ChatFormatting.RED)
            }
            QuestManager.AcceptResult.ALREADY_ACCEPTED -> {
                Component.literal("You have already accepted this quest!")
                    .withStyle(ChatFormatting.YELLOW)
            }
            QuestManager.AcceptResult.ALREADY_COMPLETED -> {
                Component.literal("You have already completed this quest!")
                    .withStyle(ChatFormatting.YELLOW)
            }
            QuestManager.AcceptResult.REQUIREMENTS_NOT_MET -> {
                Component.literal("You don't meet the requirements for this quest!")
                    .withStyle(ChatFormatting.RED)
            }
            QuestManager.AcceptResult.FAILED -> {
                Component.literal("Failed to accept quest!")
                    .withStyle(ChatFormatting.RED)
            }
        }
        
        context.source.sendSuccess({ message }, false)
    }
    
    private fun completeQuest(context: CommandContext<CommandSourceStack>) {
        val player = context.source.playerOrException
        val questId = context.getArgument("quest", String::class.java)
        
        val result = QuestManager.completeQuest(player, questId)
        
        val message = when (result) {
            QuestManager.CompleteResult.SUCCESS -> {
                Component.literal("Successfully completed quest: $questId! Rewards have been given.")
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
            }
            QuestManager.CompleteResult.QUEST_NOT_FOUND -> {
                Component.literal("Quest '$questId' not found!")
                    .withStyle(ChatFormatting.RED)
            }
            QuestManager.CompleteResult.QUEST_NOT_ACCEPTED -> {
                Component.literal("You haven't accepted this quest!")
                    .withStyle(ChatFormatting.RED)
            }
            QuestManager.CompleteResult.OBJECTIVES_NOT_COMPLETE -> {
                Component.literal("You haven't completed all objectives for this quest yet!")
                    .withStyle(ChatFormatting.YELLOW)
            }
            QuestManager.CompleteResult.ALREADY_COMPLETED -> {
                Component.literal("You have already completed this quest!")
                    .withStyle(ChatFormatting.YELLOW)
            }
            QuestManager.CompleteResult.FAILED -> {
                Component.literal("Failed to complete quest!")
                    .withStyle(ChatFormatting.RED)
            }
        }
        
        context.source.sendSuccess({ message }, false)
    }
    
    private fun showAcceptedQuests(context: CommandContext<CommandSourceStack>) {
        val player = context.source.playerOrException
        val acceptedQuests = QuestManager.getAcceptedQuests(player)
        
        if (acceptedQuests.isEmpty()) {
            val message = Component.literal("You have no accepted quests.")
                .withStyle(ChatFormatting.YELLOW)
            context.source.sendSuccess({ message }, false)
            return
        }
        
        val message = Component.literal("")
            .append(Component.literal("Accepted Quests (${acceptedQuests.size}):\n\n").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
        
        acceptedQuests.forEach { (questId, questInstance) ->
            val progressInfo = QuestManager.getQuestProgress(player, questId)
            if (progressInfo != null) {
                val statusColor = if (progressInfo.isComplete) ChatFormatting.GREEN else ChatFormatting.YELLOW
                val statusText = if (progressInfo.isComplete) "READY TO COMPLETE" else "IN PROGRESS"
                
                val questComponent = Component.literal("${progressInfo.questTitle} [$statusText]\n")
                    .withStyle(statusColor, ChatFormatting.BOLD)
                    .append(Component.literal("  ${progressInfo.questDescription}\n").withStyle(ChatFormatting.WHITE))
                    .append(Component.literal("  Accepted: ${dateFormat.format(Date(progressInfo.acceptedTime))}\n").withStyle(ChatFormatting.GRAY))
                
                // Show objective progress
                questComponent.append(Component.literal("  Progress:\n").withStyle(ChatFormatting.AQUA))
                progressInfo.objectives.forEach { objective ->
                    val objStatusColor = if (objective.isComplete) ChatFormatting.GREEN else ChatFormatting.RED
                    val objStatusSymbol = if (objective.isComplete) "✓" else "✗"
                    questComponent.append(Component.literal("    $objStatusSymbol ${objective.description}\n").withStyle(objStatusColor))
                }
                
                // Action buttons
                questComponent.append(
                    Component.literal("  [VIEW DETAILS]").withStyle(ChatFormatting.AQUA)
                        .withStyle(Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quest progress $questId")))
                )
                
                // Add completion button if quest is ready to complete
                if (progressInfo.isComplete) {
                    questComponent.append(Component.literal(" "))
                        .append(
                            Component.literal("[COMPLETE QUEST]").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                                .withStyle(Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quest complete $questId")))
                        )
                }
                
                questComponent.append(Component.literal("\n\n"))
                
                message.append(questComponent)
            }
        }
        
        context.source.sendSuccess({ message }, false)
    }
    
    private fun showQuestProgress(context: CommandContext<CommandSourceStack>) {
        val player = context.source.playerOrException
        val questId = context.getArgument("quest", String::class.java)
        
        val progressInfo = QuestManager.getQuestProgress(player, questId)
        
        if (progressInfo == null) {
            val message = Component.literal("Quest '$questId' not found or not accepted!")
                .withStyle(ChatFormatting.RED)
            context.source.sendSuccess({ message }, false)
            return
        }
        
        val statusColor = if (progressInfo.isComplete) ChatFormatting.GREEN else ChatFormatting.YELLOW
        val statusText = if (progressInfo.isComplete) "COMPLETED" else "IN PROGRESS"
        
        val message = Component.literal("")
            .append(Component.literal("Quest Progress - ${progressInfo.questTitle}\n").withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE))
            .append(Component.literal("Status: [$statusText]\n").withStyle(statusColor, ChatFormatting.BOLD))
            .append(Component.literal("Description: ${progressInfo.questDescription}\n\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("Accepted: ${dateFormat.format(Date(progressInfo.acceptedTime))}\n").withStyle(ChatFormatting.GRAY))
        
        if (progressInfo.completedTime != null) {
            message.append(Component.literal("Completed: ${dateFormat.format(Date(progressInfo.completedTime))}\n").withStyle(ChatFormatting.GRAY))
        }
        
        message.append(Component.literal("\nObjective Progress:\n").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD))
        
        progressInfo.objectives.forEach { objective ->
            val objStatusColor = if (objective.isComplete) ChatFormatting.GREEN else ChatFormatting.RED
            val objStatusSymbol = if (objective.isComplete) "✓" else "✗"
            message.append(Component.literal("  $objStatusSymbol ${objective.description} - ${objective.progress}\n").withStyle(objStatusColor))
        }
        
        context.source.sendSuccess({ message }, false)
    }
    
    private fun showCompletedQuests(context: CommandContext<CommandSourceStack>) {
        val player = context.source.playerOrException
        val completedQuests = QuestManager.getCompletedQuests(player)
        
        if (completedQuests.isEmpty()) {
            val message = Component.literal("You have not completed any quests yet.")
                .withStyle(ChatFormatting.YELLOW)
            context.source.sendSuccess({ message }, false)
            return
        }
        
        val message = Component.literal("")
            .append(Component.literal("Completed Quests (${completedQuests.size}):\n\n").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD))
        
        completedQuests.forEach { questId ->
            message.append(Component.literal("✓ $questId\n").withStyle(ChatFormatting.GREEN))
        }
        
        context.source.sendSuccess({ message }, false)
    }
}