	package tf.ssf.sfort.survivalflight;

	import net.fabricmc.api.ModInitializer;
	import net.fabricmc.loader.api.FabricLoader;
	import net.minecraft.entity.effect.StatusEffect;
	import net.minecraft.entity.effect.StatusEffectInstance;
	import net.minecraft.server.network.ServerPlayerEntity;
	import net.minecraft.util.Identifier;
	import net.minecraft.util.registry.SimpleRegistry;
	import org.apache.logging.log4j.Level;
	import org.apache.logging.log4j.LogManager;
	import org.apache.logging.log4j.Logger;
	import tf.ssf.sfort.survivalflight.script.ScriptParser;

	import java.io.File;
	import java.nio.file.Files;
	import java.nio.file.Path;
	import java.util.Arrays;
	import java.util.List;
	import java.util.function.Consumer;
	import java.util.function.Predicate;

	public class Config implements ModInitializer {
		private static final String MOD_ID = "tf.ssf.sfort.survivalflight";
		public static Logger LOGGER = LogManager.getLogger();
		public static StatusEffect exit_effect = null;



        public static int xpPerTick = 0;
		public static int beaconLevel = 0;
		public static int ticksPerXP = 0;
		public static boolean hasBeaconCondition = false;
        public static boolean hasExperianceCondition = false;

		public static Predicate<ServerPlayerEntity> canFly = (player) -> ((SPEA)player).bf$isSurvivalLike();
		public static Consumer<ServerPlayerEntity> exit = (player) -> {};

		public static Consumer<ServerPlayerEntity> tick = (splayer)-> {
			SPEA player = (SPEA) splayer;
			if (Config.canFly.test(splayer)) {
				if (!splayer.getAbilities().allowFlying)
					player.bf$fly();
			} else if (player.bf$isSurvivalLike() && splayer.getAbilities().allowFlying) {
				player.bf$fall();
			}
		};

        @Override
		public void onInitialize() {
			// Configs
			File confFolder = FabricLoader.getInstance().getConfigDirectory();
			File confFile = new File(confFolder, "SurvivalFlight.conf");
			File scriptFile = new File(confFolder, "SurvivalFlightScript.conf");
			File scriptHelpFile = new File(confFolder, "SurvivalFlightScriptHelp.conf");
			try {
				boolean existing = !confFile.createNewFile();
                List<String> la = Files.readAllLines(confFile.toPath());
				String[] ls = la.toArray(new String[Math.max(la.size(), defaultDesc.size() * 2)|1]);
                final int hash = Arrays.hashCode(ls);
				for (int i = 0; i<defaultDesc.size();++i)
					ls[i*2+1]= defaultDesc.get(i);
                int i = 0;
                boolean generateScriptHelp = false;
                try {
					generateScriptHelp =ls[i].contains("true");
				}catch (Exception ignored){}
                if (generateScriptHelp) writeScriptHelp(scriptHelpFile.toPath());
                ls[i]=String.valueOf(generateScriptHelp);
				i+=2;

				try{
				    xpPerTick = Integer.parseInt(ls[i]);
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				ls[i] = String.valueOf(xpPerTick);
				i+=2;

                try{
                    int indx = ls[i].indexOf(".");
                    if(indx != -1)
                        ticksPerXP = (int) Math.round(1/Double.parseDouble("0"+ls[i].substring(indx)));
                    else
                        ticksPerXP = Integer.parseInt(ls[i]);
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
                ls[i] = String.valueOf(ticksPerXP);
                i+=2;

                try{
				    int indx = ls[i].indexOf(";");
                    int duration = Integer.parseInt(ls[i].substring(indx+1));
					exit_effect = SimpleRegistry.STATUS_EFFECT.get(new Identifier(ls[i].substring(0,indx)));
					exit = exit.andThen((player) -> player.addStatusEffect(new StatusEffectInstance(exit_effect, duration)));
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
                i+=2;
				try{
					beaconLevel = Integer.parseInt(ls[i]);
				}catch (Exception e){ if(existing)LOGGER.log(Level.WARN, MOD_ID +" #"+i+"\n"+e); }
				ls[i] = String.valueOf(beaconLevel);

                if (hash != Arrays.hashCode(ls))
				    Files.write(confFile.toPath(), Arrays.asList(ls));
				LOGGER.log(Level.INFO, MOD_ID +" successfully loaded config file");
			} catch(Exception e) {
				LOGGER.log(Level.ERROR, MOD_ID +" failed to load config file, using defaults\n"+e);
			}


            try {
            	if(!scriptFile.createNewFile()) {
					Predicate<ServerPlayerEntity> out = new ScriptParser<ServerPlayerEntity>().ScriptParse(Files.readString(scriptFile.toPath()).replaceAll("\\s", ""), new FlightScript());
					if (out != null)
						canFly = canFly.and(out);
					LOGGER.log(Level.INFO, MOD_ID + " successfully loaded script file");
				}
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, MOD_ID +" failed to load script file\n"+e);
            }
            hasExperianceCondition = ticksPerXP != 0 || xpPerTick != 0;
            if (hasExperianceCondition && hasBeaconCondition){
            	tick = (splayer)->{
            		SPEA player = (SPEA) splayer;
					if (Config.canFly.test(splayer)) {
						player.bf$tickXP();
						player.bf$checkBeacon();
					} else if (player.bf$isSurvivalLike() && splayer.getAbilities().allowFlying) {
						player.bf$fall();
					}
					player.bf$tickBeacon();
				};
			}else if (hasExperianceCondition){
				tick = (splayer)-> {
					SPEA player = (SPEA) splayer;
					if (Config.canFly.test(splayer)) {
						player.bf$tickXP();
					} else if (player.bf$isSurvivalLike() && splayer.getAbilities().allowFlying) {
						player.bf$fall();
					}
				};
			}else if (hasBeaconCondition){
				tick = (splayer)-> {
					SPEA player = (SPEA) splayer;
					if (Config.canFly.test(splayer)) {
						if (!splayer.getAbilities().allowFlying)
							player.bf$fly();
						player.bf$checkBeacon();
					} else if (player.bf$isSurvivalLike() && splayer.getAbilities().allowFlying) {
						player.bf$fall();
					}
					player.bf$tickBeacon();
				};
			}
        }

        public static final List<String> defaultDesc = Arrays.asList(
                "^-Generate Script Help? [true] true | false",
                "^-Xp consumed per tick [0] 0 - ..",
                "^-Consume 1XP per X ticks [0] 0 - .. // if you prefer decimals/tick putting in for e.g.: 0.2 xp/t will auto translate",
                "^-Apply effect to player on mid-flight condition failure [] EffectID;tick_duration //e.g. slow_falling;20",
				"^-Required beacon level for beacon setting [0] 1-4"
		);
		public final String scriptHelp = """
				I decided to burn a day and make this mod stupidly configurable.
				Lines are ignored.
				Available operations:
				"""+ ScriptParser.getHelp()+
				"\nAvailable Conditions:\n"+
				FlightScript.getHelp()
				;
		private void writeScriptHelp(Path path){
			try {
				Files.writeString(path, scriptHelp);
			}catch (Exception e){ LOGGER.log(Level.WARN, MOD_ID +" #0\n"+e); }
		}
	}
