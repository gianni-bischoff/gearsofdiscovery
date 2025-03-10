package gg.wildblood.gearsofdiscovery.locks

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import java.util.stream.Stream

data class Lock(
    val name: String,
    val description: String,
    val actions: Map<Type, List<String>> = mapOf(),
    var enabled: Boolean = true
) {
    companion object {
        val CODEC: Codec<Lock> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<Lock> ->
            instance.group(
                Codec.STRING.fieldOf("name").forGetter(Lock::name),
                Codec.STRING.fieldOf("description").forGetter(Lock::description),
                Codec.unboundedMap(Type.CODEC, Codec.list(Codec.STRING)).fieldOf("locked_things").forGetter(Lock::actions),
            ).apply(instance, ::Lock)
        }
    }

    fun isItemLocked(itemStack: ItemStack) : Boolean {
        return enabled.and(containsAny(itemStack.tags).xor(contains(itemStack.item)))
    }

    /**
     * Checks if any of the tag keys in the provided stream match with the items in the list
     * that start with the "#" character.
     *
     * @param tags A stream of TagKey<Item> objects to be matched against the items list.
     */
    private fun containsAny(tags: Stream<TagKey<Item>>) : Boolean {
        return this.actions[Type.ITEM_USE]?.filter { it.startsWith("#") }?.any { tagName -> tags.anyMatch { tag -> tag.location.asString() == tagName.substring(1) } }
            ?: false
    }

    /**
     * Checks whether the specified item exists in the list of items that do not start with the "#" character.
     *
     * @param item The item to check for existence in the list.
     * @return True if the item exists in the list, otherwise false.
     */
    private fun contains(item: Item) : Boolean {
        return this.actions[Type.ITEM_USE]?.filter { !it.startsWith("#") }
            ?.contains(BuiltInRegistries.ITEM.getKey(item).asString()) ?: false
    }

    enum class Type(val displayName: String) {
        ITEM_USE("item:use"),
        ITEM_EQUIP("item:equip"),
        BLOCK_BREAK("block:break"),
        BLOCK_INTERACT("block:interact");

        companion object {
            fun from(displayName: String) = entries.firstOrNull() { it.displayName == displayName } ?: ITEM_USE
            val CODEC: Codec<Type> = Codec.STRING.xmap(Companion::from, Type::displayName)
        }
    }
}