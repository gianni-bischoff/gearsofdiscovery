package gg.wildblood.gearsofdiscovery.blocks

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.Companion.BlockRegister
import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod.Companion.ItemRegister
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem

object ModBlocks {
    val OFFERING_BLOCK: BlockContainer = registerSimpleItem("offering_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE))

    private fun registerSimpleItem(name: String, properties: BlockBehaviour.Properties): BlockContainer {
        val block = BlockRegister.registerSimpleBlock(name, properties)
        val item = ItemRegister.registerSimpleBlockItem(name, block)
        return BlockContainer(block, item)
    }

    fun register() {}
}

data class BlockContainer(val block: DeferredBlock<Block>, val item: DeferredItem<BlockItem>)