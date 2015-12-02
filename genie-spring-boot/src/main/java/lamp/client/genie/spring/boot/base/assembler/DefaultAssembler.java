package lamp.client.genie.spring.boot.base.assembler;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.ArrayList;
import java.util.List;

public class DefaultAssembler {

	private ModelMapper modelMapper;

	public DefaultAssembler() {
		modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	}

	public <T, F> T assemble(F from, Class<T> toClass) {
		return modelMapper.map(from, toClass);
	}

	public <T, F> List<T> assemble(List<F> fromList, Class<T> toClass) {
		List<T> list = new ArrayList<>();
		for (F f : fromList) {
			list.add(assemble(f, toClass));
		}
		return list;
	}

}
