package gg.wildblood.gearsofdiscovery.utility.extensions

import gg.wildblood.gearsofdiscovery.content.ModRegistries
import gg.wildblood.gearsofdiscovery.content.registry.Lock
import net.minecraft.client.Minecraft
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import java.util.stream.Stream
import kotlin.jvm.optionals.getOrNull

fun ItemStack.hasTypeLock(type: Lock.Type) : Boolean {
    val registry = Minecraft.getInstance().tryGetLockRegistry() ?: return false

    return registry.locksWithType(type)
        .any { lock ->
            lock.actions[type]?.any {
                this.tags.contains(it.substring(1)) || it == this.getKey().asString()
            } ?: false
        }
}

fun ResourceKey<Level>.hasTypeLock(type: Lock.Type) : Boolean {
    val registry = Minecraft.getInstance().tryGetLockRegistry() ?: return false

    return registry.locksWithType(type)
        .any { lock -> lock.actions[type]?.contains(this.location().asString()) ?: false }
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

fun Stream<out TagKey<out Item>>.contains(tagName: String) : Boolean = this.anyMatch { it.location.asString() == tagName }
fun Stream<out TagKey<out Item>>.contains(vararg tagName: String) : Boolean = this.anyMatch { tag -> tagName.any { it == tag.location.asString() } }

fun Registry<Lock>.locksWithType(type: Lock.Type): List<Lock> = this.filter { it.enabled && it.actions.containsKey(type) }
fun Registry<Lock>.locksWithType(vararg type: Lock.Type): List<Lock> = this.filter { lock -> lock.enabled && lock.actions.keys.any { it in type} }

fun Minecraft.tryGetLockRegistry() = connection?.registryAccess()?.registry(ModRegistries.LOCK_REGISTRY_KEY)?.getOrNull()

fun ResourceLocation.asString(): String = this.namespace + ":" + this.path

fun ItemStack.getKey() = BuiltInRegistries.ITEM.getKey(this.item)