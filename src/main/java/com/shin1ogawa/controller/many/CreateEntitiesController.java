package com.shin1ogawa.controller.many;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.quota.QuotaService;
import com.google.appengine.api.quota.QuotaServiceFactory;
import com.shin1ogawa.model.ManyEntity;
import com.shin1ogawa.service.ManyEntityService;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

public class CreateEntitiesController extends Controller {

	@Override
	protected Navigation run() {
		try {
			runInternal();
			return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	void runInternal() throws IOException {
		Long startId = asLong("start");
		Integer count = asInteger("count");
		String addTask = asString("addTask");
		if (StringUtils.isNotEmpty(addTask)) {
			addTasks(startId, count);
		} else {
			createEntities(startId, count);
		}
	}

	void createEntities(long startId, int count) throws IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		QuotaService quotaService = QuotaServiceFactory.getQuotaService();
		long start = quotaService.getCpuTimeInMegaCycles();
		List<ManyEntity> entities = ManyEntityService.create(startId, count);
		long end = quotaService.getCpuTimeInMegaCycles();
		double cpuSeconds = quotaService.convertMegacyclesToCpuSeconds(end - start);
		System.out.println("start=" + start + ", end=" + end + ", cpuSeconds=" + cpuSeconds);
		PrintWriter w = response.getWriter();
		for (ManyEntity manyEntity : entities) {
			w.println(ToStringBuilder.reflectionToString(manyEntity));
		}
		response.flushBuffer();
	}

	void addTasks(Long startId, int count) {
		// 100単位で作成するから、端数に誤差が出るけど気にしない。
		for (int i = 0; i < count; i += 100) {
			long id = i + startId;
			System.out.println("start=" + id);
			QueueFactory.getDefaultQueue().add(
					url("/many/createEntities").param("start", String.valueOf(id)).param("count",
							String.valueOf(100)));
		}
	}
}
