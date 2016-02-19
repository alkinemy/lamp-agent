package lamp.agent.genie.core.runtime.process;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class AppProcessTime {

	long startTime = 0L;
	long user = 0L;
	long sys = 0L;
	long total = 0L;

}
