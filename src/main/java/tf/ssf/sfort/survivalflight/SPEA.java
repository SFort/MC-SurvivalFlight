package tf.ssf.sfort.survivalflight;

import net.minecraft.util.math.Box;
public interface SPEA {
    void bf$beaconPing(Box box, int duration, int level);
    boolean bf$isSurvivalLike();
    int bf$highestLevel();
}
