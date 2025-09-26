package gg.wildblood.gearsofdiscovery.utility.extensions

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import kotlin.jvm.optionals.getOrNull

object ItemUtilities {
    fun getItemById(itemId: String): Item? {
        val rl = ResourceLocation.tryParse(itemId) ?: return null
        return BuiltInRegistries.ITEM.getOptional(rl).getOrNull()
    }
}