package gg.wildblood.gearsofdiscovery.config

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.saveddata.SavedData

class LockSavedData : SavedData() {
    private var locks: MutableList<String> = mutableListOf()

    fun getLocks(): List<String> = locks.toList()

    fun containsAny(locks: List<String>): Boolean = this.locks.any { lock -> locks.contains(lock) }

    fun addLock(lockName: String) {
        if (!locks.contains(lockName)) {
            locks.add(lockName)
            setDirty()
        }
    }

    fun removeLock(lockName: String) {
        if (locks.remove(lockName)) {
            setDirty()
        }
    }

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        val lockList = ListTag()
        locks.forEach { lockName ->
            lockList.add(StringTag.valueOf(lockName))
        }
        tag.put("Locks", lockList)
        return tag
    }

    companion object {
        private val FACTORY = Factory(
            { LockSavedData() },
            { tag, lookupProvider -> load(tag)}
        )

        private const val ID = "locks"

        fun load(compound: CompoundTag): LockSavedData {
            val data = LockSavedData()
            val lockList = compound.getList("Locks", Tag.TAG_STRING.toInt())
            lockList.forEach { tag ->
                data.locks.add(tag.asString)
            }
            return data
        }

        fun get(level: ServerLevel): LockSavedData {
            return level.dataStorage.computeIfAbsent(
                FACTORY,
                ID,
            )
        }


        fun get(player: Player): LockSavedData? {
            return player.server?.let { get(it.overworld()) }
        }
    }


}