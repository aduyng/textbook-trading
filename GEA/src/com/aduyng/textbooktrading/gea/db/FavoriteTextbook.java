package com.aduyng.textbooktrading.gea.db;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class FavoriteTextbook {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Long id;

	@Persistent
	String phoneNumber;

	@Persistent
	long textbookId;

	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "FavoriteTextbook [id=" + id + ", phoneNumber=" + phoneNumber
				+ ", textbookId=" + textbookId + "]";
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public long getTextbookId() {
		return textbookId;
	}

	public void setTextbookId(Long textbookId) {
		this.textbookId = textbookId;
	}

	public void validate(PersistenceManager pm)
			throws InvalidFieldValueException {
		if (null == phoneNumber || phoneNumber.length() == 0) {
			throw new InvalidFieldValueException("phoneNumber is empty");
		}

		try {
			getAccount(pm);
		} catch (Exception e) {
			throw new InvalidFieldValueException("account " + phoneNumber
					+ " is not found");
		}
		if (textbookId == 0) {
			throw new InvalidFieldValueException("textbookId is empty");
		}
		try {
			getTextbook(pm);
		} catch (Exception e) {
			throw new InvalidFieldValueException("textbook " + textbookId
					+ " is not found");
		}

	}

	public Textbook getTextbook(PersistenceManager pm) {
		return pm.getObjectById(Textbook.class, textbookId);
	}

	public Account getAccount(PersistenceManager pm) {
		Key k = KeyFactory
				.createKey(Account.class.getSimpleName(), phoneNumber);
		return pm.getObjectById(Account.class, k);
	}

	public void save(PersistenceManager pm) throws InvalidFieldValueException {
		validate(pm);
		pm.makePersistent(this);
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", id);
		jsonObject.put("textbookId", textbookId);
		jsonObject.put("phoneNumber", phoneNumber);
		return jsonObject;
	}

	public static FavoriteTextbook getByPhoneNumberAndTextbookId(
			PersistenceManager pm, String phoneNumber, Long textbookId) {
		try {
			Query query = pm.newQuery(FavoriteTextbook.class);
			query.setFilter("phoneNumber == phoneNumberParam && textbookId == textbookIdParam");
			query.declareParameters("String phoneNumberParam, long textbookIdParam");
			List<FavoriteTextbook> records = (List<FavoriteTextbook>) query
					.execute(phoneNumber, textbookId);
			if (records != null && records.size() > 0) {
				return (FavoriteTextbook) records.get(0);
			}
		} catch (Exception e) {

		}
		return null;

	}

}
