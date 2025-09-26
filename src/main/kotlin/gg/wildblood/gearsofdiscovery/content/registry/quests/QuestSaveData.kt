package gg.wildblood.gearsofdiscovery.content.registry.quests

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData
import java.util.*

/**
 * Handles persistence of quest data for all players
 */
class QuestSaveData : SavedData() {
    
    private val playerQuestData = mutableMapOf<UUID, PlayerQuestData>()
    
    companion object {
        private const val DATA_NAME = "quest_data"
        
        // Codec for the entire save data structure
        private val CODEC: Codec<Map<UUID, PlayerQuestData>> = 
            Codec.unboundedMap(
                Codec.STRING.xmap({ UUID.fromString(it) }, { it.toString() }),
                PlayerQuestData.CODEC
            )
        
        private val FACTORY = Factory(
            { QuestSaveData() },
            { tag, registries -> load(tag, registries) }
        )
        
        private fun load(tag: CompoundTag, registries: HolderLookup.Provider): QuestSaveData {
            val data = QuestSaveData()
            
            if (tag.contains("quest_data")) {
                val questDataTag = tag.getCompound("quest_data")
                val result = CODEC.parse(NbtOps.INSTANCE, questDataTag)
                
                result.result().ifPresent { questMap ->
                    data.playerQuestData.putAll(questMap)
                }
            }
            
            return data
        }
        
        fun get(level: ServerLevel): QuestSaveData {
            return level.dataStorage.computeIfAbsent(FACTORY, DATA_NAME)
        }
    }
    
    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        val result = CODEC.encodeStart(NbtOps.INSTANCE, playerQuestData.toMap())
        
        result.result().ifPresent { encodedData ->
            tag.put("quest_data", encodedData)
        }
        
        return tag
    }
    
    /**
     * Gets quest data for a specific player
     */
    fun getPlayerQuestData(playerId: UUID): PlayerQuestData {
        return playerQuestData.computeIfAbsent(playerId) {
            PlayerQuestData(playerId)
        }
    }
    
    /**
     * Updates quest data for a player and marks as dirty for saving
     */
    fun setPlayerQuestData(playerId: UUID, questData: PlayerQuestData) {
        playerQuestData[playerId] = questData
        setDirty()
    }
    
    /**
     * Gets all player quest data
     */
    fun getAllPlayerQuestData(): Map<UUID, PlayerQuestData> {
        return playerQuestData.toMap()
    }
    
    /**
     * Marks data as dirty for saving
     */
    fun markDirty() {
        setDirty()
    }
}