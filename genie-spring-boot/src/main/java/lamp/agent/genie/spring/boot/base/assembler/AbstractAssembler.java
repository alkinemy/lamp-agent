package lamp.agent.genie.spring.boot.base.assembler;

public abstract class AbstractAssembler<F, T> implements Assembler<F, T> {

	public final T assemble(F f) {
		if (f == null) {
			return null;
		}
		return doAssemble(f);
	}

	protected abstract T doAssemble(F f);

}
