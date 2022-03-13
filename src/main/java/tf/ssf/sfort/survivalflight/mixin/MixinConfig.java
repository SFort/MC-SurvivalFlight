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
		public static boolean elytraFapi = false;
		public static boolean elytraFapiExists = false;

		public static File confFile = new File(FabricLoader.getInstance().getConfigDir().resolve("SurvivalFlight").toFile(), "general.conf");

		@Override
		public void onLoad(String mixinPackage) {
			try {
				try{
					try{
						Class.forName("net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents", false, this.getClass().getClassLoader());
						elytraFapiExists = true;
					}catch (Exception ignore){}

					if (!confFile.isFile()) return;
					List<String> la = Files.readAllLines(confFile.toPath());
					//TODO add fapi hook as an option

					if (la.size()>18){
						elytraFreeFallFly = la.get(18).contains("true");
						if (la.get(18).contains("fapi") && elytraFapiExists){
							elytraFapi = true;
						}
					}
				}catch (Exception e){ LOGGER.log(Level.INFO, MOD_ID +" #Mixin18\n"+e); }
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
			switch (mixinClassName) {
				case mixin_dir + ".Elytra" : return !elytraFreeFallFly;
				case mixin_dir + ".FallFlyClient" : return elytraFreeFallFly && !elytraFapiExists;
				case mixin_dir + ".FallFly": case mixin_dir + ".FallFlyTick" : return elytraFreeFallFly && !elytraFapi;
				default : return true;
			}
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
