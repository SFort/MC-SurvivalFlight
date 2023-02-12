package tf.ssf.sfort.survivalflight;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
public interface SPEA {
    void bf$beaconPing(Box box, int duration);
    void bf$conduitPing(BlockPos box, int distance);
    void bf$fall();
    void bf$exit();
    void bf$fly();
    void bf$tickXP();
    void bf$tickBeacon();
    void bf$tickConduit();
    boolean bf$tickTimed();
    boolean bf$isSurvivalLike();
    boolean bf$hasBeaconTicks();
    boolean bf$hasBeaconPing();
    boolean bf$hasConduitTicks();
    boolean bf$hasConduitPing();
}
