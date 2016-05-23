package com.boubei.tss.framework.component.cache;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {

    /**
     * cache生命周期
     */
	CacheLife cyclelife() default CacheLife.SHORT;
}
