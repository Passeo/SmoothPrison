package id.passeo.smoothprison.blockhandle.workload;

public class WhenCompleteWorkload implements Workload {

    private Runnable runnable;

    public WhenCompleteWorkload(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public boolean compute() {
        runnable.run();
        return false;
    }

}
