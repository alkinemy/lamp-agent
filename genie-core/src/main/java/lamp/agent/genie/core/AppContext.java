package lamp.agent.genie.core;


public interface AppContext {

    String getId();

    void startProcess();

    void stopProcess();

    AppStatus getStatus();

    boolean isProcessRunning();


}
