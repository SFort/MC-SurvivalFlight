package tf.ssf.sfort.survivalflight;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import tf.ssf.sfort.script.Default;
import tf.ssf.sfort.script.ScriptingScreen;

public class ConfigScreen extends Screen {
    final Screen screen;
    ConfigScreen(Screen parent) {
        super(Text.of("Survival Flight"));
        this.screen = parent;
    }
    @Override
    public void init(){
        addDrawableChild(new ButtonWidget(width/2-75, height/2-30, 150, 20, Text.of("Survival Flight Script"),
                p ->{
                    client.setScreen(new ScriptingScreen(
                            Text.of("Survival Flight Script"),
                            this,
                            new ScriptingScreen.Script(
                                    "§bSurvival Flight",
                                    Default.SERVER_PLAYER_ENTITY,
                                    Config::writeFly,
                                    null,
                                    Config::readFly,
                                    ScriptingScreen.getDefaultEmbed()
                            )
                    ));
                }));
        addDrawableChild(new ButtonWidget(width/2-75, height/2-10, 150, 20, Text.of("Elytra Flight Script"),
                p ->{
                    client.setScreen(new ScriptingScreen(
                            Text.of("Elytra Flight Script"),
                            this,
                            new ScriptingScreen.Script(
                                    "§bSurvival Flight - Elytra Flight",
                                    Default.SERVER_PLAYER_ENTITY,
                                    Config::writeElytra,
                                    null,
                                    Config::readElytra,
                                    ScriptingScreen.getDefaultEmbed()
                            )
                    ));
                }));
        addDrawableChild(new ButtonWidget(width/2-75, height/2+10, 150, 20, Text.of("Firework Boost Script"),
                p ->{
                    client.setScreen(new ScriptingScreen(
                            Text.of("Survival Flight Script"),
                            this,
                            new ScriptingScreen.Script(
                                    "§bSurvival Flight - Firework Boost",
                                    Default.SERVER_PLAYER_ENTITY,
                                    Config::writeElytraBoost,
                                    null,
                                    Config::readElytraBoost,
                                    ScriptingScreen.getDefaultEmbed()
                            )
                    ));
                }));
    }
    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        int x = width/2-130;
        int y = height/2-80;
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, delta);
        textRenderer.drawWithShadow(matrix,"UI doesn't allow editing non-scripting options yet", width/2-123, height/2-70, -1);
        textRenderer.drawWithShadow(matrix,".minecraft/config/SurvivalFlight/general.conf", width/2-115, height/2-50, -1);
        fill(matrix, x, y, x+260, y+1, -1);
        fill(matrix, x, y, x+1, y+130, -1);
        fill(matrix, x, y+130-1, x+260, y+130, -1);
        fill(matrix, x+260-1, y, x+260, y+130, -1);
    }
    @Override
    public void close(){
        client.setScreen(screen);
    }
}
