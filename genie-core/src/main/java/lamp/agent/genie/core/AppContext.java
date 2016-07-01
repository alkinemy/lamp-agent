package lamp.agent.genie.core;

import java.io.IOException;
import java.io.InputStream;

public interface AppContext {

    String getId();

    void startProcess();

    void stopProcess();

    AppStatus getStatus();

    boolean isProcessRunning();

    InputStream getStdOutInputStream() throws IOException;

    InputStream getStdErrInputStream() throws IOException;

}
