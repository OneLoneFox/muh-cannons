package lonefox.muhcannons.render.entities

import lonefox.muhcannons.Muhcannons
import lonefox.muhcannons.MuhcannonsClient
import lonefox.muhcannons.entities.CannonBallEntity
import lonefox.muhcannons.render.entities.model.CannonBallEntityModel
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper

class CannonBallEntityRenderer : EntityRenderer<CannonBallEntity> {
    constructor(ctx: EntityRendererFactory.Context?) : super(ctx) {
        if(ctx == null){
            Muhcannons.logger.error("Cannon init CannonBallEntityRenderer ctx is null D:")
            return
        }
        model = CannonBallEntityModel(ctx.getPart(MuhcannonsClient.CANNON_BALL_LAYER))
    }

    private lateinit var model: CannonBallEntityModel
    private val texture = Identifier(Muhcannons.MOD_ID, "textures/entity/cannon_ball.png")

    override fun getBlockLight(entity: CannonBallEntity?, pos: BlockPos?): Int { return 15 }

    override fun render(
        entity: CannonBallEntity?,
        yaw: Float,
        tickDelta: Float,
        matrices: MatrixStack?,
        vertexConsumers: VertexConsumerProvider?,
        light: Int
    ) {
        val vertexConsumer = vertexConsumers?.getBuffer(this.model.getLayer(texture))!!
        // todo: rotate to movement direction
        val yaw = MathHelper.lerpAngleDegrees(tickDelta, entity!!.prevYaw, entity.yaw)
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f)
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light)
    }

    override fun getTexture(entity: CannonBallEntity?): Identifier {
        return texture
    }
}