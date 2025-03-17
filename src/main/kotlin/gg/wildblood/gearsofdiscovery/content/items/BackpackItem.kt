package gg.wildblood.gearsofdiscovery.content.items

import gg.wildblood.gearsofdiscovery.content.items.components.BackpackContent
import gg.wildblood.gearsofdiscovery.content.items.components.ModItemDataComponents
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.stats.Stats
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.BundleItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import org.apache.commons.lang3.math.Fraction
import top.theillusivec4.curios.api.type.capability.ICurioItem
import java.util.*
import java.util.function.Consumer
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class BackpackItem(properties: Properties) : BundleItem(properties), ICurioItem {
    companion object {
        val BAR_COLOR: Int = Mth.color(0.4f, 0.4f, 1.0f)
        const val TOOLTIP_MAX_WEIGHT: Int = 64

        fun getFullnessDisplay(stack: ItemStack): Float {
            val backpackContent = stack.getOrDefault(ModItemDataComponents.BACKPACK_COMPONENT, BackpackContent.EMPTY)
            return backpackContent.weight().toFloat()
        }
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (stack.count != 1 || action != ClickAction.SECONDARY) {
            return false
        } else {
            val backpackContent = stack.get(ModItemDataComponents.BACKPACK_COMPONENT)
            if (backpackContent == null) {
                return false
            } else {
                val itemStack = slot.item
                val mutableContent = BackpackContent.Mutable(backpackContent)
                if (itemStack.isEmpty) {
                    this.playRemoveOneSound(player)
                    val stackedItemStack = mutableContent.removeOne()
                    if (stackedItemStack != null) {
                        val itemstack2 = slot.safeInsert(stackedItemStack)
                        mutableContent.tryInsert(itemstack2)
                    }
                } else if (itemStack.item.canFitInsideContainerItems()) {
                    val i = mutableContent.tryTransfer(slot, player)
                    if (i > 0) {
                        this.playInsertSound(player)
                    }
                }

                stack.set(ModItemDataComponents.BACKPACK_COMPONENT, mutableContent.toImmutable())
                return true
            }
        }
    }

    override fun overrideOtherStackedOnMe(
        stack: ItemStack, other: ItemStack, slot: Slot, action: ClickAction, player: Player, access: SlotAccess
    ): Boolean {
        if (stack.count != 1) return false
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            val backpackContent = stack.get(ModItemDataComponents.BACKPACK_COMPONENT)
            if (backpackContent == null) {
                return false
            } else {
                val mutableContent = BackpackContent.Mutable(backpackContent)
                if (other.isEmpty) {
                    val itemstack = mutableContent.removeOne()
                    if (itemstack != null) {
                        this.playRemoveOneSound(player)
                        access.set(itemstack)
                    }
                } else {
                    val i = mutableContent.tryInsert(other)
                    if (i > 0) {
                        this.playInsertSound(player)
                    }
                }

                stack.set(ModItemDataComponents.BACKPACK_COMPONENT, mutableContent.toImmutable())
                return true
            }
        } else {
            return false
        }
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see [net.minecraft.world.item.Item.useOn].
     */
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)
        if (dropContents(itemStack, player)) {
            this.playDropContentsSound(player)
            player.awardStat(Stats.ITEM_USED[this])
            return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide())
        } else {
            return InteractionResultHolder.fail(itemStack)
        }
    }

    override fun isBarVisible(stack: ItemStack): Boolean {
        val backpackContent = stack.getOrDefault(ModItemDataComponents.BACKPACK_COMPONENT, BackpackContent.EMPTY)
        return backpackContent.weight() > Fraction.ZERO
    }

    override fun getBarWidth(stack: ItemStack): Int {
        val backpackContent = stack.getOrDefault(ModItemDataComponents.BACKPACK_COMPONENT, BackpackContent.EMPTY)
        return min((1 + Mth.mulAndTruncate(backpackContent.weight(), 12)).toDouble(), 13.0).toInt()
    }

    override fun getBarColor(stack: ItemStack): Int {
        return BAR_COLOR
    }

    private fun dropContents(stack: ItemStack, player: Player): Boolean {
        val backpackContent = stack.get(ModItemDataComponents.BACKPACK_COMPONENT)
        if (backpackContent != null && !backpackContent.isEmpty()) {
            stack.set(ModItemDataComponents.BACKPACK_COMPONENT, BackpackContent.EMPTY)
            if (player is ServerPlayer) {
                backpackContent.itemsCopy().forEach(Consumer { itemStack: ItemStack -> player.drop(itemStack, true) })
            }

            return true
        } else {
            return false
        }
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        return if (!stack.has(DataComponents.HIDE_TOOLTIP) && !stack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP))
            Optional.ofNullable<BackpackContent>(stack.get(ModItemDataComponents.BACKPACK_COMPONENT))
                .map(::BackpackTooltip)
        else
            Optional.empty<TooltipComponent>()
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val backpackContent = stack.get(ModItemDataComponents.BACKPACK_COMPONENT)
        if (backpackContent != null) {
            val i = Mth.mulAndTruncate(backpackContent.weight(), 64)
            tooltipComponents.add(
                Component.translatable("item.minecraft.bundle.fullness", i, 64).withStyle(ChatFormatting.GRAY)
            )
        }
    }

    @Deprecated("Deprecated through Parent")
    override fun onDestroyed(itemEntity: ItemEntity) {
        val backpackContent = itemEntity.item.get(ModItemDataComponents.BACKPACK_COMPONENT)
        if (backpackContent != null) {
            itemEntity.item.set(ModItemDataComponents.BACKPACK_COMPONENT, BackpackContent.EMPTY)
            ItemUtils.onContainerDestroyed(itemEntity, backpackContent.itemsCopy())
        }
    }

    private fun playRemoveOneSound(entity: Entity) =
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8f, 0.8f + entity.level().getRandom().nextFloat() * 0.4f)


    private fun playInsertSound(entity: Entity) =
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8f, 0.8f + entity.level().getRandom().nextFloat() * 0.4f)


    private fun playDropContentsSound(entity: Entity) =
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8f, 0.8f + entity.level().getRandom().nextFloat() * 0.4f)
}

data class BackpackTooltip(private val contents: BackpackContent) : TooltipComponent {
    fun contents() : BackpackContent {
        return this.contents
    }
}

@OnlyIn(Dist.CLIENT)
class ClientBackpackTooltipComponent(private val contents: BackpackContent) : ClientTooltipComponent {
    companion object {
        val BACKGROUND_SPRITE: ResourceLocation = ResourceLocation.withDefaultNamespace("container/bundle/background")
        const val MARGIN_Y: Int = 4
        const val BORDER_WIDTH: Int = 1
        const val SLOT_SIZE_X: Int = 18
        const val SLOT_SIZE_Y: Int = 20

        @OnlyIn(Dist.CLIENT)
        enum class Texture(val sprite: ResourceLocation, val w: Int, val h: Int) {
            BLOCKED_SLOT(ResourceLocation.withDefaultNamespace("container/bundle/blocked_slot"), 18, 20),
            SLOT(ResourceLocation.withDefaultNamespace("container/bundle/slot"), 18, 20)
        }
    }

    override fun getHeight(): Int {
        return this.backgroundHeight() + 4
    }

    override fun getWidth(font: Font): Int {
        return this.backgroundWidth()
    }

    private fun backgroundWidth(): Int {
        return this.gridSizeX() * 18 + 2
    }

    private fun backgroundHeight(): Int {
        return this.gridSizeY() * 20 + 2
    }

    override fun renderImage(font: Font, x: Int, y: Int, guiGraphics: GuiGraphics) {
        val i = this.gridSizeX()
        val j = this.gridSizeY()
        guiGraphics.blitSprite(BACKGROUND_SPRITE, x, y, this.backgroundWidth(), this.backgroundHeight())
        val flag = contents.weight() >= Fraction.ONE
        var k = 0

        for (l in 0..<j) {
            for (i1 in 0..<i) {
                val j1 = x + i1 * 18 + 1
                val k1 = y + l * 20 + 1
                this.renderSlot(j1, k1, k++, flag, guiGraphics, font)
            }
        }
    }

    private fun renderSlot(
        x: Int,
        y: Int,
        itemIndex: Int,
        isBundleFull: Boolean,
        guiGraphics: GuiGraphics,
        font: Font
    ) {
        if (itemIndex >= contents.size()) {
            this.blit(guiGraphics, x, y, if (isBundleFull) Texture.BLOCKED_SLOT else Texture.SLOT)
        } else {
            val itemStack = contents.getItemUnsafe(itemIndex)
            this.blit(guiGraphics, x, y, Texture.SLOT)
            guiGraphics.renderItem(itemStack, x + 1, y + 1, itemIndex)
            guiGraphics.renderItemDecorations(font, itemStack, x + 1, y + 1)
            if (itemIndex == contents.selectedSlot) {
                AbstractContainerScreen.renderSlotHighlight(guiGraphics, x + 1, y + 1, 0)
            }
        }
    }

    private fun blit(guiGraphics: GuiGraphics, x: Int, y: Int, texture: Texture) {
        guiGraphics.blitSprite(texture.sprite, x, y, 0, texture.w, texture.h)
    }

    private fun gridSizeX(): Int {
        return max(2.0, ceil(sqrt(contents.size().toDouble() + 1.0)).toInt().toDouble()).toInt()
    }

    private fun gridSizeY(): Int {
        return ceil((contents.size().toDouble() + 1.0) / gridSizeX().toDouble()).toInt()
    }

}