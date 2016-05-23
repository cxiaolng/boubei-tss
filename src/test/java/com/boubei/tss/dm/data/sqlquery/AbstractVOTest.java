package com.boubei.tss.dm.data.sqlquery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.boubei.tss.dm.data.InvSnapshotVO;

public class AbstractVOTest {
	
	@Test
	public void test() {
		InvSnapshotVO vo = new InvSnapshotVO();
		vo.setWhCode("仓库");
		vo.setCustomerCode("货主");
		vo.setSkuCode("1234");
		vo.setQtyUom(100D);
		vo.setDay(new Date());
		
		System.out.println(vo.displayHeaderNames());
		System.out.println(Arrays.asList(vo.displayFieldValues()));
		
		List<InvSnapshotVO> voList = new ArrayList<InvSnapshotVO>();
		voList.add(vo);
		Assert.assertEquals("仓库", AbstractVO.voList2Objects(voList ).get(0)[0]);
	}

}
