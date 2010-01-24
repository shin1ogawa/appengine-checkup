package com.shin1ogawa.service;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import com.shin1ogawa.TestUtil;
import com.shin1ogawa.meta.ManyEntityMeta;

public class ManyEntityServiceTest {

	@Test
	public void test() {
		ManyEntityService.create(1, 100);
	}

	@Before
	public void setUp() {
		TestUtil.setUpAppEngine(new File("target"));

		Datastore.delete(Datastore.query(ManyEntityMeta.get()).asKeyList());
	}

	@After
	public void teawDown() {
		TestUtil.tearDownAppEngine();
	}
}
