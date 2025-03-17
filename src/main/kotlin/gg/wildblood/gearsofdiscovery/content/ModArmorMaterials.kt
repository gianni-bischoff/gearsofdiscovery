package gg.wildblood.gearsofdiscovery.content

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.ArmorMaterial.Layer
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient
import java.util.*
import java.util.function.Supplier

object ModArmorMaterials {
    val MINER_ARMOR_TYPE: Holder<ArmorMaterial> = register(
        "miner_armor",
        EnumMap(mapOf(
            ArmorItem.Type.BOOTS to 1,
            ArmorItem.Type.LEGGINGS to 2,
            ArmorItem.Type.CHESTPLATE to 3,
            ArmorItem.Type.HELMET to 1
        )),
        15, 0.0f, 0.0f
    ) { ModItems.MINERS_BACKPACK }



    fun register(
        name: String,
        typeProtection: EnumMap<ArmorItem.Type, Int>,
        enchantablity: Int, toughness: Float,
        knockbackResistance: Float,
        ingredientItem: Supplier<Item>): Holder.Reference<ArmorMaterial> {

        val location: ResourceLocation = ResourceLocation.fromNamespaceAndPath(GearsOfDiscoveryMod.MODID, name)
        val equipSound: Holder<SoundEvent> = SoundEvents.ARMOR_EQUIP_GENERIC
        val ingredient = Supplier { Ingredient.of(ingredientItem.get()) }
        val layers: List<Layer> = listOf(Layer(location))
        val typeMap: EnumMap<ArmorItem.Type, Int> = EnumMap(ArmorItem.Type::class.java)

        ArmorItem.Type.entries.forEach { type ->
            typeMap[type] = typeProtection[type]
        }

        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, location, ArmorMaterial(
            typeProtection, enchantablity, equipSound, ingredient, layers, toughness, knockbackResistance
        ))
    }
}