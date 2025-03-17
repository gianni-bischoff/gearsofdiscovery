package gg.wildblood.gearsofdiscovery.datagen

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class ModBlockStateProvider(output: PackOutput, exFileHelper: ExistingFileHelper) : BlockStateProvider(output, GearsOfDiscoveryMod.MODID, exFileHelper) {
    override fun registerStatesAndModels() {

    }

    private fun <T> blockWithItem(block: T) where T : Block {
        simpleBlockWithItem(block, cubeAll(block))
    }
}