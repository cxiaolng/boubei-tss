package com.boubei.tss.cache;

import com.boubei.tss.cache.extension.workqueue.TaskPoolCustomizer;

public class ScannerTaskPoolCustomizer extends TaskPoolCustomizer {

	protected String getTaskClass() {
		return ScannerTask.class.getName();
	}

}
