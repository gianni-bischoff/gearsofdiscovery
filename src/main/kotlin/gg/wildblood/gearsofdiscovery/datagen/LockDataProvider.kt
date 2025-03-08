package gg.wildblood.gearsofdiscovery.datagen

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.datamaps.Lock
import gg.wildblood.gearsofdiscovery.locks.ModRegistries.LOCK_REGISTRY_KEY
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceKey
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class LockDataProvider(
    private val packOutput: PackOutput,
    private val lookupProvider: CompletableFuture<net.minecraft.core.HolderLookup.Provider>,
    private val existingFileHelper: ExistingFileHelper
) : DataProvider {

    override fun run(cache: CachedOutput): CompletableFuture<*> {
        val locks: MutableList<Lock> = mutableListOf()

        locks.add(Lock(
            "fish",
            "Locks all Fishes",
            Lock.Type.ITEM_USE,
            listOf("#c:foods")
        ))

        locks.add(Lock(
            "nodirt",
            "No dirt in my Hood.",
            Lock.Type.ITEM_USE,
            listOf("minecraft:dirt")
        ))

        locks.add(Lock(
            "disable_end",
            "Disable End.",
            Lock.Type.DIMENSION,
            listOf("minecraft:the_end")
        ))

        locks.add(Lock(
            "disable_end_portal_interact",
            "Disable End portal interaction.",
            Lock.Type.INTERACT_WITH,
            listOf("minecraft:end_portal")
        ))

        val registryBuilder = RegistrySetBuilder().add(LOCK_REGISTRY_KEY) { bootstrap ->
                locks.forEach {
                    bootstrap.register(
                        ResourceKey.create(LOCK_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(GearsOfDiscoveryMod.MODID, it.name)),
                        it
                    )
                }
            }

        val provider = DatapackBuiltinEntriesProvider(
            packOutput,
            lookupProvider,
            registryBuilder,
            setOf(GearsOfDiscoveryMod.MODID)
        )

        return provider.run(cache)
    }

    override fun getName(): String = "Lock Datapack Registry Data Provider"
}