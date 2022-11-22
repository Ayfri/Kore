package arguments.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import serializers.LowercaseSerializer

@Serializable(Attribute.Companion.AttributeSerializer::class)
enum class Attribute {
	GENERIC_ARMOR,
	GENERIC_ARMOR_TOUGHNESS,
	GENERIC_ATTACK_DAMAGE,
	GENERIC_ATTACK_SPEED,
	GENERIC_FLYING_SPEED,
	GENERIC_FOLLOW_RANGE,
	GENERIC_KNOCKBACK_RESISTANCE,
	GENERIC_LUCK,
	GENERIC_MAX_HEALTH,
	GENERIC_MOVEMENT_SPEED,
	HORSE_JUMP_STRENGTH,
	ZOMBIE_SPAWN_REINFORCEMENTS;
	
	companion object {
		val values = values()
		
		object AttributeSerializer : KSerializer<Attribute> by LowercaseSerializer(values) {
			override fun serialize(encoder: Encoder, value: Attribute) {
				encoder.encodeString(value.name.replaceFirst('_', '.').lowercase())
			}
		}
	}
}
