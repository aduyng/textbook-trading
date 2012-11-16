package com.aduyng.textbooktrading.gea.db;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class AppAccount {
	@PrimaryKey
	String key;

	@Persistent
	String name;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void validate(PersistenceManager pm)
			throws InvalidFieldValueException {
		if (null == name || name.length() == 0) {
			throw new InvalidFieldValueException("name is empty");
		}

		if (null == key || key.trim().length() == 0) {
			throw new InvalidFieldValueException("key is empty");
		}
	}

	public void save(PersistenceManager pm) throws InvalidFieldValueException {
		validate(pm);
		pm.makePersistent(this);
	}

}
