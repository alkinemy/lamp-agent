package lamp.client.genie.utils;

import java.util.Collection;

public abstract class CollectionUtils { //NOSONAR

    public static boolean isEmpty(final Collection<?> coll) {
        return org.apache.commons.collections4.CollectionUtils.isEmpty(coll);
    }

    public static boolean isNotEmpty(final Collection<?> coll) {
        return !isEmpty(coll);
    }
}
