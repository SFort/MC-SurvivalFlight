package tf.ssf.sfort.survivalflight.mixin;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import tf.ssf.sfort.survivalflight.ConfigLoader;
import tf.ssf.sfort.survivalflight.EnumTrueFalseFapi;

import java.util.List;
import java.util.Set;

public class MixinConfig implements IMixinConfigPlugin {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "tf.ssf.sfort.survivalflight";
	public static boolean elytraFreeFallFly = false;
	public static boolean elytraFapi = false;
	public static boolean elytraFapiExists = false;

	@Override
	public void onLoad(String mixinPackage) {
		try {
			try {
				Class.forName("net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents", false, this.getClass().getClassLoader());
				elytraFapiExists = true;
			} catch (Exception ignore) {}

			ConfigLoader.getGeneral(ini -> {
				try {
					EnumTrueFalseFapi en = ini.getEnum("allowGlideWithoutElytra", EnumTrueFalseFapi.class);
					elytraFreeFallFly = en == EnumTrueFalseFapi.TRUE;
					elytraFapi = elytraFapiExists && en == EnumTrueFalseFapi.FAPI;
				} catch (Exception e) {
					LOGGER.log(Level.ERROR, MOD_ID +" failed to load allowGlideWithoutElytra for Mixin, using defaults\n"+e);
				}

			});

		} catch(Exception e) {
			LOGGER.log(Level.ERROR, MOD_ID +" failed to load config file for Mixin, using defaults\n"+e);
		}
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	private static final String mixin_dir = "tf.ssf.sfort.survivalflight.mixin";
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return switch (mixinClassName) {
			case mixin_dir + ".Elytra" -> !elytraFreeFallFly;
			case mixin_dir + ".FallFlyClient" -> elytraFreeFallFly && !elytraFapiExists;
			case mixin_dir + ".FallFly", mixin_dir + ".FallFlyTick" -> elytraFreeFallFly && !elytraFapi;
			default -> true;
		};
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}
}
