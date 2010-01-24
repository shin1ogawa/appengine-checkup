package com.shin1ogawa;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.apphosting.api.ApiProxy;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

public class DatastoreTest {

	@Test
	public void all() {
		List<Entity> list = Datastore.query("c").filter("prop", FilterOperator.EQUAL, "b").asList();
		assertThat(list.size(), is(equalTo(2)));
	}

	@Test
	public void ancestor() {
		List<Entity> list =
				Datastore.query("c", KeyFactory.createKey("p", "p2")).filter("prop",
						FilterOperator.EQUAL, "b").asList();
		assertThat(list.size(), is(equalTo(1)));
	}

	/**
	 * 同じカインド内でキー名を重複させる事ができる。
	 */
	@Test
	public void keyname() {
		Datastore.delete(Datastore.query("p").asKeyList());
		Datastore.delete(Datastore.query("c").asKeyList());
		Entity p1 = new Entity("p", "name1");
		Entity p2 = new Entity("p", "name2");
		Entity p1c1 = new Entity("c", "namec", p1.getKey());
		Entity p2c1 = new Entity("c", "namec", p2.getKey());
		Datastore.put(p1, p2, p1c1, p2c1);
		List<Entity> list = Datastore.query("c").asList();
		for (Entity entity : list) {
			System.out.println(entity.getKey());
		}
	}

	@Test
	public void ancestorQuery() {
		EnableAncestorQueryDelegate delegate = new EnableAncestorQueryDelegate();
		try {
			ApiProxy.setDelegate(delegate);
			Key ancestorKey = KeyFactory.createKey("p", "p1");
			List<Entity> list1 = Datastore.query(ancestorKey).asList();
			System.out.println(list1.size());
			List<Entity> list2 = Datastore.query("c", ancestorKey).asList();
			System.out.println(list2.size());
		} finally {
			ApiProxy.setDelegate(delegate.before);
		}
	}

	@Before
	public void setUp() {
		TestUtil.setUpAppEngine(new File("target"));

		Datastore.delete(Datastore.query("p").asKeyList());
		Datastore.delete(Datastore.query("c").asKeyList());

		Entity p1 = new Entity("p", "p1");
		Entity c1a = new Entity("c", p1.getKey());
		c1a.setProperty("prop", "a");
		Entity c1b = new Entity("c", p1.getKey());
		c1b.setProperty("prop", "b");
		Entity c1c = new Entity("c", p1.getKey());
		c1c.setProperty("prop", "c");

		Entity p2 = new Entity("p", "p2");
		Entity c2a = new Entity("c", p2.getKey());
		c2a.setProperty("prop", "a");
		Entity c2b = new Entity("c", p2.getKey());
		c2b.setProperty("prop", "b");
		Entity c2c = new Entity("c", p2.getKey());
		c2c.setProperty("prop", "c");

		Datastore.put(Arrays.asList(p1, c1a, c1b, c1c, p2, c2a, c2b, c2c));
	}

	@After
	public void teawDown() {
		TestUtil.tearDownAppEngine();
	}
}
