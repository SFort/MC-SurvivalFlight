package tf.ssf.sfort.survivalflight;

import net.minecraft.util.math.Box;
public interface SPEA {
    void bf$beaconPing(Box box, int duration);
    void bf$fall();
    void bf$fly();
    void bf$tickXP();
    void bf$tickBeacon();
    boolean bf$tickTimed();
    boolean bf$isSurvivalLike();
    boolean bf$hasBeacon();
}
