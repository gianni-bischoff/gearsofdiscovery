package gg.wildblood.gearsofdiscovery.content

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.registries.DeferredRegister

object ModItemDataComponents {
    val REGISTRY: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, GearsOfDiscoveryMod.MODID)

}