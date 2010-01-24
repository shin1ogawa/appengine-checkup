package com.shin1ogawa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.DatastorePb;
import com.google.apphosting.api.ApiProxy.ApiConfig;
import com.google.apphosting.api.ApiProxy.ApiProxyException;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.api.ApiProxy.LogRecord;
import com.google.apphosting.api.DatastorePb.GetSchemaRequest;
import com.google.apphosting.api.DatastorePb.QueryResult;
import com.google.apphosting.api.DatastorePb.Schema;
import com.google.storage.onestore.v3.OnestoreEntity.EntityProto;
import com.google.storage.onestore.v3.OnestoreEntity.Path;
import com.google.storage.onestore.v3.OnestoreEntity.Reference;
import com.google.storage.onestore.v3.OnestoreEntity.Path.Element;

/**
 * kindless ancestor queryをローカルでも動作させる{@link ApiProxy.Delegate}の実装.
 * @author shin1ogawa
 */
public class EnableAncestorQueryDelegate implements ApiProxy.Delegate<Environment> {

	@SuppressWarnings("unchecked")
	ApiProxy.Delegate<Environment> before = ApiProxy.getDelegate();


	public byte[] makeSyncCall(Environment env, String service, String method, byte[] request)
			throws ApiProxyException {
		if (service.equals("datastore_v3") == false || method.equals("RunQuery") == false) {
			return before.makeSyncCall(env, service, method, request);
		}
		DatastorePb.Query requestPb = new DatastorePb.Query();
		requestPb.mergeFrom(request);
		if (requestPb.getAncestor() == null || requestPb.getKind().isEmpty() == false) {
			return before.makeSyncCall(env, service, method, request);
		}
		// kindless ancestor queryの時は全てのKindに対してancestor queryを実行する。
		String[] kinds = getKinds();
		// ancestor keyのpbからancestorのkindを取得し、最初はancestor kindから実行する。
		Reference ancestor = requestPb.getAncestor();
		String ancestorKind = ancestor.getPath().getElement(0).getType();
		QueryResult resultOfAncestor =
				runAncestorQuery(env, service, method, ancestorKind, requestPb);
		resultOfAncestor.setMoreResults(true);
		for (String kind : kinds) {
			if (kind.equals(ancestorKind) == false) {
				QueryResult result = runAncestorQuery(env, service, method, kind, requestPb);
				Iterator<EntityProto> i = result.resultIterator();
				while (i.hasNext()) {
					EntityProto next = i.next();
					resultOfAncestor.addResult(next);
				}
			}
		}
		return resultOfAncestor.toByteArray();
	}

	DatastorePb.QueryResult runAncestorQuery(Environment env, String service, String method,
			String kind, DatastorePb.Query requestPb) {
		requestPb.setKind(kind);
		// ancestorのkindを設定したrequestPbをbyte[]にする。
		byte[] requestbytes = requestPb.toByteArray();
		DatastorePb.QueryResult result = new DatastorePb.QueryResult();
		result.mergeFrom(before.makeSyncCall(env, service, method, requestbytes));
		return result;
	}

	public void log(Environment env, LogRecord logRecord) {
		before.log(env, logRecord);
	}

	public Future<byte[]> makeAsyncCall(Environment env, String service, String method,
			byte[] request, ApiConfig config) {
		return before.makeAsyncCall(env, service, method, request, config);
	}

	String[] getKinds() {
		LocalDatastoreService datastoreService =
				(LocalDatastoreService) ((ApiProxyLocalImpl) before).getService("datastore_v3");
		Schema schema =
				datastoreService.getSchema(null, new GetSchemaRequest().setApp(ApiProxy
					.getCurrentEnvironment().getAppId()));
		List<EntityProto> entityProtoList = schema.kinds();
		List<String> kindList = new ArrayList<String>(entityProtoList.size());
		for (EntityProto entityProto : entityProtoList) {
			List<?> path = entityProto.getKey().getPath().elements();
			Element element = (Element) path.get(path.size() - 1);
			kindList.add(element.getType());
		}
		return kindList.toArray(new String[0]);
	}

	static void debugResult(QueryResult result, String indent) {
		Iterator<EntityProto> it = result.resultIterator();
		while (it.hasNext()) {
			EntityProto next = it.next();
			StringBuilder b = new StringBuilder();
			Path path = next.getKey().getPath();
			int size = path.elementSize();
			for (int i = 0; i < size; i++) {
				if (i > 0) {
					b.append("/");
				}
				Element element = path.getElement(i);
				String name =
						element.getName().equals("") ? String.valueOf(element.getId()) : element
							.getName();
				b.append(element.getType()).append("(").append(name).append(")");
			}
			System.out.println(indent + "" + b.toString());
		}
	}
}
