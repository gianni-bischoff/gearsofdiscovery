package gg.wildblood.gearsofdiscovery.content.items.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.client.ICurioRenderer


class BackpackRenderer : ICurioRenderer {
    override fun <T : LivingEntity?, M : EntityModel<T>?> render(
        stack: ItemStack,
        slotContext: SlotContext,
        matrixStack: PoseStack,
        renderLayerParent: RenderLayerParent<T, M>,
        renderTypeBuffer: MultiBufferSource,
        light: Int,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        matrixStack.pushPose()

        // Apply entity transformations to make it rotate/move with the player
        matrixStack.translate(
            0.0,
            .61,
            .28
        ) // Move to the chest position
        //matrixStack.mulPose(Axis.YP.rotationDegrees(-slotContext.entity().yBodyRot)) // Rotate with player
        matrixStack.mulPose(Axis.XN.rotationDegrees(90F))

        matrixStack.scale(0.4f, 0.4f, 0.4f) // Scale item

        // Render the item
        val mc = Minecraft.getInstance()

        mc.itemRenderer.renderStatic(
            stack,
            ItemDisplayContext.FIXED,
            light,
            light,
            matrixStack,
            renderTypeBuffer,
            slotContext.entity().level(),
            0
        )

        matrixStack.popPose()
    }
}