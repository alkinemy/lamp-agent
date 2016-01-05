package lamp.agent.genie.spring.boot;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

public class LampAgentBanner implements Banner {
	private static final String BANNER =
		  " ██████╗ ███████╗███╗   ██╗██╗███████╗\n"
		+ "██╔════╝ ██╔════╝████╗  ██║██║██╔════╝\n"
		+ "██║  ███╗█████╗  ██╔██╗ ██║██║█████╗  \n"
		+ "██║   ██║██╔══╝  ██║╚██╗██║██║██╔══╝  \n"
		+ "╚██████╔╝███████╗██║ ╚████║██║███████╗\n"
		+ " ╚═════╝ ╚══════╝╚═╝  ╚═══╝╚═╝╚══════╝\n"
		+ "                                      ";


	private static final String SPRING_BOOT = " :: Genie (present by joke) :: ";

	private static final int STRAP_LINE_SIZE = 42;

	@Override
	public void printBanner(Environment environment, Class<?> sourceClass,
		PrintStream printStream) {
		printStream.println(BANNER);
		String version = LampAgentBanner.class.getPackage().getImplementationVersion();
		version = (version == null ? "" : " (v" + version + ")");
		String padding = "";
		while (padding.length() < STRAP_LINE_SIZE
			- (version.length() + SPRING_BOOT.length())) {
			padding += " ";
		}

		printStream.println(AnsiOutput.toString(AnsiColor.GREEN, SPRING_BOOT, AnsiColor.DEFAULT, padding,
			AnsiStyle.FAINT, version));
		printStream.println();
	}
}
