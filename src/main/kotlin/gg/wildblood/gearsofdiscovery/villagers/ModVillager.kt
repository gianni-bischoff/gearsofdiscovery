package gg.wildblood.gearsofdiscovery.villagers

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.core.registries.BuiltInRegistries
import net.neoforged.neoforge.registries.DeferredRegister

object ModVillager {
    val POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, GearsOfDiscoveryMod.MODID)
    val VILLAGER_PROFESSION = DeferredRegister.create(BuiltInRegistries.VILLAGER_PROFESSION, GearsOfDiscoveryMod.MODID)
}