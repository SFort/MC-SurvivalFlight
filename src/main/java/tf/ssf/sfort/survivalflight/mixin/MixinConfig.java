	package tf.ssf.sfort.survivalflight.mixin;

	import net.fabricmc.loader.api.FabricLoader;
	import org.apache.logging.log4j.Level;
	import org.objectweb.asm.tree.ClassNode;
	import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
	import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

	import java.io.File;
	import java.nio.file.Files;
	import java.util.List;
	import java.util.Set;

	import static tf.ssf.sfort.survivalflight.Config.LOGGER;
	import static tf.ssf.sfort.survivalflight.Config.MOD_ID;

	public class MixinConfig implements IMixinConfigPlugin {

		public static boolean elytraFreeFallFly = false;

		public static File confFile = new File(FabricLoader.getInstance().getConfigDir().resolve("SurvivalFlight").toFile(), "general.conf");

		@Override
		public void onLoad(String mixinPackage) {
			try {
				boolean existing = !confFile.createNewFile();
				List<String> la = Files.readAllLines(confFile.toPath());
				try{
					elytraFreeFallFly = la.get(18).contains("true");
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #Mixin18\n"+e); }
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
				case mixin_dir + ".FallFly", mixin_dir + ".FallFlyClient", mixin_dir + ".FallFlyTick" -> elytraFreeFallFly;
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