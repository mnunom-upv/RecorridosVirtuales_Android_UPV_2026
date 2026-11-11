package upv.pm.may_ago_26.iti_271503.pgu2_eq02;


import android.hardware.SensorEventListener;

public interface SensorEventProvider {
    void start();
    
    void stop();
    
    void registerListener(SensorEventListener p0);
    
    void unregisterListener(SensorEventListener p0);
}
