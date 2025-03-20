package gg.wildblood.gearsofdiscovery.content.items

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.content.ModSounds
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class Otomaton(private val props: Properties) : Item(props) {
    private val OTOMATON_TONES = listOf(
        ModSounds.OTOMATON_NOTE_1,
        ModSounds.OTOMATON_NOTE_2,
        ModSounds.OTOMATON_NOTE_3,
        ModSounds.OTOMATON_NOTE_4,
        ModSounds.OTOMATON_NOTE_5,
        ModSounds.OTOMATON_NOTE_6,
    )

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val interactionItem = player.getItemInHand(usedHand)
        if(interactionItem.item is Otomaton) {
            when(player.isLocalPlayer) {
                false -> level.playLocalSound(player.blockPosBelowThatAffectsMyMovement, getTone(), SoundSource.PLAYERS, 1.0f, 1.0f,false)
                true -> level.playSound(player, player.blockPosBelowThatAffectsMyMovement, getTone(), SoundSource.PLAYERS, 1.0f, 1.0f)
            }
        }
        player.cooldowns.addCooldown(this, 5)
        return InteractionResultHolder.success(player.getItemInHand(usedHand))
    }

    private fun getTone(): SoundEvent {
        return OTOMATON_TONES.random().get()
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(Component.translatable("item.${GearsOfDiscoveryMod.MODID}.otomaton.tooltip"))
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}