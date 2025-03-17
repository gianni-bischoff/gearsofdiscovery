package gg.wildblood.gearsofdiscovery.content.items.components

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object ModItemDataComponents {
    val REGISTRY: DeferredRegister.DataComponents = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, GearsOfDiscoveryMod.MODID)

    val SPECIALIZED_INVENTORY_COMPONENT: DeferredHolder<DataComponentType<*>, DataComponentType<SpecializedInventoryComponent>> = REGISTRY.registerComponentType(
        "specialized_inv"
    ) { builder ->
        builder.persistent(SpecializedInventoryComponent.BASIC_CODEC).networkSynchronized(SpecializedInventoryComponent.BASIC_STREAM_CODEC)
    }

    val BACKPACK_COMPONENT: DeferredHolder<DataComponentType<*>, DataComponentType<BackpackContent>> = REGISTRY.registerComponentType(
        "backpack"
    ) { builder ->
        builder.persistent(BackpackContent.CODEC).networkSynchronized(BackpackContent.STREAM_CODEC)
    }

}