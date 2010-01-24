package com.shin1ogawa.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.shin1ogawa.meta.ManyEntityMeta;
import com.shin1ogawa.model.ManyEntity;

public class ManyEntityService {

	private ManyEntityService() {
	}


	static final ManyEntityMeta META = ManyEntityMeta.get();


	public static List<ManyEntity> create(long startId, int count) {
		if (startId == 0) {
			throw new IllegalArgumentException("0はKeyのid値として使用できません。");
		}
		List<ManyEntity> entities = new ArrayList<ManyEntity>(count);
		for (int i = 0; i < count; i++) {
			long id = i + startId;
			Key key = Datastore.createKey(META, id);
			ManyEntity entity = new ManyEntity();
			entity.setKey(key);
			entity.setMod2(id % 2 == 0);
			entity.setMod3(id % 3 == 0);
			entity.setMod5(id % 5 == 0);
			entity.setMod7(id % 7 == 0);
			entity.setMod11(id % 11 == 0);
			entity.setMod13(id % 13 == 0);
			entity.setMod17(id % 17 == 0);
			entity.setListProp(randomStringList(entity));
			entity.setNumberProp((long) (Math.random() * 50));
			entity.setStringProp(RandomStringUtils.randomAlphanumeric(20));
			entities.add(entity);
		}
		Datastore.put(entities);
		return entities;
	}

	private static Set<String> randomStringList(ManyEntity entity) {
		Set<String> set = new HashSet<String>();
		if (entity.getMod2()) {
			set.add("apple");
		}
		if (entity.getMod3()) {
			set.add("banana");
		}
		if (entity.getMod5()) {
			set.add("cherry");
		}
		if (entity.getMod7()) {
			set.add("dragonfruit");
		}
		if (entity.getMod11()) {
			set.add("eggfruit");
		}
		if (entity.getMod13()) {
			set.add("fig");
		}
		if (entity.getMod17()) {
			set.add("google");
		}
		return set;
	}
}
