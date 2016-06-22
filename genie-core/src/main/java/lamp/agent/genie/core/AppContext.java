package lamp.agent.genie.core;


public interface AppContext {

    String getId();


    void startProcess();

    void stopProcess();


    boolean isProcessRunning();
}
