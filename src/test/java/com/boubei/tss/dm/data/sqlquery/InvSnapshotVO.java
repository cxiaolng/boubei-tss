package com.boubei.tss.dm.data.sqlquery;

import java.util.Date;
import java.util.List;

import com.boubei.tss.dm.data.sqlquery.AbstractVO;

public class InvSnapshotVO extends AbstractVO {
	 
	public List<String> displayHeaderNames() {
		return null;
	}

	private String whCode;
	private String customerCode;
	private String skuCode;
	private String locationCode;
	private String zoneCode;
	private Double qtyUom;
	private Date day;

	public String getWhCode() {
		return whCode;
	}

	public void setWhCode(String whCode) {
		this.whCode = whCode;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public String getZoneCode() {
		return zoneCode;
	}

	public void setZoneCode(String zoneCode) {
		this.zoneCode = zoneCode;
	}

	public Double getQtyUom() {
		return qtyUom;
	}

	public void setQtyUom(Double qtyUom) {
		this.qtyUom = qtyUom;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}
}