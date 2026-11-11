package upv.pm.may_ago_26.iti_271503.pgu2_eq01;

public class SystemClock implements Clock {
    @Override
    public long nanoTime() {
        return System.nanoTime();
    }
}
