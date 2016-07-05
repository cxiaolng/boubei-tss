package com.boubei.tss.dm;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.boubei.tss.dm.data.DataSourceManagerTest;
import com.boubei.tss.dm.data.WashDataJobTest;
import com.boubei.tss.dm.data.sqlquery.AbstractVOTest;
import com.boubei.tss.dm.data.sqlquery.SOUtilTest;
import com.boubei.tss.dm.data.sqlquery.SQLExcutorH2Test;
import com.boubei.tss.dm.data.sqlquery.SqlConfigTest;
import com.boubei.tss.dm.data.util.DataExportTest;
import com.boubei.tss.dm.data.util.GroupByUtilTest;
import com.boubei.tss.dm.ext.SyncUserRoleJobTest;
import com.boubei.tss.dm.log.AccessLogTest;
import com.boubei.tss.dm.other.Json2ListTest;
import com.boubei.tss.dm.other.RegTest;
import com.boubei.tss.dm.record.BuildTableTest;
import com.boubei.tss.dm.record.ImportCSVTest;
import com.boubei.tss.dm.record.RecordTest;
import com.boubei.tss.dm.record._DatabaseTest;
import com.boubei.tss.dm.record._RecorderTest;
import com.boubei.tss.dm.report.DataServiceListTest;
import com.boubei.tss.dm.report.ReportActionTest;
import com.boubei.tss.dm.report.ScriptParseTest;
import com.boubei.tss.dm.report.QueryCacheInterceptorTest;
import com.boubei.tss.dm.report._ReporterTest;
import com.boubei.tss.dm.report.timer.ReportJobTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	DMUtilTest.class,
	Json2ListTest.class,
	RegTest.class,
	AccessLogTest.class,
	_ReporterTest.class,
	DataServiceListTest.class,
	ReportActionTest.class,
	ScriptParseTest.class,
	QueryCacheInterceptorTest.class,
	ReportJobTest.class,
	_DatabaseTest.class,
	_RecorderTest.class,
	BuildTableTest.class,
	ImportCSVTest.class,
	RecordTest.class,
	SyncUserRoleJobTest.class,
	DataSourceManagerTest.class,
	WashDataJobTest.class,
	AbstractVOTest.class,
	SOUtilTest.class,
	SqlConfigTest.class,
	SQLExcutorH2Test.class,
	DataExportTest.class,
	GroupByUtilTest.class
})
public class _AllDMTests {
 
}
