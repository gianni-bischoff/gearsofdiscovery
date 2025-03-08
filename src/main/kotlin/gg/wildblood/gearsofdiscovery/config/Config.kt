package gg.wildblood.gearsofdiscovery.config

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.neoforge.common.ModConfigSpec

@EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.MOD)
object Config {
    private val BUILDER: ModConfigSpec.Builder = ModConfigSpec.Builder()

    private val DIMENSION_LOCK: ModConfigSpec.BooleanValue =
        BUILDER.comment("Whenever its possible for a Dimension to get Locked")
            .translation("config.${GearsOfDiscoveryMod.MODID}.dimensionLock")
            .define("dimensionLock", true)

    private val ITEM_USE_LOCK: ModConfigSpec.BooleanValue =
        BUILDER.comment("Whenever its possible for a Item usage to get Locked")
            .translation("config.${GearsOfDiscoveryMod.MODID}.itemUseLock")
            .define("itemUseLock", true)

    private val CRAFT_LOCK: ModConfigSpec.BooleanValue =
        BUILDER.comment("Whenever its possible for a Crafting Recipe to get Locket")
            .translation("config.${GearsOfDiscoveryMod.MODID}.craftLock")
            .define("craftLock", true)

    val SPEC: ModConfigSpec = BUILDER.build()

    var dimensionLock: Boolean = true
    var itemUseLock: Boolean = true
    var craftLock: Boolean = true

    @SubscribeEvent
    fun onLoad(event: ModConfigEvent) {
        event.config.loadedConfig?.let { config ->
            dimensionLock = DIMENSION_LOCK.get()
            itemUseLock = ITEM_USE_LOCK.get()
            craftLock = CRAFT_LOCK.get()
        }
    }
}