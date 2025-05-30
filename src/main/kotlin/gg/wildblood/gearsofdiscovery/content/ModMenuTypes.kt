package gg.wildblood.gearsofdiscovery.content

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.network.IContainerFactory
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModMenuTypes {
    val REGISTRY = DeferredRegister.create(Registries.MENU, GearsOfDiscoveryMod.MODID)

    private fun <T : AbstractContainerMenu> registerMenuType(name: String, factory: IContainerFactory<T>): DeferredHolder<MenuType<*>, MenuType<T>> {
        return REGISTRY.register(name, Supplier { IMenuTypeExtension.create(factory) })
    }
}