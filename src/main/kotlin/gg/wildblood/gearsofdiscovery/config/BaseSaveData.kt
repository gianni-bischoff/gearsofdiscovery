package gg.wildblood.gearsofdiscovery.config

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData

class BaseSaveData : SavedData() {
    var unlockList: MutableSet<String> = mutableSetOf()

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        val lockList = ListTag()
        println("Saving Unlocks: ${unlockList.joinToString()}")
        unlockList.forEach { lockName ->
            lockList.add(StringTag.valueOf(lockName))
        }
        tag.put("Unlocks", lockList)
        return tag
    }

    fun addUnlock(unlockNames: Array<out String>) {
        unlockList.addAll(unlockNames)
        setDirty()
    }

    fun removeUnlock(unlockNames: Array<out String>) {
        unlockList.removeAll(unlockNames.toSet())
        setDirty()
    }

    companion object {
        private val FACTORY = Factory(
            { BaseSaveData() },
            { tag, _ -> load(tag)}
        )

        private const val ID = "locks"

        private fun load(compound: CompoundTag): BaseSaveData {
            val data = BaseSaveData()
            val lockList = compound.getList("Unlocks", Tag.TAG_STRING.toInt())
            println("Loading Unlocks: ${lockList.joinToString()}")
            lockList.forEach { tag ->
                data.unlockList.add(tag.asString)
            }
            return data
        }

        fun get(level: ServerLevel): BaseSaveData {
            return level.dataStorage.computeIfAbsent(
                FACTORY,
                ID,
            )
        }
    }
}