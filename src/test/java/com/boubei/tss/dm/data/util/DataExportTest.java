package com.boubei.tss.dm.data.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.boubei.tss.dm.TxTestSupport4DM;
import com.boubei.tss.dm.data.InvSnapshotVO;
import com.boubei.tss.dm.data.sqlquery.AbstractExportSO;

public class DataExportTest extends TxTestSupport4DM {
	
	@Test
	public void test() {
		List<InvSnapshotVO> voList = new ArrayList<InvSnapshotVO>();

		InvSnapshotVO temp = new InvSnapshotVO();
		temp.setWhCode("W1");
		temp.setCustomerCode("C1");
		temp.setLocationCode("L1");
		temp.setSkuCode("SKU1");
		temp.setQtyUom(10D);
		voList.add(temp);

		temp = new InvSnapshotVO();
		temp.setWhCode("W1");
		temp.setCustomerCode("C1");
		temp.setLocationCode("L1");
		temp.setSkuCode("SKU2");
		temp.setQtyUom(10D);
		voList.add(temp);
		
		AbstractExportSO so = new AbstractExportSO() {
			private static final long serialVersionUID = 1L;
			public String[] getParameterNames() {
				return null;
			}
			public String getExportFileName() {
				return "Test-" + System.currentTimeMillis() + ".csv";
			}
		};
		
		Map<String, Object> result = DataExport.getDataByPage(voList, 1, 12);
		Assert.assertEquals(2, result.get("total"));
		
		DataExport.exportCSV(voList, so);
		
		List<String> cnFields = Arrays.asList("h1", "h2");
		Object[][] data = new Object[2][2];
		DataExport.exportCSVII("123.csv", data , cnFields);
		
		String tmpDir = System.getProperty("java.io.tmpdir") + "temp";
		DataExport.exportCSV(tmpDir + "/456.csv", "h1,h2\n1,2");
		
		List<Object[]> data2 = new ArrayList<Object[]>();
		data2.add(new Object[] { 1, 2 } );
		DataExport.exportCSV(data2, cnFields); 
	}
}

