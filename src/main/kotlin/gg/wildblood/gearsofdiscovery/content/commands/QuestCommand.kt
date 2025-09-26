package gg.wildblood.gearsofdiscovery.content.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import gg.wildblood.gearsofdiscovery.utility.extensions.tryGetQuestRegistry
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

object QuestCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("quest")
                .executes { context -> listQuests(context); 0}
                .requires { p -> p.hasPermission(4) }
                .then(Commands.literal("list").executes { context -> listQuests(context); 0 })
                .then(Commands.literal("detail")
                    .then(Commands.argument("quest", StringArgumentType.word())
                        .executes { context -> questDetail(context); 0 }
                    )
                )
                .then(Commands.literal("testcompletion")
                    .then(Commands.argument("quest", StringArgumentType.word())
                        .executes { context -> testQuestCompletion(context); 0 }
                    )
                )
                .then(Commands.literal("testrewards")
                    .then(Commands.argument("quest", StringArgumentType.word())
                        .executes { context -> testQuestRewards(context); 0 }
                    )
                )
        )
    }

    private fun listQuests(context: CommandContext<CommandSourceStack>) {
        val questRegistry = Minecraft.getInstance().tryGetQuestRegistry() ?: return

        val messageText = Component.literal("Loaded Quests:")

        questRegistry.forEach { quest ->
            messageText.append(
                Component.literal("\n- ${quest.title} (${quest.id})")
                    .withStyle(ChatFormatting.GREEN)
                    .withStyle(Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quest detail ${quest.id}")))
            )
        }

        context.source.sendSuccess({ messageText }, false)
    }

    private fun questDetail(context: CommandContext<CommandSourceStack>) {
        val questId = context.getArgument("quest", String::class.java)

        val questRegistry = Minecraft.getInstance().tryGetQuestRegistry() ?: return

        val quest = questRegistry.firstOrNull { it.id == questId }

        if (quest == null) {
            context.source.sendFailure(Component.literal("Quest $questId not found!").withStyle(ChatFormatting.RED))
            return
        }

        val message = Component.literal("").withStyle(ChatFormatting.GRAY)
            .append(Component.literal("\nQuest Info - ${quest.title}\n").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.WHITE))
            .append("\nID: ${quest.id}")
            .append("\nType: ${quest.type}")
            .append("\nDescription: ${quest.description}")

        // Show requirements
        if (quest.requirements.isNotEmpty()) {
            message.append(Component.literal("\n\nRequirements:").withStyle(ChatFormatting.YELLOW))
            quest.requirements.forEach { requirement ->
                message.append(Component.literal("\n─ $requirement"))
            }
        }

        // Show objectives
        if (quest.objectives.isNotEmpty()) {
            message.append(Component.literal("\n\nObjectives:").withStyle(ChatFormatting.AQUA))
            quest.objectives.forEach { objective ->
                message.append(Component.literal("\n─ ${objective.description()}"))
            }
        }

        // Show rewards
        if (quest.rewards.isNotEmpty()) {
            message.append(Component.literal("\n\nRewards:").withStyle(ChatFormatting.GOLD))
            quest.rewards.forEach { reward ->
                message.append(Component.literal("\n─ ${reward.description()}"))
            }
        }

        // Show meta information
        if (quest.meta.isNotEmpty()) {
            message.append(Component.literal("\n\nMeta:").withStyle(ChatFormatting.DARK_GRAY))
            quest.meta.forEach { (key, value) ->
                message.append(Component.literal("\n─ $key: $value"))
            }
        }

        context.source.sendSuccess({ message }, false)
    }

    private fun testQuestCompletion(context: CommandContext<CommandSourceStack>) {
        val questId = context.getArgument("quest", String::class.java)
        val player = context.source.playerOrException

        val questRegistry = Minecraft.getInstance().tryGetQuestRegistry() ?: return

        val quest = questRegistry.firstOrNull { it.id == questId }

        if (quest == null) {
            context.source.sendFailure(Component.literal("Quest $questId not found!").withStyle(ChatFormatting.RED))
            return
        }

        val message = Component.literal("").withStyle(ChatFormatting.GRAY)
            .append(Component.literal("\nQuest Completion Test - ${quest.title}\n").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.WHITE))

        // Test each objective
        if (quest.objectives.isNotEmpty()) {
            message.append(Component.literal("\nObjective Status:").withStyle(ChatFormatting.AQUA))
            var allObjectivesComplete = true

            quest.objectives.forEachIndexed { index, objective ->
                val canComplete = objective.check(player)
                val statusText = if (canComplete) {
                    " ✓"
                } else {
                    allObjectivesComplete = false
                    " ✗"
                }
                val statusColor = if (canComplete) ChatFormatting.GREEN else ChatFormatting.RED
                
                message.append(Component.literal("\n─ ${objective.description()}$statusText").withStyle(statusColor))
            }

            // Overall completion status
            message.append(Component.literal("\n\nOverall Status: ").withStyle(ChatFormatting.WHITE))
            if (allObjectivesComplete) {
                message.append(Component.literal("READY TO COMPLETE").withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD))
            } else {
                message.append(Component.literal("CANNOT COMPLETE").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD))
            }
        } else {
            message.append(Component.literal("\nNo objectives to test.").withStyle(ChatFormatting.YELLOW))
        }

        context.source.sendSuccess({ message }, false)
    }

    private fun testQuestRewards(context: CommandContext<CommandSourceStack>) {
        val questId = context.getArgument("quest", String::class.java)
        val player = context.source.playerOrException

        val questRegistry = Minecraft.getInstance().tryGetQuestRegistry() ?: return

        val quest = questRegistry.firstOrNull { it.id == questId }

        if (quest == null) {
            context.source.sendFailure(Component.literal("Quest $questId not found!").withStyle(ChatFormatting.RED))
            return
        }

        val message = Component.literal("").withStyle(ChatFormatting.GRAY)
            .append(Component.literal("\nQuest Rewards Test - ${quest.title}\n").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE).withStyle(ChatFormatting.WHITE))

        // Test each reward
        if (quest.rewards.isNotEmpty()) {
            message.append(Component.literal("\nTesting Rewards:").withStyle(ChatFormatting.GOLD))
            var allRewardsValid = true
            var rewardsGranted = 0

            quest.rewards.forEachIndexed { index, reward ->
                try {
                    // Create a test copy of player inventory state before testing
                    val testResult = reward.execute(player)
                    val statusText = if (testResult) {
                        rewardsGranted++
                        " ✓ GRANTED"
                    } else {
                        allRewardsValid = false
                        " ✗ FAILED"
                    }
                    val statusColor = if (testResult) ChatFormatting.GREEN else ChatFormatting.RED
                    
                    message.append(Component.literal("\n─ ${reward.description()}$statusText").withStyle(statusColor))
                } catch (e: Exception) {
                    message.append(Component.literal("\n─ ${reward.description()} ✗ ERROR: ${e.message}").withStyle(ChatFormatting.DARK_RED))
                    allRewardsValid = false
                }
            }

            // Overall reward status
            message.append(Component.literal("\n\nReward Summary: ").withStyle(ChatFormatting.WHITE))
            message.append(Component.literal("$rewardsGranted/${quest.rewards.size} rewards granted successfully").withStyle(
                if (allRewardsValid) ChatFormatting.GREEN else ChatFormatting.YELLOW
            ))

            if (rewardsGranted > 0) {
                message.append(Component.literal("\n\nNote: Rewards have been actually granted to your inventory!").withStyle(ChatFormatting.LIGHT_PURPLE).withStyle(ChatFormatting.ITALIC))
            }
        } else {
            message.append(Component.literal("\nNo rewards to test.").withStyle(ChatFormatting.YELLOW))
        }

        context.source.sendSuccess({ message }, false)
    }
}