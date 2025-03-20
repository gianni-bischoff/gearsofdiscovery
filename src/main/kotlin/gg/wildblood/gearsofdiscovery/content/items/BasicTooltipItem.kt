package gg.wildblood.gearsofdiscovery.content.items

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

class BasicTooltipItem(properties: Properties) : Item(properties) {
    var tooltipComponent: Component? = null

    fun withTooltip(tooltipComponent: Component): BasicTooltipItem {
        this.tooltipComponent = tooltipComponent
        return this
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        if(tooltipComponent != null) tooltipComponents.add(tooltipComponent!!)
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}