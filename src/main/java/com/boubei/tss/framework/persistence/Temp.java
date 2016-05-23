package com.boubei.tss.framework.persistence;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p> Temp.java </p>
 * 临时表，用以批量查询等操作，可取代in 查询。
 *   
  -- Create table
  create global temporary table TBL_TEMP
    (
      ID NUMBER(10) not null, 
      udf1 varchar2(255 char), 
      udf2 varchar2(255 char), 
      udf3 varchar2(255 char)
    )
    on commit delete rows;
  -- Create/Recreate primary, unique and foreign key constraints 
    alter table TBL_TEMP
    add primary key (ID);
 */
@Entity
@Table(name = "TBL_TEMP")
public class Temp implements IEntity {
    
	@Id
	private Long id; 
	
	private String udf1;
	private String udf2;
	private String udf3;
	
	public Temp() { }
	
	public Temp(Long id) {
		this.setId(id);
	}
    
    public Long getId() {
        return id;
    }
 
    public void setId(Long id) {
        this.id = id;
    }

    public String getUdf1() {
        return udf1;
    }

    public void setUdf1(String udf1) {
        this.udf1 = udf1;
    }

    public String getUdf2() {
        return udf2;
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }

    public String getUdf3() {
        return udf3;
    }

    public void setUdf3(String udf3) {
        this.udf3 = udf3;
    }

	public Serializable getPK() {
		return this.getId();
	}
	
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}

	public int hashCode() {
		return this.toString().hashCode();
	}

	public String toString() {
		return "id=" + this.getId();
	}
}

	