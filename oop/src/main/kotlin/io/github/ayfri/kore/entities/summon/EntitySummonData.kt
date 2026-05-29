package io.github.ayfri.kore.entities.summon

import io.github.ayfri.kore.arguments.chatcomponents.ChatComponents
import io.github.ayfri.kore.arguments.chatcomponents.textComponent
import io.github.ayfri.kore.arguments.maths.Vec2
import io.github.ayfri.kore.arguments.numbers.PosNumber
import io.github.ayfri.kore.arguments.types.literals.LiteralArgument
import io.github.ayfri.kore.arguments.types.literals.UUIDArgument
import io.github.ayfri.kore.utils.nbtList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.benwoodworth.knbt.*

/**
 * Serializable fields mirror common Minecraft entity root NBT. Use [applyCommonEntitySummonNbt] inside a summon
 * compound builder. Item-only spawn options live on [ItemEntitySummonData] (`PickupDelay`, `Age`, … are applied in
 * [io.github.ayfri.kore.items.summon] after the shared tags).
 */
@Serializable
sealed class EntitySummonData {
	var air: Int? = null
	var customName: String? = null
	var fallDistance: Double? = null
	var fire: Short? = null
	var glowing: Boolean? = null
	var hasVisualFire: Boolean? = null
	var invulnerable: Boolean? = null
	var motion: Triple<Double, Double, Double>? = null
	var noGravity: Boolean? = null
	var onGround: Boolean? = null
	var portalCooldown: Int? = null
	var pos: Triple<PosNumber, PosNumber, PosNumber>? = null

	@Transient
	var rotation: Vec2? = null
	var silent: Boolean? = null
	var tags: List<String>? = null
}

fun EntitySummonData.applyCommonEntitySummonNbt(nbt: NbtCompoundBuilder) {
	nbt.apply {
		air?.let { put("Air", it) }
		when {
			this@applyCommonEntitySummonNbt is ItemEntitySummonData -> {
				val item = this@applyCommonEntitySummonNbt
				when {
					item.displayName != null -> {
						put("CustomName", item.displayName!!.toNbtTag())
						put("CustomNameVisible", item.displayNameVisible)
					}

					customName != null -> {
						put("CustomName", NbtString(customName!!))
						put("CustomNameVisible", item.displayNameVisible)
					}
				}
			}

			customName != null -> put("CustomName", NbtString(customName!!))
		}
		fallDistance?.let { put("FallDistance", it) }
		fire?.let { put("Fire", it) }
		glowing?.let { put("Glowing", it) }
		hasVisualFire?.let { put("HasVisualFire", it) }
		invulnerable?.let { put("Invulnerable", it) }
		motion?.let { (dx, dy, dz) ->
			put("Motion", nbtList<NbtDouble> {
				add(NbtDouble(dx))
				add(NbtDouble(dy))
				add(NbtDouble(dz))
			})
		}
		noGravity?.let { put("NoGravity", it) }
		onGround?.let { put("OnGround", it) }
		portalCooldown?.let { put("PortalCooldown", it) }
		pos?.let { (x, y, z) ->
			put("Pos", nbtList<NbtDouble> {
				add(NbtDouble(x.value))
				add(NbtDouble(y.value))
				add(NbtDouble(z.value))
			})
		}
		rotation?.let { r ->
			put("Rotation", nbtList<NbtFloat> {
				add(NbtFloat(r.x.value.toFloat()))
				add(NbtFloat(r.y.value.toFloat()))
			})
		}
		silent?.let { put("Silent", it) }
		tags?.let { tagList ->
			put("Tags", nbtList<NbtString> {
				tagList.forEach { add(NbtString(it)) }
			})
		}
	}
}

@Serializable
@SerialName("item")
data class ItemEntitySummonData(
	var age: Short? = null,
	var displayNameVisible: Boolean = true,
	var health: Short? = null,
	var owner: UUIDArgument? = null,
	var pickupDelay: Short? = null,
	var showcase: Boolean = false,
	var thrower: UUIDArgument? = null,
) : EntitySummonData() {
	@Transient
	var displayName: ChatComponents? = null
}

fun itemEntitySummonData(block: ItemEntitySummonData.() -> Unit) = ItemEntitySummonData().apply(block)

fun EntitySummonData.motion(dx: Double, dy: Double, dz: Double) {
	motion = Triple(dx, dy, dz)
}

fun EntitySummonData.rotation(yaw: Float, pitch: Float) {
	rotation = Vec2(yaw, pitch)
}

fun ItemEntitySummonData.displayName(text: String) {
	customName = textComponent(text).asString()
}

fun ItemEntitySummonData.displayName(holder: LiteralArgument) {
	customName = holder.asString()
}
