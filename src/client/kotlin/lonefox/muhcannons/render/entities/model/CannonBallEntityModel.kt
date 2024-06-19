package lonefox.muhcannons.render.entities.model

import lonefox.muhcannons.entities.CannonBallEntity
import net.minecraft.client.model.ModelData
import net.minecraft.client.model.ModelPart
import net.minecraft.client.model.ModelPartBuilder
import net.minecraft.client.model.ModelTransform
import net.minecraft.client.model.TexturedModelData
import net.minecraft.client.render.entity.model.SinglePartEntityModel

class CannonBallEntityModel(private val model: ModelPart) : SinglePartEntityModel<CannonBallEntity>() {
    override fun setAngles(
        entity: CannonBallEntity?,
        limbAngle: Float,
        limbDistance: Float,
        animationProgress: Float,
        headYaw: Float,
        headPitch: Float
    ) {

    }

    override fun getPart(): ModelPart {
        return model
    }

    private fun getBullet(): ModelPart? {
        return model.getChild("main")
    }

    fun setRotation(yaw: Float, pitch: Float){
        val bullet = getBullet()
        bullet?.yaw = yaw
        bullet?.pitch = pitch
    }

    companion object {
        public fun getTexturedModelData(): TexturedModelData {
            val modelData = ModelData()
            val modelPartData = modelData.root
            modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, 0f, -4.0f, 8.0f, 8.0f, 8.0f), ModelTransform.pivot(0f, 0f, 0f))
            return TexturedModelData.of(modelData, 32, 32)
        }
    }

}