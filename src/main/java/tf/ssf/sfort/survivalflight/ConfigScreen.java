package tf.ssf.sfort.survivalflight;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import tf.ssf.sfort.script.ScriptingScreen;

public class ConfigScreen extends Screen {
    final Screen screen;
    ConfigScreen(Screen parent) {
        super(new LiteralText("Survival Flight"));
        this.screen = parent;
    }
    @Override
    public void init(){
        addButton(new ButtonWidget(width/2-75, height/2-30, 150, 20, new LiteralText("Survival Flight Script"),
                p ->{
                    client.openScreen(new ScriptingScreen(
                            new LiteralText("Survival Flight Script"),
                            this,
                            new ScriptingScreen.Script(
                                    "§bSurvival Flight",
                                    new FlightScript(),
                                    Config::writeFly,
                                    null,
                                    Config::readFly,
                                    ScriptingScreen.getDefaultEmbed()
                            )
                    ));
                }));
        addButton(new ButtonWidget(width/2-75, height/2-10, 150, 20, new LiteralText("Elytra Flight Script"),
                p ->{
                    client.openScreen(new ScriptingScreen(
                            new LiteralText("Elytra Flight Script"),
                            this,
                            new ScriptingScreen.Script(
                                    "§bSurvival Flight - Elytra Flight",
                                    new FlightScript(),
                                    Config::writeElytra,
                                    null,
                                    Config::readElytra,
                                    ScriptingScreen.getDefaultEmbed()
                            )
                    ));
                }));
        addButton(new ButtonWidget(width/2-75, height/2+10, 150, 20, new LiteralText("Firework Boost Script"),
                p ->{
                    client.openScreen(new ScriptingScreen(
                            new LiteralText("Survival Flight Script"),
                            this,
                            new ScriptingScreen.Script(
                                    "§bSurvival Flight - Firework Boost",
                                    new FlightScript(),
                                    Config::writeElytraBoost,
                                    null,
                                    Config::readElytraBoost,
                                    ScriptingScreen.getDefaultEmbed()
                            )
                    ));
                }));

    }
    @Override
    public void onClose(){
        client.openScreen(screen);
    }
}
