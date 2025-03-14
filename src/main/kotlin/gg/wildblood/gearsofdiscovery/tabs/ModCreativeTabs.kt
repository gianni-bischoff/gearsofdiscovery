package gg.wildblood.gearsofdiscovery.tabs

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.blocks.ModBlocks
import gg.wildblood.gearsofdiscovery.items.ModItems
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Items
import net.neoforged.neoforge.registries.DeferredRegister

import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object ModCreativeTabs {
    val REGISTRY: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GearsOfDiscoveryMod.MODID)

    val gearsOfDiscoveryCreativeTab: CreativeModeTab by REGISTRY.register("gearsofdiscovery_tab") { ->
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.gearsofdiscovery"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon { Items.CLOCK.defaultInstance }
            .displayItems { _: ItemDisplayParameters?, output: CreativeModeTab.Output ->
                output.accept(Items.CLOCK)
                output.accept(ModItems.SMALL_ENGINEERING_BUNDLE)
                output.accept(ModBlocks.PILLOW_ITEM)
            }.build()
    }
}