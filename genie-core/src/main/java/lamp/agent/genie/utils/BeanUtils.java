package lamp.agent.genie.utils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.ArrayConverter;
import org.apache.commons.beanutils.converters.StringConverter;

import java.lang.reflect.InvocationTargetException;

public abstract class BeanUtils {

	static {
		BeanUtilsBean.setInstance(new BeanUtilsBean2());

		StringConverter stringConverter = new StringConverter();
		ArrayConverter stringArrayConverter = new ArrayConverter(String[].class, stringConverter);
		stringArrayConverter.setDelimiter(',');
		stringArrayConverter.setOnlyFirstToString(false);
		ConvertUtils.register(stringArrayConverter, String[].class);
	}

	public static String getProperty(Object bean, String name)
		throws IllegalAccessException, InvocationTargetException,
		NoSuchMethodException {

		return org.apache.commons.beanutils.BeanUtils.getProperty(bean, name);
	}

}
