package gg.wildblood.gearsofdiscovery.items

import gg.wildblood.gearsofdiscovery.GearsOfDiscoveryMod
import gg.wildblood.gearsofdiscovery.sounds.ModSounds
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.EntityTypeTags
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.LogicalSide
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock

class OtomatonWeapon(private val props: Properties) : SwordItem(Tiers.IRON, props) {

    @EventBusSubscriber(modid = GearsOfDiscoveryMod.MODID, bus = EventBusSubscriber.Bus.GAME)
    companion object {
        fun knockback(target: LivingEntity, knockBackStrength: Double, yRot: Float) {
            target.stopRiding()
            target.stopSleeping()
            target.knockback(
                knockBackStrength * .5F,
                Mth.sin(yRot * Mth.DEG_TO_RAD).toDouble(),
                (-Mth.cos(yRot * Mth.DEG_TO_RAD)).toDouble()
            )
        }

        @SubscribeEvent
        fun otomatonDoNoise(event: LeftClickBlock) {
            event.level.playLocalSound(event.pos, ModSounds.OTOMATON_SQUEAL.get(), SoundSource.PLAYERS, 1.0f, 1.0f,false)

            if(event.itemStack.item is OtomatonWeapon && event.action == PlayerInteractEvent.LeftClickBlock.Action.START) {
                when(event.side) {
                    LogicalSide.CLIENT -> event.level.playLocalSound(event.pos, (event.itemStack.item as OtomatonWeapon).getTone(), SoundSource.PLAYERS, 1.0f, 1.0f,false)
                    LogicalSide.SERVER -> event.level.playSound(event.entity, event.pos, (event.itemStack.item as OtomatonWeapon).getTone(), SoundSource.PLAYERS, 1.0f, 1.0f)
                    else -> return
                }
            }
        }

        //@SubscribeEvent(priority = EventPriority.HIGHEST)
        fun otomatonCantHurtYou(event: AttackEntityEvent) {
            val attacker: Player = event.entity
            if (event.target !is LivingEntity || event.target.type.`is`(EntityTypeTags.ARTHROPOD)) return
            val stack: ItemStack = attacker.getItemInHand(InteractionHand.MAIN_HAND)
            if (event.entity.mainHandItem.item !is OtomatonWeapon) return

            //AllSoundEvents.CARDBOARD_SWORD.playFrom(attacker, 0.75f, 1.85f)

            event.isCanceled = true


            /**
            // Reference player.attack()
            // This section replicates knockback behaviour without hurting the target
            var knockbackStrength = (attacker.getAttributeValue(Attributes.ATTACK_KNOCKBACK) + 2)
            if (attacker.level() is ServerLevel) {
                knockbackStrength = EnchantmentHelper.modifyKnockback(
                    attacker.level() as ServerLevel,
                    stack,
                    event.target,
                    attacker.level().damageSources().playerAttack(attacker),
                    knockbackStrength.toFloat()
                ).toDouble()
            }

            if (attacker.isSprinting && attacker.getAttackStrengthScale(0.5f) > 0.9f) ++knockbackStrength

            if (knockbackStrength <= 0) return

            val yRot: Float = attacker.getYRot()
            knockback(event.target as LivingEntity, knockbackStrength, yRot)

            val targetIsPlayer = event.target is Player
            val targetType: MobCategory = event.target.getClassification(false)

            if (event.target is ServerPlayer) CatnipServices.NETWORK.sendToClient(
                target,
                KnockbackPacket(yRot, knockbackStrength)
            )

            if ((targetType == MobCategory.MISC || targetType == MobCategory.CREATURE) && !targetIsPlayer) target.addEffect(
                MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 9, true, false, false)
            )

            attacker.setDeltaMovement(
                attacker.getDeltaMovement()
                    .multiply(0.6, 1.0, 0.6)
            )
            attacker.setSprinting(false)
            */
        }
    }

    fun getTone(): SoundEvent {
        return ModSounds.OTOMATON_SQUEAL.get()
    }

    override fun getAttackDamageBonus(target: Entity, damage: Float, damageSource: DamageSource): Float {
        return super.getAttackDamageBonus(target, -100.0f, damageSource)
    }

    override fun supportsEnchantment(stack: ItemStack, enchantment: Holder<Enchantment>): Boolean {
        return enchantment.key == Enchantments.KNOCKBACK
    }

    override fun isBookEnchantable(stack: ItemStack, book: ItemStack): Boolean {
        val enchants: ItemEnchantments = book.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY)
        return !enchants.keySet().any { !supportsEnchantment(stack, it) }
    }
}