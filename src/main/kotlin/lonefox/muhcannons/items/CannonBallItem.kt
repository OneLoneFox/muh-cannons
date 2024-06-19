package lonefox.muhcannons.items

import net.minecraft.item.Item

class CannonBallItem(settings: Settings?) : Item(settings) {
    companion object {
        // 3 second cool down by default
        const val COOLDOWN = 60
        const val PIERCING_VALUE = 10
        const val ENTITY_DAMAGE = 20f
    }

    val cooldown = COOLDOWN
    val piercingValue = PIERCING_VALUE
    val entityDamage = ENTITY_DAMAGE
}