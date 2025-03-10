package gg.wildblood.gearsofdiscovery.locks

import net.minecraft.client.Minecraft
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import java.util.stream.Stream
import kotlin.jvm.optionals.getOrNull

object LockHelper {
}

fun ItemStack.hasTypeLock(type: Lock.Type) : Boolean {
    val registry = Minecraft.getInstance().tryGetLockRegistry() ?: return false

    // Blocked if in Type is the Tag or the name of the item | the tag is prefixed with # that needs to be removed before checking
    return registry.locksWithType(type)
        .any { lock ->
            lock.actions[type]?.any {
                this.tags.contains(it.substring(1)) || it == this.getKey().asString()
            } ?: false
        }
}

fun Block.hasTypeLock(type: Lock.Type): Boolean {
    val registry = Minecraft.getInstance().tryGetLockRegistry() ?: return false

    return registry.locksWithType(type)
        .any { it.actions[type]?.contains(BuiltInRegistries.BLOCK.getKey(this).asString()) ?: false }
}

fun Block.hasTypeLock(vararg type: Lock.Type): Boolean {
    val registry = Minecraft.getInstance().tryGetLockRegistry() ?: return false

    return registry.locksWithType(*type)
        .any { lock -> lock.actions.filter { entry -> type.contains(entry.key) }.values.any { it.contains(BuiltInRegistries.BLOCK.getKey(this).asString()) } }
}

fun Block.isLocked() : Boolean {
    val registry = Minecraft.getInstance().tryGetLockRegistry() ?: return false

    return registry.locksWithType(Lock.Type.BLOCK_BREAK)
        .any { it.actions[Lock.Type.BLOCK_BREAK]?.contains(BuiltInRegistries.BLOCK.getKey(this).asString()) ?: false }
}

fun Stream<out TagKey<out Item>>.contains(tagName: String) : Boolean = this.anyMatch { it.location.asString() == tagName }
fun Stream<out TagKey<out Item>>.contains(vararg tagName: String) : Boolean = this.anyMatch { tag -> tagName.any { it == tag.location.asString() } }

fun Registry<Lock>.locksWithType(type: Lock.Type): List<Lock> = this.filter { it.enabled && it.actions.containsKey(type) }
fun Registry<Lock>.locksWithType(vararg type: Lock.Type): List<Lock> = this.filter { lock -> lock.enabled && lock.actions.keys.any { it in type} }

fun Minecraft.tryGetLockRegistry() = connection?.registryAccess()?.registry(ModRegistries.LOCK_REGISTRY_KEY)?.getOrNull()

fun ResourceLocation.asString(): String = this.namespace + ":" + this.path

fun ItemStack.getKey() = BuiltInRegistries.ITEM.getKey(this.item)