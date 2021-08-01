package tf.ssf.sfort.survivalflight.script;

public interface Type {
    default String getType(){return this.getClass().getTypeName();}
}
