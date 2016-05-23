package com.boubei.tss.dm.log;

import java.util.Date;

public interface XXService {
	
	@Access(methodName = "report1")
	Object report1(int index, Date day);

}
