package gg.wildblood.gearsofdiscovery.tabs

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.Companion.CreativeModeTabRegister
import gg.wildblood.gearsofdiscovery.items.ModItems
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Items
import net.neoforged.neoforge.registries.DeferredHolder
import java.util.function.Supplier

object ModCreativeTabs {
    val gearsOfDiscoveryCreativeTab: DeferredHolder<CreativeModeTab, CreativeModeTab> = CreativeModeTabRegister.register("gearsofdiscovery_tab", Supplier {
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.gearsofdiscovery"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon { Items.CLOCK.defaultInstance }
            .displayItems { _: ItemDisplayParameters?, output: CreativeModeTab.Output ->
                output.accept(Items.CLOCK)
                output.accept(ModItems.SMALL_ENGINEERING_BUNDLE)
            }.build()
    })

    fun register() {}
}