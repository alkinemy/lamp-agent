package lamp.agent.genie.spring.boot.base.assembler;

import lamp.agent.genie.utils.ArrayUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.GenericTypeResolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SmartAssembler implements ApplicationContextAware {

	private Map<Pair<Class, Class>, Assembler> assemblerMap = new HashMap<>();
	private Map<Pair<Class, Class>, ListAssembler> listAssemblerMap = new HashMap<>();

	private DefaultAssembler defaultAssembler = new DefaultAssembler();

	@Getter
	@Setter
	private boolean parentAutoRegister = true;
	private SmartAssembler parentSmartAssembler;

	public SmartAssembler() {
	}

	public SmartAssembler(SmartAssembler parentSmartAssembler) {
		this.parentSmartAssembler = parentSmartAssembler;
		this.parentAutoRegister = false;
	}

	@Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		String[] assemblerNames = applicationContext.getBeanNamesForType(Assembler.class);
		for (String name : assemblerNames) {
			Assembler assembler = (Assembler) applicationContext.getBean(name);
			Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(assembler.getClass(), Assembler.class);
			Pair<Class, Class> pair = key(classes[0], classes[1]);
			assemblerMap.put(pair, assembler);
			log.info("Assembler {} registered ({}, {})", assembler, classes[0], classes[1]);
		}

		String[] listAssemblerNames = applicationContext.getBeanNamesForType(ListAssembler.class);
		for (String name : listAssemblerNames) {
			ListAssembler assembler = (ListAssembler) applicationContext.getBean(name);
			Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(assembler.getClass(), ListAssembler.class);
			Pair<Class, Class> pair = key(classes[0], classes[1]);
			listAssemblerMap.put(pair, assembler);
			log.info("ListAssembler {} registered ({}, {})", assembler, classes[0], classes[1]);
		}

		if (parentAutoRegister) {
			ApplicationContext parent = applicationContext.getParent();
			if (parent != null) {
				String[] names = applicationContext.getBeanNamesForType(SmartAssembler.class);
				if (ArrayUtils.getLength(names) == 1) {
					this.parentSmartAssembler = parent.getBean(SmartAssembler.class);
					log.info("Parent auto registered : {}", parentSmartAssembler);
				}
			}
		}
	}

	protected Pair<Class, Class> key(Class from, Class to) {
		return new ImmutablePair(from, to);
	}

	public <F, T> T assemble(F from, Class<T> toClass) {
		if (from == null) {
			return null;
		}
		Class<F> fromClass = (Class<F>) from.getClass();
		return assemble(from, fromClass, toClass);
	}

	public <F, T> T assemble(F from, Class<F> fromClass, Class<T> toClass) {
		if (from == null) {
			return null;
		}
		Assembler assembler = assemblerMap.get(key(fromClass, toClass));
		if (assembler != null) {
			return (T) assembler.assemble(from);
		} else if (parentSmartAssembler != null) {
			return parentSmartAssembler.assemble(from, fromClass, toClass);
		} else {
			return defaultAssembler.assemble(from, toClass);
		}
	}

	public <F, T> List<T> assemble(List<F> fromList, Class<T> toClass) {
		if (fromList == null) {
			return null;
		}
		if (fromList.size() == 0) {
			return Collections.emptyList();
		}
		Class<F> fromClass = (Class<F>) fromList.get(0).getClass();
		return assemble(fromList, fromClass, toClass);
	}

	public <F, T> List<T> assemble(List<F> fromList, Class<F> fromClass, Class<T> toClass) {
		if (fromList == null) {
			return null;
		}
		if (fromList.size() == 0) {
			return Collections.emptyList();
		}
		ListAssembler assembler = listAssemblerMap.get(key(fromClass, toClass));
		if (assembler != null) {
			return (List<T>) assembler.assemble(fromList);
		} else if (parentSmartAssembler != null) {
			return parentSmartAssembler.assemble(fromList, fromClass, toClass);
		} else {
			return defaultAssembler.assemble(fromList, toClass);
		}
	}

	//	public <F, T> Page<T> assemble(Pageable pageable, Page<F> fromList, Class<T> toClass) {
	//		if (fromList == null) {
	//			return null;
	//		}
	//		List<T> content = assemble(fromList.getContent(), toClass);
	//		return new PageImpl<>(content, pageable, fromList.getTotalElements());
	//	}
	//
	//	public <F, T> Page<T> assemble(Pageable pageable, Page<F> fromList, Class<F> fromClass, Class<T> toClass) {
	//		if (fromList == null) {
	//			return null;
	//		}
	//		List<T> content = assemble(fromList.getContent(), fromClass, toClass);
	//		return new PageImpl<>(content, pageable, fromList.getTotalElements());
	//	}

}
