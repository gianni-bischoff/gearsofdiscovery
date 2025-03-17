package gg.wildblood.gearsofdiscovery.content.menus

import com.mojang.blaze3d.systems.RenderSystem
import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.content.ModMenuTypes
import gg.wildblood.gearsofdiscovery.content.items.components.ModItemDataComponents
import gg.wildblood.gearsofdiscovery.content.items.components.SpecializedInventoryComponent
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandlerModifiable
import net.neoforged.neoforge.items.SlotItemHandler


class BackpackMenu(player: Player, menuType: MenuType<*>?, containerId: Int) : AbstractContainerMenu(menuType, containerId) {
     init {
        this.addPlayerInventory(player.inventory)
        this.addPlayerHotbar(player.inventory)

        this.addSlot(SlotItemHandler(
            SpecializedInventoryComponentItemHandlerWrapper(player.mainHandItem),
            0,
            80,
            35)
        )
    }

    constructor(containerId: Int , inv: Inventory, data: RegistryFriendlyByteBuf?) : this(
        inv.player,
        ModMenuTypes.BACKPACK_MENU.get(),
        containerId
    )

    constructor(containerId: Int, inv: Inventory?, player: Player) : this(
        player,
        ModMenuTypes.BACKPACK_MENU.get(),
        containerId
    )

    val HOTBAR_SLOT_COUNT: Int = 9
    val PLAYER_INVENTORY_ROW_COUNT: Int = 3
    val PLAYER_INVENTORY_COLUMN_COUNT: Int = 9
    val PLAYER_INVENTORY_SLOT_COUNT: Int = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT
    val VANILLA_SLOT_COUNT: Int = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT
    val VANILLA_FIRST_SLOT_INDEX: Int = 0
    val TE_INVENTORY_FIRST_SLOT_INDEX: Int = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT

    // THIS YOU HAVE TO DEFINE!
    val TE_INVENTORY_SLOT_COUNT: Int = 1
    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        val sourceSlot = slots[index]
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY //EMPTY_ITEM
        val sourceStack = sourceSlot.item
        val copyOfSourceStack = sourceStack.copy()


        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(
                    sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                            + TE_INVENTORY_SLOT_COUNT, false
                )
            ) {
                return ItemStack.EMPTY // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(
                    sourceStack,
                    VANILLA_FIRST_SLOT_INDEX,
                    VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
                    false
                )
            ) {
                return ItemStack.EMPTY
            }
        } else {
            println("Invalid slotIndex:$index")
            return ItemStack.EMPTY
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.count == 0) {
            sourceSlot.set(ItemStack.EMPTY)
        } else {
            sourceSlot.setChanged()
        }
        sourceSlot.onTake(player, sourceStack)
        return copyOfSourceStack
    }

    override fun stillValid(player: Player): Boolean {
        return true
    }

    private fun addPlayerInventory(playerInventory: Inventory) {
        for (i in 0..2) {
            for (l in 0..8) {
                this.addSlot(Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18))
            }
        }
    }

    private fun addPlayerHotbar(playerInventory: Inventory) {
        for (i in 0..8) {
            this.addSlot(Slot(playerInventory, i, 8 + i * 18, 142))
        }
    }

}

class SpecializedInventoryComponentItemHandlerWrapper(private var itemStack: ItemStack) : IItemHandlerModifiable {
    val MaxSlotSize = 66;

    private fun getInventoryComponent() = itemStack.get(ModItemDataComponents.SPECIALIZED_INVENTORY_COMPONENT)!!

    override fun getSlots(): Int {
        return 1
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        return getInventoryComponent().items[slot]
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return ItemStack.EMPTY
    }

    private fun updateInventory(slot: Int, stack: ItemStack) {
        // Erstelle eine modifizierbare Kopie der aktuellen Items-Liste
        val mutableItems = getInventoryComponent().items
        mutableItems[slot] = stack // Aktualisiere den Slot
        itemStack.set(ModItemDataComponents.SPECIALIZED_INVENTORY_COMPONENT, SpecializedInventoryComponent(mutableItems))
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return ItemStack.EMPTY
    }

    override fun getSlotLimit(slot: Int): Int {
        return MaxSlotSize
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        return true;
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        val inventoryComponent = getInventoryComponent()
        inventoryComponent.items[slot] = stack
        itemStack.set(ModItemDataComponents.SPECIALIZED_INVENTORY_COMPONENT, inventoryComponent)
    }

}

class BackpackScreen(menu: BackpackMenu, playerInventory: Inventory, title: Component) : AbstractContainerScreen<BackpackMenu>(
    menu,
    playerInventory,
    title
) {
    companion object {
        val GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(GearsOfDiscoveryMod.MODID, "textures/gui/backpack/backpack_gui.png")
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShaderTexture(0, GUI_TEXTURE)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShader(GameRenderer::getPositionTexShader)

        val x = (width - imageWidth) / 2;
        val y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x,y, 0, 0, imageWidth, imageHeight)
    }

}