package gg.wildblood.gearsofdiscovery.datamaps

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gg.wildblood.gearsofdiscovery.config.LockSavedData
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.stream.Stream

data class Lock(
    val name: String,
    val description: String,
    val type: String,
    val items: List<String>
) {
    companion object {
        val CODEC: Codec<Lock> = RecordCodecBuilder.create { instance: RecordCodecBuilder.Instance<Lock> ->
            instance.group(
                Codec.STRING.fieldOf("name").forGetter(Lock::name),
                Codec.STRING.fieldOf("description").forGetter(Lock::description),
                Codec.STRING.fieldOf("type").forGetter(Lock::type),
                Codec.list(Codec.STRING).fieldOf("items").forGetter(Lock::items)
            ).apply(instance, ::Lock)
        }
    }

    fun isItemLocked(itemStack: ItemStack, player: Player) : Boolean {
        return isLockEnabled(player)?.and(containsAny(itemStack.tags).xor(contains(itemStack.item))) ?: false
    }

    private fun isLockEnabled(player: Player): Boolean? {
        return LockSavedData.get(player)?.getLocks()?.contains(name)
    }

    /**
     * Checks if any of the tag keys in the provided stream match with the items in the list
     * that start with the "#" character.
     *
     * @param tags A stream of TagKey<Item> objects to be matched against the items list.
     */
    private fun containsAny(tags: Stream<TagKey<Item>>) : Boolean {
        return this.items.filter { it.startsWith("#") }.any { tagName -> tags.anyMatch { tag -> tag.location.asString() == tagName.substring(1) } }
    }

    /**
     * Checks whether the specified item exists in the list of items that do not start with the "#" character.
     *
     * @param item The item to check for existence in the list.
     * @return True if the item exists in the list, otherwise false.
     */
    private fun contains(item: Item) : Boolean {
        return this.items.filter { !it.startsWith("#") }.contains(BuiltInRegistries.ITEM.getKey(item).asString())
    }
}

private fun ResourceLocation.asString(): String = this.namespace + ":" + this.path