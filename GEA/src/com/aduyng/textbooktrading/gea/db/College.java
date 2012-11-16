package com.aduyng.textbooktrading.gea.db;

import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

@PersistenceCapable
public class College {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	private String name;

	@Persistent
	private String logoBlobKey;

	
	public String getLogoBlobKey() {
		return logoBlobKey;
	}

	public void setLogoBlobKey(String logoBlobKey) {
		this.logoBlobKey = logoBlobKey;
	}

	@Persistent
	private Set<String> tags;

	public College() {

	}

	// public College(String name, String logoBlobKey) {
	// super();
	// this.name = name;
	// this.logoBlobKey = logoBlobKey;
	// }

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private void validate() throws InvalidFieldValueException {
		if (null == name || 0 == name.trim().length()) {
			throw new InvalidFieldValueException("name is empty");
		}
		name = name.trim();

		if (null == logoBlobKey || 0 == logoBlobKey.trim().length()) {
			throw new InvalidFieldValueException("logoBlobKey is empty");
		}

		

		tags = FullTextSearchTokenizer.getTokensForIndexingOrQuery(name, 20);

	}

	public void save(PersistenceManager pm) throws InvalidFieldValueException {
		validate();
		pm.makePersistent(this);
	}

	@Override
	public String toString() {
		return College.class.getSimpleName() + "[id=" + id + ",name=" + name
				+ ",logoBlobKey=" + logoBlobKey + "]";
	}

	public JSONObject toJSONObject()  {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("logoBlobKey", logoBlobKey);
		jsonObject.put("logoSmallUrl", this.getLogoSmallUrl());
		jsonObject.put("logoUrl", this.getLogoUrl());
		jsonObject.put("name", name);
		jsonObject.put("id", id);
		return jsonObject;
	}

	private Object getLogoUrl() {
		ImagesService imagesService = ImagesServiceFactory
				.getImagesService();
		BlobKey blobKey = new BlobKey(logoBlobKey);
		return imagesService.getServingUrl(blobKey, 48, false);
	}

	private Object getLogoSmallUrl() {
		ImagesService imagesService = ImagesServiceFactory
				.getImagesService();
		BlobKey blobKey = new BlobKey(logoBlobKey);
		return imagesService.getServingUrl(blobKey, 128, false);
	}

}
