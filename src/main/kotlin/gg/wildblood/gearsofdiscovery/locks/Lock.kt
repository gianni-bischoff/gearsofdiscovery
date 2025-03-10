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

    enum class Type(val displayName: String) {
        ITEM_USE("item:use"),
        ITEM_EQUIP("item:equip"),
        BLOCK_BREAK("block:break"),
        BLOCK_INTERACT("block:interact"),
        DIMENSION_TRAVEL("dimension:travel");

        companion object {
            fun from(displayName: String) = entries.firstOrNull() { it.displayName == displayName } ?: ITEM_USE
            val CODEC: Codec<Type> = Codec.STRING.xmap(Companion::from, Type::displayName)
        }
    }
}