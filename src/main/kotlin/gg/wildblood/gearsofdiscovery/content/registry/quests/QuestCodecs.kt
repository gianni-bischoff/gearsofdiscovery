package gg.wildblood.gearsofdiscovery.content.registry.quests

import gg.wildblood.gearsofdiscovery.content.registry.quests.objectives.CollectObjective
import gg.wildblood.gearsofdiscovery.content.registry.quests.objectives.DeliverObjective
import gg.wildblood.gearsofdiscovery.content.registry.quests.rewards.ItemRewardDefinition
import gg.wildblood.gearsofdiscovery.content.registry.quests.rewards.MoneyRewardDefinition
import gg.wildblood.gearsofdiscovery.content.registry.quests.rewards.XPRewardDefinition

/**
 * Ensures all quest-related codecs are registered by forcing class loading.
 * This must be called before any quest deserialization occurs.
 */
object QuestCodecs {
    
    fun initialize() {
        // Force loading of all reward classes to trigger their init blocks
        ItemRewardDefinition::class
        MoneyRewardDefinition::class  
        XPRewardDefinition::class
        
        // Force loading of all objective classes to trigger their init blocks
        CollectObjective::class
        DeliverObjective::class
    }
}