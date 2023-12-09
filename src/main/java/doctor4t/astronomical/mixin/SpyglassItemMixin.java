package doctor4t.astronomical.mixin;

import doctor4t.astronomical.cca.AstraCardinalComponents;
import doctor4t.astronomical.cca.world.AstraSkyComponent;
import doctor4t.astronomical.common.init.ModSoundEvents;
import doctor4t.astronomical.common.structure.CelestialObject;
import doctor4t.astronomical.common.structure.InteractableStar;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpyglassItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import static doctor4t.astronomical.common.Astronomical.rotateViaQuat;

@Mixin(SpyglassItem.class)
public abstract class SpyglassItemMixin extends Item {
	public SpyglassItemMixin(Settings settings) {
		super(settings);
	}

	private float astra$getSkyAngle(long time) {
		double d = MathHelper.fractionalPart((double) time / 24000.0 - 0.25);
		double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
		return (float) (d * 2.0 + e) / 3.0F;
	}

	public float getStarBrightness(World w) {
		float f = w.getSkyAngle(1);
		float g = 1.0F - (MathHelper.cos(f * (float) (Math.PI * 2)) * 2.0F + 0.25F);
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		return g * g * 0.5F;
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (getStarBrightness(world) <= 0) {
			return;
		}

		Quaternion rotation = Vec3f.POSITIVE_Z.getDegreesQuaternion(world.getSkyAngle(1) * 360.0F);
		//rotation.normalize();
		Vec3d vec = user.getRotationVector();
		AstraSkyComponent c = world.getComponent(AstraCardinalComponents.SKY);
		CelestialObject supernovad = null;
		for (CelestialObject obj : c.getCelestialObjects().stream().filter(g -> g.canInteract() && !((InteractableStar) g).subjectForTermination).toList()) {
			Vec3d rotatedVec = rotateViaQuat(obj.getDirectionVector(), rotation).normalize();
			double h = vec.x * rotatedVec.x + vec.y * rotatedVec.y + vec.z * rotatedVec.z;
			if (h > 0.9999) {
				if (rotatedVec.dotProduct(new Vec3d(0, 1, 0)) > -0.2) {
					if (world instanceof ServerWorld serverWorld) {
						((InteractableStar) obj).crossFire = (k) -> {
							serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), ModSoundEvents.STAR_FALL, SoundCategory.AMBIENT, 20f, 1f);

							for (int i = 0; i <= world.random.nextInt(3); i++) {
								Vec3d pos = user.getPos().add(world.random.nextGaussian() * 80, 0, world.random.nextGaussian() * 80);
								world.getComponent(AstraCardinalComponents.FALL).addFall(10 + world.random.nextInt(11), rotateViaQuat(obj.getDirectionVector(), Vec3f.POSITIVE_Z.getDegreesQuaternion(k.getSkyAngle(1) * 360.0F)).normalize(), new Vec3d(pos.x, world.getTopY(Heightmap.Type.MOTION_BLOCKING, MathHelper.floor(pos.x), MathHelper.floor(pos.z)), pos.z));
							}
						};
						supernovad = obj;
						break;
					}
				}
			}
		}
		if (supernovad != null) {
			((InteractableStar) supernovad).subjectForTermination = true;
			AstraCardinalComponents.SKY.sync(world);
		}
	}
}
