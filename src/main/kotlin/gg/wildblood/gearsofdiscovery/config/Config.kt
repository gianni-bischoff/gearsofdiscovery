package gg.wildblood.gearsofdiscovery.config

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
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

    private val STARTING_EQUIPMENT_STRINGS = BUILDER.comment("Starting equipment for new players")
        .translation("config.${GearsOfDiscoveryMod.MODID}.startingEquipment")
        .defineList("startingEquipment", listOf("minecraft:iron_ingot#2")) { itemName -> validateItemName(itemName) }

    private val DISCORD_CLIENT_ID: ModConfigSpec.ConfigValue<String> =
        BUILDER.comment("Whenever its possible for a Crafting Recipe to get Locket")
            .translation("config.${GearsOfDiscoveryMod.MODID}.craftLock")
            .define("client_id", "xxx")

    private val DISCORD_SECRET: ModConfigSpec.ConfigValue<String> =
        BUILDER.comment("Whenever its possible for a Crafting Recipe to get Locket")
            .translation("config.${GearsOfDiscoveryMod.MODID}.craftLock")
            .define("client_id", "xxx")



    val SPEC: ModConfigSpec = BUILDER.build()

    var dimensionLock: Boolean = true
    var itemUseLock: Boolean = true
    var craftLock: Boolean = true
    var startingEquipment: List<ItemStack> = listOf()
    var discordClientId: String = ""
    var discordSecret: String = ""

    private fun validateItemName(obj: Any): Boolean = obj is String && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(obj.substringBefore('#'))) && obj.substringAfter('#').toIntOrNull() != null

    @SubscribeEvent
    fun onLoad(event: ModConfigEvent) {
        event.config.loadedConfig?.let {
            dimensionLock = DIMENSION_LOCK.get()
            itemUseLock = ITEM_USE_LOCK.get()
            craftLock = CRAFT_LOCK.get()
            startingEquipment = STARTING_EQUIPMENT_STRINGS.get().map {
                val item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(it.substringBefore('#')))
                ItemStack(item, it.substringAfter("#").toInt())
            }.toList()
        }
    }
}