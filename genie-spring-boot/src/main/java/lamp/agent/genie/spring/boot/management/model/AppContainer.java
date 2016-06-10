package lamp.agent.genie.spring.boot.management.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
	@JsonSubTypes.Type(value = SimpleAppContainer.class, name = AppContainerType.Names.SIMPLE),
	@JsonSubTypes.Type(value = SimpleAppContainer.class, name = AppContainerType.Names.SPRING_BOOT),
	@JsonSubTypes.Type(value = SimpleAppContainer.class, name = AppContainerType.Names.DOCKER)
})
public interface AppContainer {
}
