package tf.ssf.sfort.survivalflight;

import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;
import tf.ssf.sfort.ini.SFIni;
import tf.ssf.sfort.survivalflight.mixin.MixinConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigLoader {
	public static Path oldLegacyConfFolder = FabricLoader.getInstance().getConfigDir();
	public static File oldLegacyConfFile = new File(oldLegacyConfFolder.toFile(), "SurvivalFlight.conf");
	public static File oldLegacyScriptFile = new File(oldLegacyConfFolder.toFile(), "SurvivalFlightScript.conf");
	public static File oldLegacyScriptHelpFile = new File(oldLegacyConfFolder.toFile(), "SurvivalFlightScriptHelp.conf");
	public static Path confFolder = FabricLoader.getInstance().getConfigDir().resolve("SurvivalFlight");
	public static File legacyConfFile = new File(confFolder.toFile(), "general.conf");
	public static File confFile = new File(confFolder.toFile(), "general.sf.ini");
	public static File scriptFile = new File(confFolder.toFile(), "creative_flight.script");

	public static void getGeneral(Consumer<SFIni> loadIni) {
		if(!confFolder.toFile().isDirectory()){
			if(confFolder.toFile().mkdirs()){
				oldLegacyScriptHelpFile.delete();
				oldLegacyConfFile.renameTo(legacyConfFile);
				oldLegacyScriptFile.renameTo(scriptFile);
			}
		}
		SFIni defIni = new SFIni();
		defIni.load(String.join("\n", new String[]{
				"; Generate Script Help? [true] true | ludicrous | false",
				";  note that some values are only available while in a world you can get them by running /ssf load survivalflight then disabling this setting",
				"genScriptHelp=true",
				"; Xp consumed per tick [0] 0+",
				"xpPerTick=0",
				"; Consume 1XP per X ticks [0] 0+",
				";  if you prefer decimals/tick putting in for e.g.: 0.2 xp/t, it will auto translate",
				"ticksPerXp=0",
				"; Apply effect to player on mid-flight condition failure [] EffectID;tick_duration e.g.: slow_falling;20",
				"flightFailEffect=",
				"; Required beacon level for beacon setting [0] 1-4",
				"minBeaconLevel=0",
				"; Add reload settings command [true] true | false (game restart required)",
				"addCommands=true",
				"; Flight duration in ticks before cool-down starts [0] 0+",
				"flightDurationLimit=0",
				"; Flight cool-down in ticks [0] 0+",
				"flightCooldown=0",
				"; Apply effect to player on elytra mid-flight condition failure [] EffectID;tick_duration e.g.: slow_falling;20",
				"elytraFlightFailEffect=",
				"; Allow gliding without an elytra [false] true | false | fapi",
				"; WARNING disables vanilla elytra and Requires mod to be installed on client and server. (also needs game restart)",
				"allowGlideWithoutElytra=false",
				"; Enable PlayerAbilityLib compatibility [true] true | false",
				"playerAbilityLibCompat=true",
				"; Adds a command to set scripts [true] true | false",
				"allowWritingServerConfigs=true",
		}));
		loadLegacy(defIni);

		if (!confFile.exists()) {
			try {
				Files.write(confFile.toPath(), defIni.toString().getBytes());
				MixinConfig.LOGGER.log(Level.INFO, MixinConfig.MOD_ID+" successfully created config file");
				loadIni.accept(defIni);
			} catch (IOException e) {
				MixinConfig.LOGGER.log(Level.ERROR, MixinConfig.MOD_ID+" failed to create config file, using defaults", e);
			}
			return;
		}
		try {
			SFIni ini = new SFIni();
			String text = Files.readString(confFile.toPath());
			int hash = text.hashCode();
			ini.load(text);
			for (Map.Entry<String, List<SFIni.Data>> entry : defIni.data.entrySet()) {
				List<SFIni.Data> list = ini.data.get(entry.getKey());
				if (list == null || list.isEmpty()) {
					ini.data.put(entry.getKey(), entry.getValue());
				} else {
					list.get(0).comments = entry.getValue().get(0).comments;
				}
			}
			loadIni.accept(ini);
			String iniStr = ini.toString();
			if (hash != iniStr.hashCode()) {
				Files.write(confFile.toPath(), iniStr.getBytes());
			}
		} catch (IOException e) {
			MixinConfig.LOGGER.log(Level.ERROR, MixinConfig.MOD_ID+" failed to load config file, using defaults", e);
		}
	}
	public static void loadLegacy(SFIni inIni) {
		Map<String, String> oldConf = new HashMap<>();
		if (!legacyConfFile.exists()) return;
		try {
			List<String> la = Files.readAllLines(legacyConfFile.toPath());
			String[] ls = la.toArray(new String[Math.max(la.size(), 24)|1]);

			try{
				oldConf.put("genScriptHelp", ls[0].contains("true") ? "true" : ls[0].contains("ludicrous") ? "ludicrous" : "false");
			}catch (Exception ignore){}
			try {
				oldConf.put("xpPerTick", Integer.toString(Integer.parseInt(ls[2])));
			} catch (Exception ignore){}
			try {
				oldConf.put("ticksPerXp", Integer.toString(Integer.parseInt(ls[4])));
			} catch (Exception ignore){}
			try {
				if (!ls[6].contains("null")) {
					oldConf.put("flightFailEffect", ls[6]);
				}
			} catch (Exception ignore){}
			try {
				oldConf.put("minBeaconLevel", Integer.toString(Integer.parseInt(ls[8])));
			} catch (Exception ignore){}
			try {
				oldConf.put("addCommands", !ls[10].contains("false") ? "true" : "false");
			} catch (Exception ignore){}
			try {
				oldConf.put("flightDurationLimit", Integer.toString(Integer.parseInt(ls[12])));
			} catch (Exception ignore){}
			try {
				oldConf.put("flightCooldown", Integer.toString(Integer.parseInt(ls[14])));
			} catch (Exception ignore){}
			try {
				if (!ls[6].contains("null")) {
					oldConf.put("elytraFlightFailEffect", ls[16]);
				}
			} catch (Exception ignore){}
			try {
				oldConf.put("allowGlideWithoutElytra", ls[18].contains("true") ? "true" : ls[18].contains("fapi") ? "fapi" : "false");
			} catch (Exception ignore){}
			try {
				oldConf.put("playerAbilityLibCompat", !ls[20].contains("false") ? "true" : "false");
			} catch (Exception ignore){}

			for (Map.Entry<String, String> entry : oldConf.entrySet()) {
				SFIni.Data data = inIni.getLastData(entry.getKey());
				if (data != null) {
					data.val = entry.getValue();
				}
			}

			Files.delete(legacyConfFile.toPath());
			MixinConfig.LOGGER.log(Level.INFO, MixinConfig.MOD_ID+" successfully loaded legacy .conf file");
		} catch(Exception e) {
			MixinConfig.LOGGER.log(Level.ERROR, MixinConfig.MOD_ID+" failed to load legacy .conf file", e);
		}
	}
}