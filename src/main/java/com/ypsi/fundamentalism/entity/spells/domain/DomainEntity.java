package com.ypsi.fundamentalism.entity.spells.domain;

import com.ypsi.fundamentalism.block.YpsBlocks;
import com.ypsi.fundamentalism.block.custom.DomainBlockEntity;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.entity.ModEntities;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.acetheeldritchking.aces_spell_utils.entity.spells.AbstractDomainEntity;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.acetheeldritchking.aces_spell_utils.utils.AcesSpellUtilsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class DomainEntity extends AbstractDomainEntity implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final AnimationController<DomainEntity> animationController =
            new AnimationController<>(this, "controller", 0, this::predicate);

    private int durationTicks = 20*30;

    private final Map<BlockPos, BlockState> originalBlocks = new HashMap<>();
    private final Map<LivingEntity, Vec3> entityPositions = new HashMap<>();
    private final List<LivingEntity> affectedEntities = new ArrayList<>();
    private boolean building = false;
    private int buildIndex = 0;
    private List<BuildTask> buildTasks = new ArrayList<>();

    private SchoolType schoolType;
    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(DomainEntity.class, EntityDataSerializers.INT);

    private record BuildTask(List<BlockPos> positions, boolean placeBarrier) {}

    public DomainEntity(Level pLevel, Vector3f schoolColor, SchoolType schoolType) {
        super(ModEntities.DOMAIN_ENTITY.get(), pLevel);
        this.schoolType = schoolType;
        setRadius(20);
        setRefinement(10);
        setOpen(false);
        setSpawnAnimTime(40);
        setColor(Utils.packRGB(schoolColor));

    }

    public DomainEntity(EntityType<DomainEntity> domainEntityEntityType, Level level) {
        super(domainEntityEntityType, level);
    }

    public List<BuildTask> getBuildTasks() {return new ArrayList<>(buildTasks);}
    public void setBuildTasks(List<BuildTask> buildTasks) {this.buildTasks = new ArrayList<>(buildTasks);}

    public List<LivingEntity> getAffectedEntities() {return affectedEntities;}
    public Map<LivingEntity, Vec3> getEntityPositions() {return entityPositions;}
    public Map<BlockPos, BlockState> getOriginalBlocks() {return originalBlocks;}

    @Override
    public void onActivation() {
        if (!this.level().isClientSide) {
            this.setSpawnTime(this.level().getGameTime());
        }

        if (!this.level().isClientSide()) {
            ServerChunkCache cache = this.getServer().getLevel(this.level().dimension()).getChunkSource();
            cache.addRegionTicket(TicketType.FORCED, ASUtils.getChunkPos(new BlockPos((int)this.position().x, (int)this.position().y, (int)this.position().z)), 20, ASUtils.getChunkPos(new BlockPos((int)this.position().x, (int)this.position().y, (int)this.position().z)), true);
        }

        this.addClashingMapIfNecessary();
        this.level().getEntitiesOfClass(AbstractDomainEntity.class, new AABB(this.position().
                subtract(this.getRadius()*2, this.getRadius()*2, this.getRadius()*2), this.position().add(this.getRadius()*2, this.getRadius()*2, this.getRadius()*2))).stream()
                .forEach((e) -> {
            if (e.distanceTo(this) < (float)this.getRadius()*2 && !Objects.equals(e, this) && e.getClashable() && (e.getOwner() == null || this.getOwner() == null || !e.getOwner().equals(this.getOwner()))) {
                if ((double)e.getRefinement() / (double)this.getRefinement() >= AcesSpellUtilsConfig.refinementDifference) {
                    this.destroyDomain();
                } else if ((double)this.getRefinement() / (double)e.getRefinement() >= AcesSpellUtilsConfig.refinementDifference) {
                    e.destroyDomain();
                } else if (this.getClashingWith() != null && e.getClashingWith() != null) {
                    if (!this.getClashingWith().contains(e)) {
                        getClashingWith().add(e);
                    }
                    if (!e.getClashingWith().contains(this)) {
                        e.getClashingWith().add(this);
                    }

                    this.setClashing(true);
                    e.setClashing(true);
                }
            }

        });

        Entity owner = getOwner();
        if(owner == null) return;
        if(!owner.level().isClientSide) {
            if(owner instanceof LivingEntity livingOwner) {

                Vec3 barrierPos = getPos(this);
                List<Entity> targets = owner.level().getEntities(
                        null, new AABB(
                                owner.getX() - getRadius(), owner.getY() - getRadius(),
                                owner.getZ() - getRadius(), owner.getX() + getRadius(),
                                owner.getY() + getRadius(), owner.getZ() + getRadius()
                        )
                );

                for (Entity e : targets) {
                    if (e instanceof LivingEntity target) {
                        target.addEffect(new MobEffectInstance(ModEffects.SHOCK_EFFECT, 20 * 10, 0, false, false));
                        affectedEntities.add(target);

                    }
                }
                livingOwner.setDeltaMovement(0,0,0);
                buildTasks = generateBuildTasks(barrierPos, getRadius());
                buildIndex = 0;
                building = true;

                if(isClashing()){
                    Optional<AbstractDomainEntity> oldestDomains = getClashingWith()
                            .stream()
                            .min(Comparator.comparing(AbstractDomainEntity::getSpawnTime));

                    if(oldestDomains.isPresent()) {
                        AbstractDomainEntity oldestDomain = oldestDomains.get();
                        if (oldestDomain.getSpawnTime() < this.getSpawnTime()) { //Another is the oldest
                            //tp this to the one from the list and do not build the barrier
                            Vec3 oldPos = this.position();
                            Vec3 newPos = getPos(oldestDomain);
                            this.moveTo(getPos(oldestDomain));
                            building = false;
                            buildTasks.clear();

                            if(oldestDomain instanceof DomainEntity domainEntity){
                                domainEntity.getAffectedEntities().addAll(this.getAffectedEntities());
                                domainEntity.getEntityPositions().putAll(this.getEntityPositions());
                            }
                            for (LivingEntity target : affectedEntities) {
                                Vec3 offset = target.position().subtract(oldPos);
                                target.moveTo(newPos.add(offset));
                            }

                        } else { //This is the oldest
                            //tp all from the list to this
                            Vec3 thisPos = getPos(this);
                            getClashingWith().forEach(domain -> {
                                Vec3 oldDomainPos = domain.position();
                                domain.moveTo(thisPos);

                                if (domain instanceof DomainEntity domainEntity) {
                                    domainEntity.building = false;
                                    for (LivingEntity target : domainEntity.affectedEntities) {
                                        Vec3 offset = target.position().subtract(oldDomainPos);
                                        target.moveTo(thisPos.add(offset));
                                    }
                                    this.getAffectedEntities().addAll(domainEntity.getAffectedEntities());
                                    this.getEntityPositions().putAll(domainEntity.getEntityPositions());

                                }
                            });

                        }
                    }
                }

            }

        }

    }

    private List<BuildTask> generateBuildTasks(Vec3 center, int radius) {
        List<BuildTask> tasks = new ArrayList<>();
        BlockPos centerPos = BlockPos.containing(center);
        double outerThreshold = radius + 0.2;

        for (int r = 0; r <= radius; r++) {
            List<BlockPos> ring = getHorizontalRing(centerPos, 0, r, radius, outerThreshold, true);
            if (!ring.isEmpty()) {
                tasks.add(new BuildTask(ring, false));
                tasks.add(new BuildTask(ring, true));
            }
        }
        //y -> 1
        for (int y = 1; y <= radius; y++) {
            List<BlockPos> disk = getHorizontalDisk(centerPos, y, radius);
            if (!disk.isEmpty()) {
                tasks.add(new BuildTask(disk, false));
            }
            List<BlockPos> ring = getHorizontalRing(centerPos, y, radius, radius, outerThreshold, false);
            if (!ring.isEmpty()) {
                tasks.add(new BuildTask(ring, true));          // construir solo el anillo exterior
            }
        }
        for (int y = -radius; y <= -1; y++) {
            List<BlockPos> disk = getHorizontalDisk(centerPos, y, radius);
            if (!disk.isEmpty()) {
                tasks.add(new BuildTask(disk, false));            // ahueca el interior
            }
            List<BlockPos> ring = getHorizontalRing(centerPos, y, radius, radius, outerThreshold, false);
            if (!ring.isEmpty()) {
                tasks.add(new BuildTask(ring, true));             // solo el contorno exterior
            }
        }
        return tasks;
    }
    private List<BlockPos> getHorizontalDisk(BlockPos center, int y, int radius) {
        List<BlockPos> list = new ArrayList<>();
        int rsq = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x*x + y*y + z*z <= rsq) {
                    list.add(center.offset(x, y, z));
                }
            }
        }
        list.sort(Comparator.comparingDouble(pos -> pos.distSqr(center)));
        return list;
    }
    private List<BlockPos> getHorizontalRing(BlockPos center, int y, int ringRadius, int totalRadius, double outerThreshold, boolean fullRing) {
        List<BlockPos> list = new ArrayList<>();
        for (int x = -totalRadius; x <= totalRadius; x++) {
            for (int z = -totalRadius; z <= totalRadius; z++) {
                double dist = Math.sqrt(x*x + y*y + z*z);
                if (fullRing) {
                    if (Math.floor(Math.sqrt(x*x + z*z)) == ringRadius && dist <= totalRadius) {
                        list.add(center.offset(x, y, z));
                    }
                } else {
                    if (dist >= totalRadius - 1 && dist <= outerThreshold) {
                        list.add(center.offset(x, y, z));
                    }
                }
            }
        }
        list.sort(Comparator.comparingDouble(pos -> pos.distSqr(center)));
        return list;
    }

    @Override
    public void handleTransportation() {
        if(building) return;
        Entity owner = this.getOwner();
        if(!owner.level().isClientSide) {
            if(owner instanceof LivingEntity) {

                for (int i = 0; i < affectedEntities.size(); i++) {
                    if (affectedEntities.get(i) instanceof LivingEntity target) {
                        entityPositions.put(target, getPos(target));
                        target.removeEffect(ModEffects.SHOCK_EFFECT);
                    }
                }
                super.handleTransportation();
            }
        }

    }

    private Vec3 getPos(Entity entity) {
        double x = entity.getX();
        double z = entity.getZ();
        double y = entity.getY();
        return new Vec3(x, y, z);
    }

    @Override
    public void destroyDomain() {
        building = false;
        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            if(!this.isClashing()) {
                if (!buildTasks.isEmpty())
                    clearBarrierBlocks(serverLevel);
                if (!buildTasks.isEmpty())
                    teleportToOriginalPlaces(serverLevel);
            }else{
                //pass info to other domains
                Optional<AbstractDomainEntity> oldestDomains = getClashingWith()
                        .stream()
                        .min(Comparator.comparing(AbstractDomainEntity::getSpawnTime));
                if(oldestDomains.isPresent()){
                    if(oldestDomains.get() instanceof DomainEntity domainEntity){
                        domainEntity.getAffectedEntities().clear();
                        domainEntity.getAffectedEntities().addAll(this.getAffectedEntities());

                        domainEntity.getEntityPositions().clear();
                        domainEntity.getEntityPositions().putAll(this.getEntityPositions());

                        domainEntity.getOriginalBlocks().clear();
                        domainEntity.getOriginalBlocks().putAll(this.getOriginalBlocks());

                        domainEntity.getBuildTasks().clear();
                        domainEntity.setBuildTasks(this.getBuildTasks());
                    }
                }
            }
        }
        super.destroyDomain();
    }

    public void teleportToOriginalPlaces(ServerLevel serverLevel){
        for (Map.Entry<LivingEntity, Vec3> entry : entityPositions.entrySet()) {
            entry.getKey().moveTo(entry.getValue());
        }
        entityPositions.clear();
    }
    public void clearBarrierBlocks(ServerLevel serverLevel){
        for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
            serverLevel.setBlock(entry.getKey(), entry.getValue(), 3);
        }
        originalBlocks.clear();
    }


    @Override
    public void handleDomainClash(ArrayList<AbstractDomainEntity> opposingDomains) {
        if(!buildTasks.isEmpty() && this.tickCount%10==0){
            ServerLevel serverLevel = (ServerLevel) level();
            for(BuildTask task: buildTasks) {
                for (BlockPos blockPos : task.positions()) {
                    if (!originalBlocks.containsKey(blockPos)) {
                        originalBlocks.put(blockPos, serverLevel.getBlockState(blockPos));
                    }

                    if (task.placeBarrier()) {
                        serverLevel.setBlock(blockPos, YpsBlocks.DOMAIN_BLOCK.get().defaultBlockState(), 3);
                        if (serverLevel.getBlockEntity(blockPos) instanceof DomainBlockEntity be) {
                            int random = this.random.nextInt(0, opposingDomains.size()-1);
                            if(opposingDomains.get(random) instanceof DomainEntity domainEntity) {
                                be.setColor(domainEntity.getColor());
                            }else{
                                be.setColor(this.getColor());
                            }
                        }
                    } else {
                        serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                    }


                }
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if(tickCount % 5 == 0) {
                long actualDuration = level().getGameTime() - getSpawnTime();

                //Discard by time
                if (actualDuration > durationTicks) {
                    destroyDomain();

                }
            }

            if(building && !isRemoved() && tickCount>1){
                for(LivingEntity target: affectedEntities){
                    target.setDeltaMovement(Vec3.ZERO);
                }
                if (buildIndex < buildTasks.size()) {
                    BuildTask task = buildTasks.get(buildIndex);
                    ServerLevel serverLevel = (ServerLevel) level();

                    for (BlockPos blockPos : task.positions()) {
                        if (!originalBlocks.containsKey(blockPos)) {
                            originalBlocks.put(blockPos, serverLevel.getBlockState(blockPos));
                        }

                        if (task.placeBarrier()) {
                            serverLevel.setBlock(blockPos, YpsBlocks.DOMAIN_BLOCK.get().defaultBlockState(), 3);
                            if (serverLevel.getBlockEntity(blockPos) instanceof DomainBlockEntity be) {
                                be.setColor(this.getColor());
                            }
                        } else {
                            serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                        }

                        AABB area = new AABB(blockPos.getX(), blockPos.getY() - 15, blockPos.getZ(),
                                blockPos.getX() + 1, blockPos.getY() + 1 + 15, blockPos.getZ() + 1);
                        List<LivingEntity> blockArea = level().getEntitiesOfClass(LivingEntity.class, area, LivingEntity::isAlive);

                        double safeRadius = getRadius() - 2;
                        Vec3 barrierPos = getPos(this);
                        double BARRIER_Y = barrierPos.y + 1;

                        for(LivingEntity target: blockArea){
                            if(affectedEntities.contains(target)){

                                entityPositions.put(target, getPos(target));

                                Vec3 delta = getPos(target).subtract(barrierPos);
                                double xzDist = Math.sqrt(delta.x * delta.x + delta.z * delta.z);

                                if (xzDist > safeRadius) {
                                    delta = new Vec3(delta.x, 0, delta.z).normalize().scale(safeRadius);
                                }

                                Vec3 safePos = new Vec3(barrierPos.x + delta.x, BARRIER_Y, barrierPos.z + delta.z);
                                target.moveTo(safePos);
                                target.setDeltaMovement(Vec3.ZERO);
                            }
                        }

                    }
                    buildIndex++;
                }else {
                    building = false;
                }
            }

        }

    }

    @Override
    public void targetSureHit() {
        this.level().getEntitiesOfClass(Entity.class,
                new AABB(this.position().subtract(this.getRadius(), this.getRadius(), this.getRadius()), this.position().add(this.getRadius(), this.getRadius(), this.getRadius()))).stream().forEach((e) -> {
            if (e.distanceTo(this) < (float)this.getRadius() && this.canTarget(e)) {
                this.handleSureHit(e);
            }
        });
    }

    @Override
    public void handleSureHit(Entity e) {
        if(e instanceof LivingEntity target){
            Entity owner = this.getOwner();
            if (owner == null) return;

            if(!target.level().isClientSide && tickCount%20==0){
                Holder<DamageType> damageTypeHolder = target.level().registryAccess().holderOrThrow(schoolType.getDamageType());
                DamageSource source = new DamageSource(damageTypeHolder, target, owner);
                DamageSources.applyDamage(target, 8, source);
            }
        }

    }



    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COLOR, 0xFFFFFFFF);
    }

    public int getColor() { return this.entityData.get(COLOR); }
    public void setColor(int color) { this.entityData.set(COLOR, color); }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(animationController);
    }

    private PlayState predicate(AnimationState<DomainEntity> event){
        long time = level().getGameTime() - getSpawnTime();
        if(time < 40) {
            event.getController().setAnimation(RawAnimation.begin().thenPlayAndHold("animation.open"));
        }else{
            event.getController().setAnimation(RawAnimation.begin().thenLoop("animation.open_idle"));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(this.getRadius());
    }

}
