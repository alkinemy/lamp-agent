package lamp.agent.genie.metrics;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;

@Getter
@Setter
@ToString
public class Disk {

    private String name;
    private File path;

}
