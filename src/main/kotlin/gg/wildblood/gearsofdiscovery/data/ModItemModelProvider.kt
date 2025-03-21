package gg.wildblood.gearsofdiscovery.data

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class ModItemModelProvider(output: PackOutput, existingFileHelper: ExistingFileHelper)
    : ItemModelProvider(output, GearsOfDiscoveryMod.MODID, existingFileHelper) {
    override fun registerModels() {

    }
}