package gg.wildblood.gearsofdiscovery.datagen

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.content.registry.Lock
import gg.wildblood.gearsofdiscovery.utility.extensions.asString
import gg.wildblood.gearsofdiscovery.content.ModRegistries.LOCK_REGISTRY_KEY
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.neoforged.neoforge.common.Tags
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

        locks.add(
            Lock(
            "fish",
            "Locks all Fishes",
            mapOf(
                Lock.Type.ITEM_USE to listOf(Tags.Items.FOODS.asString())
            )
        )
        )

        locks.add(
            Lock(
            "disable_eye_of_ender",
            "Disable End portal interaction.",
            mapOf(
                Lock.Type.ITEM_USE to listOf(Items.ENDER_EYE.asString())
            )
        )
        )

        locks.add(
            Lock(
            "disable_mining_stone",
            "No Stone for you.",
            mapOf(
                Lock.Type.BLOCK_BREAK to listOf(Blocks.STONE.asString())
            )
        )
        )

        locks.add(
            Lock(
            "disable_nether",
            "The nether should be prohibited.",
            mapOf(
                Lock.Type.DIMENSION_TRAVEL to listOf(Level.NETHER.location().asString())
            )
        )
        )

        locks.add(
            Lock(
            "disable_end",
            "The end should be prohibited.",
            mapOf(
                Lock.Type.DIMENSION_TRAVEL to listOf(Level.END.location().asString())
            )
        )
        )

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

fun Block.asString(): String = BuiltInRegistries.BLOCK.getKey(this).asString()
fun TagKey<Item>.asString(): String = "#${this.location.namespace}:${this.location.path}"
fun Item.asString(): String = BuiltInRegistries.ITEM.getKey(this).asString()