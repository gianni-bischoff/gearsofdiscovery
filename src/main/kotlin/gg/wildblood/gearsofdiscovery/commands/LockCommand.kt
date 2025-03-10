package gg.wildblood.gearsofdiscovery.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import gg.wildblood.gearsofdiscovery.locks.tryGetLockRegistry
import gg.wildblood.gearsofdiscovery.network.ModClientPayloadHandler
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style


object LockCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("lock")
                .requires { p -> p.hasPermission(4) }
                .then(Commands.literal("list").executes { context -> listLocks(context); 0 })
                .then(Commands.literal("toggle")
                        .then(Commands.argument("lock", StringArgumentType.word())
                            .executes { context -> toggleLock(context); 0 }
                        )
                )
        )
    }

    private fun listLocks(context: CommandContext<CommandSourceStack>) {
        val lockRegistry = Minecraft.getInstance().tryGetLockRegistry() ?: return

        val messageText = Component.literal("Loaded Locks:")

        lockRegistry.forEach {
            messageText.append(
                Component.literal("\n- [${if(it.enabled) "✔" else "X"}] ${it.name}")
                    .withStyle(if(it.enabled) ChatFormatting.GREEN else ChatFormatting.RED)
                    .withStyle(Style.EMPTY.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lock toggle ${it.name}")))
            )
        }

        context.source.sendSuccess({ messageText },false)
    }

    private fun toggleLock(context: CommandContext<CommandSourceStack>) {
        val lockName = context.getArgument("lock", String::class.java)

        val lockRegistry = Minecraft.getInstance().tryGetLockRegistry() ?: return

        val locks = lockRegistry.filter { it.name == lockName }

        if (locks.isEmpty()) {
            context.source.sendFailure(Component.literal("Lock $lockName not found!").withStyle(ChatFormatting.RED))
            return
        } else if (locks.size > 1) {
            context.source.sendFailure(Component.literal("Multiple Locks with name $lockName found! ( i guess someone did a little fucky... )").withStyle(ChatFormatting.RED))
        }

        val lock = locks.first()

        if (lock.enabled) {
            ModClientPayloadHandler.serverAddUnlock(context.source.playerOrException, lock.name)
        } else {
            ModClientPayloadHandler.serverRemoveUnlock(context.source.playerOrException, lock.name)
        }

        context.source.sendSuccess({ Component.literal("Lock ${lock.name} ${if(lock.enabled) "disabled" else "enabled"}!") },false)

    }
}

