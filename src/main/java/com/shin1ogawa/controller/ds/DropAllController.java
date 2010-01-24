package com.shin1ogawa.controller.ds;

import java.util.List;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.quota.QuotaService;
import com.google.appengine.api.quota.QuotaServiceFactory;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

public class DropAllController extends Controller {

	@Override
	protected Navigation run() {
		String kind = asString("kind");
		QuotaService quotaService = QuotaServiceFactory.getQuotaService();
		long start = quotaService.getCpuTimeInMegaCycles();
		List<Key> keyList = Datastore.query(kind).offset(0).limit(100).asKeyList();
		Datastore.delete(keyList);
		if (Datastore.query(kind).offset(0).limit(1).count() > 0) {
			QueueFactory.getDefaultQueue().add(url("/ds/delete").param("kind", kind));
		}
		long end = quotaService.getCpuTimeInMegaCycles();
		double cpuSeconds = quotaService.convertMegacyclesToCpuSeconds(end - start);
		System.out.println("start=" + start + ", end=" + end + ", cpuSeconds=" + cpuSeconds);
		return null;
	}
}
