package gg.wildblood.gearsofdiscovery.datagen

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.ExistingFileHelper
import top.theillusivec4.curios.api.CuriosDataProvider
import java.util.concurrent.CompletableFuture

class ModCuriosDataProvider(output: PackOutput?, fileHelper: ExistingFileHelper?,
                            registries: CompletableFuture<HolderLookup.Provider>?) : CuriosDataProvider(GearsOfDiscoveryMod.MODID, output,
    fileHelper, registries) {
    override fun generate(registries: HolderLookup.Provider?, fileHelper: ExistingFileHelper?) {
        this.createEntities("wearer")
            .addPlayer()
            .addSlots("back")
    }
}