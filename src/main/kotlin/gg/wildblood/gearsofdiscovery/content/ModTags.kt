package gg.wildblood.gearsofdiscovery.content

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object ModTags {
    val BACKPACK_TAG_KEY: TagKey<Item> = TagKey.create(
        Registries.ITEM,
        ResourceLocation.fromNamespaceAndPath(GearsOfDiscoveryMod.MODID, "backpacks")
    )
}