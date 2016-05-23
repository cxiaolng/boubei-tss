package com.boubei.tss.portal;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.boubei.tss.cms.helper.ArticleHelperTest;
import com.boubei.tss.cms.helper.ImageProcessorTest;
import com.boubei.tss.cms.job.ArticlePublishTest;
import com.boubei.tss.cms.job.CMSJobsTest;
import com.boubei.tss.cms.lucene.IndexHelperTest;
import com.boubei.tss.cms.module.ArticleModuleTest;
import com.boubei.tss.cms.module.ChannelModuleTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	ArticleModuleTest.class,
	ChannelModuleTest.class,
	IndexHelperTest.class,
	ArticlePublishTest.class,
	CMSJobsTest.class,
	ArticleHelperTest.class,
	ImageProcessorTest.class,
})
public class _AllPortalTests {
 
}
