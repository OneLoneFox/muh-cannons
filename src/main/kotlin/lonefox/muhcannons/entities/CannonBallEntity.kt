package lonefox.muhcannons.entities

import lonefox.muhcannons.ModDamageTypes
import lonefox.muhcannons.Muhcannons
import lonefox.muhcannons.blocks.AbstractCannonBarrelBlock
import lonefox.muhcannons.blocks.CannonBlock
import lonefox.muhcannons.items.CannonBallItem
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.mob.CreeperEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.RaycastContext
import net.minecraft.world.World
import kotlin.math.round

class CannonBallEntity(entityType: EntityType<out ProjectileEntity>?, world: World?) :
    ProjectileEntity(entityType, world) {

    constructor(
        world: World?,
        entityDamage: Float,
        piercingValue: Int,
        cannonOwner: CannonBlockEntity,
        x: Double,
        y: Double,
        z: Double
    ) : this(EntitiesRegistry.CANNON_BALL_ENTITY, world) {
        this.entityDamage = entityDamage
        this.integrity = piercingValue
        this.cannonOwner = cannonOwner
        this.noClip = true
        setPos(x, y, z)
    }

    // damage dealt to entities
    private var entityDamage = 2.0f
    private var cannonOwner: CannonBlockEntity? = null
    private val gravity = -0.01
    private var currentAge = 0
    private var integrity = CannonBallItem.PIERCING_VALUE
    private var shouldIgnoreCannonBarrels = true

    override fun initDataTracker() {
    }

    override fun tick() {
        super.tick()

        if(!world.isClient){
            destroyBlocksInPath()
        }

        val velocityMultiplier = if (isTouchingWater) 0.9 else 0.99
        velocity = velocity.multiply(velocityMultiplier)

        checkEntityHit()
        moveForward()
        fall()
        setRotationFromVelocity()
        createTrailParticles()

        if(integrity <= 0){
            world.playSound(null, blockPos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 2.0f, 0.3f)
            discard()
            return
        }

        if(!world.isClient){
            if(integrity <= 0){
                discard()
            }
            age()
        }
    }

    private fun destroyBlocksInPath() {
        var hitResult = getCollidedBlockResult()
        var iterations = 0
        do {
            if(hitResult.type != HitResult.Type.MISS) {
                val blockState = world.getBlockState(hitResult.blockPos)
                if(blockState.block !is AbstractCannonBarrelBlock || !shouldIgnoreCannonBarrels){
                    blockState.onProjectileHit(world, blockState, hitResult, this)
                    destroyBlock(blockState, hitResult.blockPos)
                }
            }else{
                shouldIgnoreCannonBarrels = false
            }
            hitResult = getCollidedBlockResult()
            if(hitResult.type == HitResult.Type.MISS){
                shouldIgnoreCannonBarrels = false
            }
            iterations++
        } while (hitResult.type != HitResult.Type.MISS && iterations < 10 && integrity > 0)
    }

    private fun destroyBlock(blockState: BlockState, blockPos: BlockPos){
        if(!blockState.isAir){
            val block = blockState.block
            val blockHardness = round(block.hardness).toInt()
            if(blockHardness < 0) {
                integrity = 0
                return
            }
            if(block is AbstractCannonBarrelBlock || block is CannonBlock){
                world.createExplosion(this, blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble(), 4.0f, World.ExplosionSourceType.TNT)
                integrity = 0
                return
            }
            if(integrity > blockHardness) {
                world.breakBlock(blockPos, true, this)
                integrity -= blockHardness
            }else{
                integrity = 0
            }
        }
    }

    private fun moveForward() {
        setPosition(x + velocity.x, y + velocity.y, z + velocity.z)
    }

    private fun createTrailParticles() {
        val particle = getTrailParticles()
        if(particle != null){
            for (i in 0..5){
                val partial = i * 0.1
                val dx = MathHelper.lerp(partial, prevX, x)
                val dy = MathHelper.lerp(partial, prevY, y)
                val dz = MathHelper.lerp(partial, prevZ, z)
                val velDeviation = if(i == 0) 0.005 else 0.001
                world.addParticle(particle, dx, dy + 0.5, dz,
                    0.0 + random.nextBetweenExclusive(-5, 5) * velDeviation,
                    0.0 + random.nextBetweenExclusive(-5, 5) * velDeviation,
                    0.0 + random.nextBetweenExclusive(-5, 5) * velDeviation
                )
            }
        }
    }

    private fun age(){
        if(++currentAge >= 200) {
            discard()
        }
    }

    private fun fall(){
        velocity = velocity.add(0.0, gravity, 0.0)
    }

    private fun setRotationFromVelocity(){
        val vec3d = velocity
        if (prevPitch == 0.0f && prevYaw == 0.0f) {
            val d = vec3d.horizontalLength()
            yaw = (MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875).toFloat()
            pitch = (MathHelper.atan2(vec3d.y, d) * 57.2957763671875).toFloat()
            prevYaw = yaw
            prevPitch = pitch
        }
    }

    private fun getTrailParticles(): ParticleEffect? {
        return if(!isTouchingWater) ParticleTypes.CAMPFIRE_SIGNAL_SMOKE else ParticleTypes.BUBBLE
    }

    override fun updateTrackedPositionAndAngles(
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float,
        interpolationSteps: Int,
        interpolate: Boolean
    ) {
        this.setPos(x, y, z)
        this.setRotation(yaw, pitch)
    }

    private fun getCollidedBlockResult(): BlockHitResult {
        /*val direction = pos.subtract(prevPos).normalize()
        val prevPosOffset = prevPos.add(direction.multiply(-0.1))
        val posOffset = pos.add(direction.multiply(0.1))*/
        return world.raycast(RaycastContext(
            pos,
            pos.add(velocity),
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.NONE,
            this
        ))
    }

    override fun onCollision(hitResult: HitResult?) {
        super.onCollision(hitResult)
    }

    private fun checkEntityHit(){
        for (entity in world.getOtherEntities(this, boundingBox.stretch(velocity).expand(1.0))){
            onEntityHit(entity)
        }
    }

    private fun onEntityHit(entity: Entity) {
        val isEnderman = entity.type === EntityType.ENDERMAN
        // todo: add different cannon ball types with fire effect, etc
        val velocityLen = velocity.length().toFloat()
        val damage = MathHelper.ceil(MathHelper.clamp((velocityLen * entityDamage).toDouble(), 0.0, 2.147483647E9)).toFloat()
        val damageSource = ModDamageTypes.of(entity.world, ModDamageTypes.CANNON_BALL_HIT)
        /*val damageSource = damageSources.generic()*/
        if(entity.damage(damageSource, damage)){
            if(isEnderman) return
            val isCreeper = entity.type === EntityType.CREEPER
            if(isCreeper){
                (entity as CreeperEntity).ignite()
            }
            val directionVector = velocity.normalize()
            val punchVector = directionVector.multiply(velocityLen.toDouble())
            if(punchVector.lengthSquared() > 0){
                entity.addVelocity(punchVector)
            }
        }else{
            Muhcannons.logger.info("Entity ${entity.type} cannot be damaged")
        }
    }

    override fun canModifyAt(world: World?, pos: BlockPos?): Boolean {
        return true
    }

    override fun shouldRender(distance: Double): Boolean {
        return distance < 4096.0
    }
}