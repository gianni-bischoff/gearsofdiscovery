package gg.wildblood.gearsofdiscovery.content.items.components

import com.google.common.collect.Lists
import com.mojang.serialization.Codec
import net.minecraft.core.component.DataComponents
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.apache.commons.lang3.math.Fraction
import java.util.stream.Stream
import kotlin.math.max
import kotlin.math.min


class BackpackContent(var items: List<ItemStack>, var weight: Fraction, var selectedSlot: Int = 0) : TooltipComponent {
    companion object {
        val EMPTY: BackpackContent = BackpackContent(listOf())
        private val BACKPACK_IN_BUNDLE_WEIGHT: Fraction = Fraction.getFraction(1, 16)
        private val STONE_IN_BUNDLE_WEIGHT: Fraction = Fraction.getFraction(1, 128)
        val NO_STACK_INDEX: Int = -1

        val CODEC: Codec<BackpackContent> = ItemStack.CODEC.listOf().xmap(
            { items: List<ItemStack> -> BackpackContent(items) },
            { backpackContent: BackpackContent -> backpackContent.items }
        )

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BackpackContent> = ItemStack.STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(
                { items: List<ItemStack> -> BackpackContent(items) },
                { backpackContent: BackpackContent -> backpackContent.items }
            )

        fun computeContentWeight(content: List<ItemStack>): Fraction {
            var fraction = Fraction.ZERO

            for (itemstack in content) {
                fraction = fraction.add(getWeight(itemstack).multiplyBy(Fraction.getFraction(itemstack.count, 1)))
            }

            return fraction
        }

        fun getWeight(stack: ItemStack): Fraction {
            val backpackContent = stack.get(ModItemDataComponents.BACKPACK_COMPONENT)
            if (backpackContent != null) {
                return BACKPACK_IN_BUNDLE_WEIGHT.add(backpackContent.weight())
            } else if(stack.item == Items.COBBLESTONE) {
                return STONE_IN_BUNDLE_WEIGHT
            } else {
                val list = stack.getOrDefault(DataComponents.BEES, listOf())
                return if (list.isNotEmpty()) Fraction.ONE else Fraction.getFraction(1, stack.maxStackSize)
            }
        }
    }

    constructor(items: List<ItemStack>) : this(items, computeContentWeight(items))

    fun getItemUnsafe(index: Int): ItemStack {
        return items[index]
    }

    fun itemCopyStream(): Stream<ItemStack> {
        return items.stream().map { obj: ItemStack -> obj.copy() }
    }

    fun items(): Iterable<ItemStack> {
        return this.items
    }

    fun itemsCopy(): Iterable<ItemStack> {
        return Lists.transform(
            this.items
        ) { obj: ItemStack -> obj.copy() }
    }

    fun size(): Int {
        return items.size
    }

    fun weight(): Fraction {
        return this.weight
    }

    fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else {
            if (other !is BackpackContent)
                false
            else
                this.weight == other.weight && ItemStack.listMatches(this.items, other.items)
        }
    }

    override fun hashCode(): Int {
        return ItemStack.hashStackList(this.items)
    }

    override fun toString(): String {
        return "BackpackContent" + this.items
    }

    class Mutable(contents: BackpackContent) {
        private var items: MutableList<ItemStack> = ArrayList(contents.items)
        private var weight: Fraction = contents.weight

        fun clearItems(): BackpackContent.Mutable {
            items.clear()
            this.weight = Fraction.ZERO
            return this
        }

        private fun findStackIndex(stack: ItemStack): Int {
            if (!stack.isStackable) {
                return -1
            } else {
                for (i in items.indices) {
                    if (ItemStack.isSameItemSameComponents(items[i], stack)) {
                        return i
                    }
                }

                return -1
            }
        }

        private fun getMaxAmountToAdd(stack: ItemStack): Int {
            val fraction = Fraction.ONE.subtract(this.weight)
            return max(fraction.divideBy(getWeight(stack)).toInt().toDouble(), 0.0).toInt()
        }

        fun tryInsert(stack: ItemStack): Int {
            if (!stack.isEmpty && stack.item.canFitInsideContainerItems()) {
                val i = min(stack.count.toDouble(), getMaxAmountToAdd(stack).toDouble()).toInt()
                if (i == 0) {
                    return 0
                } else {
                    this.weight = weight.add(getWeight(stack).multiplyBy(Fraction.getFraction(i, 1)))
                    val j = this.findStackIndex(stack)
                    if (j != -1) {
                        val itemstack = items.removeAt(j)
                        val itemstack1 = itemstack.copyWithCount(itemstack.count + i)
                        stack.shrink(i)
                        items.add(0, itemstack1)
                    } else {
                        items.add(0, stack.split(i))
                    }

                    return i
                }
            } else {
                return 0
            }
        }

        fun tryTransfer(slot: Slot, player: Player): Int {
            val itemstack = slot.item
            val i = this.getMaxAmountToAdd(itemstack)
            return this.tryInsert(slot.safeTake(itemstack.count, i, player))
        }

        fun removeOne(): ItemStack? {
            if (items.isEmpty()) {
                return null
            } else {
                val itemstack = items.removeAt(0).copy()
                this.weight = weight.subtract(
                    getWeight(itemstack).multiplyBy(Fraction.getFraction(itemstack.count, 1))
                )
                return itemstack
            }
        }

        fun weight(): Fraction {
            return this.weight
        }

        fun toImmutable(): BackpackContent {
            return BackpackContent(java.util.List.copyOf(this.items), this.weight)
        }
    }
}