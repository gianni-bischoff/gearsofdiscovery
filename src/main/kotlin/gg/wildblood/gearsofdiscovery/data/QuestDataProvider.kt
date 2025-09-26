package gg.wildblood.gearsofdiscovery.data

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.content.ModRegistries.QUEST_REGISTRY_KEY
import gg.wildblood.gearsofdiscovery.content.registry.quests.QuestDefinition
import gg.wildblood.gearsofdiscovery.content.registry.quests.ItemTarget
import gg.wildblood.gearsofdiscovery.content.registry.quests.objectives.CollectObjective
import gg.wildblood.gearsofdiscovery.content.registry.quests.objectives.DeliverObjective
import gg.wildblood.gearsofdiscovery.content.registry.quests.rewards.ItemRewardDefinition
import gg.wildblood.gearsofdiscovery.content.registry.quests.rewards.MoneyRewardDefinition
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture

class QuestDataProvider(
    private val packOutput: PackOutput,
    private val lookupProvider: CompletableFuture<net.minecraft.core.HolderLookup.Provider>,
    private val existingFileHelper: ExistingFileHelper
) : DataProvider {

    override fun run(cache: CachedOutput): CompletableFuture<*> {
        val quests: MutableList<QuestDefinition> = mutableListOf()

        generateExampleQuests(quests)

        val registryBuilder = RegistrySetBuilder().add(QUEST_REGISTRY_KEY) { bootstrap ->
            quests.forEach {
                bootstrap.register(
                    ResourceKey.create(QUEST_REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(GearsOfDiscoveryMod.MODID, it.id.replace("/", "_"))),
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
    
    private fun generateExampleQuests(quests: MutableList<QuestDefinition>) {
        // Tutorial quest
        quests.add(QuestDefinition(
            id = "first_steps",
            type = QuestDefinition.Type.SOLO,
            title = "quest.first_steps.title",
            description = "quest.first_steps.desc",
            requirements = listOf(),
            objectives = listOf(
                DeliverObjective(ItemTarget(itemTag = "minecraft:logs"), 10)
            ),
            rewards = listOf(
                ItemRewardDefinition(ItemTarget(itemId = "minecraft:stone_axe"), 1),
                MoneyRewardDefinition(25)
            ),
            meta = mapOf("version" to "1", "category" to "tutorial")
        ))
        
        // Progression quest
        quests.add(QuestDefinition(
            id = "iron_age",
            type = QuestDefinition.Type.SOLO,
            title = "quest.iron_age.title",
            description = "quest.iron_age.desc",
            requirements = listOf("quest:tutorial/first_steps"),
            objectives = listOf(
                CollectObjective(ItemTarget(itemId = "minecraft:iron_ingot"), 20),
                CollectObjective(ItemTarget(itemId = "minecraft:coal"), 32)
            ),
            rewards = listOf(
                ItemRewardDefinition(ItemTarget(itemId = "minecraft:iron_pickaxe"), 1),
                ItemRewardDefinition(ItemTarget(itemId = "minecraft:iron_sword"), 1),
                MoneyRewardDefinition(100)
            ),
            meta = mapOf("version" to "1", "category" to "progression")
        ))
        
        // Resource gathering quest
        quests.add(QuestDefinition(
            id = "food_storage",
            type = QuestDefinition.Type.SOLO,
            title = "quest.food_storage.title", 
            description = "quest.food_storage.desc",
            requirements = listOf(),
            objectives = listOf(
                CollectObjective(ItemTarget(itemId = "minecraft:wheat"), 64),
                CollectObjective(ItemTarget(itemId = "minecraft:carrot"), 32),
                CollectObjective(ItemTarget(itemId = "minecraft:potato"), 32)
            ),
            rewards = listOf(
                ItemRewardDefinition(ItemTarget(itemId = "minecraft:bread"), 20),
                ItemRewardDefinition(ItemTarget(itemId = "minecraft:golden_apple"), 2),
                MoneyRewardDefinition(75)
            ),
            meta = mapOf("version" to "1", "category" to "gathering")
        ))
    }

    override fun getName(): String = "Quest Datapack Registry Data Provider"
}