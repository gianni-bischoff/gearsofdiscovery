package gg.wildblood.gearsofdiscovery.content.registry.quests

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gg.wildblood.gearsofdiscovery.utility.extensions.ItemUtilities
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

data class ItemTarget(
    val itemId: String? = null,
    val itemTag: String? = null
) {
    init {
        require((itemId != null) xor (itemTag != null)) {
            "ItemTarget must specify exactly one of itemId or itemTag, not both or neither"
        }
    }

    companion object {
        val CODEC: Codec<ItemTarget> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<ItemTarget> ->
            instance.group(
                Codec.STRING.optionalFieldOf("itemId").forGetter { java.util.Optional.ofNullable(it.itemId) },
                Codec.STRING.optionalFieldOf("itemTag").forGetter { java.util.Optional.ofNullable(it.itemTag) }
            ).apply(instance) { itemId, itemTag ->
                ItemTarget(itemId.orElse(null), itemTag.orElse(null))
            }
        }
    }

    /**
     * Gets the specific item if this target specifies an itemId
     */
    fun getItem(): Item? {
        return itemId?.let { ItemUtilities.getItemById(it) }
    }

    /**
     * Gets the item tag if this target specifies an itemTag
     */
    fun getItemTag(): TagKey<Item>? {
        return itemTag?.let { 
            TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse(it))
        }
    }

    /**
     * Checks if the given item matches this target (either by ID or tag)
     */
    fun matches(item: Item): Boolean {
        return when {
            itemId != null -> {
                val targetItem = getItem()
                targetItem != null && item == targetItem
            }
            itemTag != null -> {
                val tag = getItemTag()
                tag != null && item.builtInRegistryHolder().`is`(tag)
            }
            else -> false
        }
    }

    /**
     * Checks if the given item stack matches this target
     */
    fun matches(itemStack: ItemStack): Boolean {
        return matches(itemStack.item)
    }

    /**
     * Gets all items that match this target
     */
    fun getMatchingItems(): List<Item> {
        return when {
            itemId != null -> {
                val item = getItem()
                if (item != null) listOf(item) else emptyList()
            }
            itemTag != null -> {
                val tag = getItemTag()
                if (tag != null) {
                    BuiltInRegistries.ITEM.getTag(tag).map { holders ->
                        holders.map { it.value() }
                    }.orElse(emptyList())
                } else {
                    emptyList()
                }
            }
            else -> emptyList()
        }
    }

    /**
     * Returns a human-readable description of this target
     */
    fun getDescription(): String {
        return when {
            itemId != null -> itemId
            itemTag != null -> "#$itemTag"
            else -> "invalid target"
        }
    }
}