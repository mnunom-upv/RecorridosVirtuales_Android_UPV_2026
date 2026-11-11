package upv.pm.may_ago_26.iti_271503.pgu2_eq01;

import android.hardware.*;

public interface SensorEventProvider {
    void start();
    
    void stop();
    
    void registerListener(SensorEventListener p0);
    
    void unregisterListener(SensorEventListener p0);
}
