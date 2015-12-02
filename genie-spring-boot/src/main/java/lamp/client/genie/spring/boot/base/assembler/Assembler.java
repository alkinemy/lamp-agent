package lamp.client.genie.spring.boot.base.assembler;

public interface Assembler<F, T> {

	T assemble(F f);

}
