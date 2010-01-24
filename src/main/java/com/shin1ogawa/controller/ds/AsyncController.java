package com.shin1ogawa.controller.ds;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityQuery;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityTranslator;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PbUtil;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.DatastorePb;
import com.google.apphosting.api.ApiProxy.ApiConfig;
import com.google.apphosting.api.ApiProxy.Delegate;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.storage.onestore.v3.OnestoreEntity.EntityProto;

/**
 * クエリを非同期で複数実行する。
 * @author shin1ogawa
 */
public class AsyncController extends Controller {

	private static final String KIND = "OrCombinationTest";


	@Override
	protected Navigation run() {
		try {
			runInternal();
			return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	Navigation runInternal() throws IOException, InterruptedException, ExecutionException {
		super.response.setContentType("text/plain");
		super.response.setCharacterEncoding("utf-8");
		PrintWriter w = super.response.getWriter();
		String q = super.asString("q");
		if (StringUtils.equals(q, "sync2357")) {
			w.println("sync2357");
			sync2357(w);
		} else if (StringUtils.equals(q, "async2357")) {
			w.println("async2357");
			async2357(w);
		} else if (StringUtils.equals(q, "asyncMany")) {
			w.println("asyncMany:" + asInteger("c"));
			asyncMany(w, asInteger("c"));
		} else if (StringUtils.equals(q, "setUp")) {
			w.println("setUp:" + asInteger("c"));
			setUpTestData(w, asInteger("c"));
		}
		response.flushBuffer();
		return null;
	}

	void sync2357(PrintWriter w) {
		EntityQuery q2 =
				Datastore.query(KIND).filter("mod2", FilterOperator.EQUAL, true).offset(0).limit(
						1000).prefetchSize(1000);
		EntityQuery q3 =
				Datastore.query(KIND).filter("mod3", FilterOperator.EQUAL, true).offset(0).limit(
						1000).prefetchSize(1000);
		EntityQuery q5 =
				Datastore.query(KIND).filter("mod5", FilterOperator.EQUAL, true).offset(0).limit(
						1000).prefetchSize(1000);
		EntityQuery q7 =
				Datastore.query(KIND).filter("mod7", FilterOperator.EQUAL, true).offset(0).limit(
						1000).prefetchSize(1000);

		long start = System.currentTimeMillis();
		List<Entity> r3 = q3.asList();
		List<Entity> r5 = q5.asList();
		List<Entity> r7 = q7.asList();
		List<Entity> r2 = q2.asList();
		long end = System.currentTimeMillis();
		@SuppressWarnings("unchecked")
		List<Entity> merged = merge(Arrays.asList(r2, r3, r5, r7));
		w.println("count=" + merged.size() + ", " + (end - start) + "[ms]");
	}

	void async2357(PrintWriter w) throws InterruptedException, ExecutionException {
		Query q2 = new Query(KIND).addFilter("mod2", FilterOperator.EQUAL, true);
		Query q3 = new Query(KIND).addFilter("mod3", FilterOperator.EQUAL, true);
		Query q5 = new Query(KIND).addFilter("mod5", FilterOperator.EQUAL, true);
		Query q7 = new Query(KIND).addFilter("mod7", FilterOperator.EQUAL, true);
		FetchOptions fetchOptions =
				FetchOptions.Builder.withOffset(0).limit(1000).prefetchSize(1000);

		long start = System.currentTimeMillis();
		List<List<Entity>> lists = asyncQuery(Arrays.asList(q2, q3, q5, q7), fetchOptions);
		long end = System.currentTimeMillis();
		w.println("count=" + merge(lists).size() + ", " + (end - start) + "[ms]");
	}

	void asyncMany(PrintWriter w, int count) throws InterruptedException, ExecutionException {
		List<Query> queries = new ArrayList<Query>();
		for (int i = 0; i < count; i++) {
			Query q = new Query(KIND).addFilter("mod2", FilterOperator.EQUAL, true);
			queries.add(q);
		}

		FetchOptions fetchOptions =
				FetchOptions.Builder.withOffset(0).limit(1000).prefetchSize(1000);
		w.println("query count=" + queries.size());

		long start = System.currentTimeMillis();
		List<List<Entity>> lists = asyncQuery(queries, fetchOptions);
		long end = System.currentTimeMillis();
		w.println("count=" + merge(lists).size() + ", " + (end - start) + "[ms]");
	}

	List<Entity> merge(List<List<Entity>> lists) {
		Map<Key, Entity> map = new HashMap<Key, Entity>();
		for (List<Entity> list : lists) {
			for (Entity entity : list) {
				if (map.containsKey(entity.getKey()) == false) {
					map.put(entity.getKey(), entity);
				}
			}
		}
		return new ArrayList<Entity>(map.values());
	}

	/**
	 * 複数のクエリを非同期で実行し、全てのクエリの結果のリストを返す。
	 * <p>マージはしないので、重複を排除する処理は呼び出し元で行うこと。</p>
	 * @param queries
	 * @param fetchOptions
	 * @return 全てのクエリの結果。
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	static List<List<Entity>> asyncQuery(List<Query> queries, FetchOptions fetchOptions)
			throws InterruptedException, ExecutionException {
		@SuppressWarnings("unchecked")
		Delegate<Environment> delegate = ApiProxy.getDelegate();
		Environment env = ApiProxy.getCurrentEnvironment();
		ApiConfig config = new ApiProxy.ApiConfig();
		config.setDeadlineInSeconds(5.0);

		List<Future<byte[]>> futures = new ArrayList<Future<byte[]>>(queries.size());
		for (Query query : queries) {
			futures.add(delegate.makeAsyncCall(env, "datastore_v3", "RunQuery", PbUtil
				.toQueryRequestPb(query, fetchOptions).toByteArray(), config));
		}
		List<List<Entity>> lists = new ArrayList<List<Entity>>();
		for (Future<byte[]> future : futures) {
			DatastorePb.QueryResult rPb = new DatastorePb.QueryResult();
			rPb.mergeFrom(future.get());
			Iterator<EntityProto> it = rPb.resultIterator();
			List<Entity> entities = new ArrayList<Entity>();
			while (it.hasNext()) {
				entities.add(EntityTranslator.createFromPb(it.next()));
			}
			lists.add(entities);
		}
		return lists;
	}

	void setUpTestData(PrintWriter w, int start) {
		List<Entity> list = new ArrayList<Entity>();
		int number = start;
		for (int i = 0; i < 100; i++) {
			Entity e = new Entity(KeyFactory.createKey(KIND, Long.valueOf(number)));
			e.setProperty("mod2", number % 2 == 0);
			e.setProperty("mod3", number % 3 == 0);
			e.setProperty("mod5", number % 5 == 0);
			e.setProperty("mod7", number % 7 == 0);
			number++;
			list.add(e);
		}
		w.println(start + " to " + (number - 1));
		Datastore.put(list);
	}
}
