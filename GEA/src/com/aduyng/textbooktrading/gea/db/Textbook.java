package com.aduyng.textbooktrading.gea.db;

import java.util.Date;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

@PersistenceCapable
public class Textbook {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	Long id;
	@Persistent
	String title;
	@Persistent
	String isbn10;
	@Persistent
	String isbn13;
	@Persistent
	String publishers;
	@Persistent
	String authors;
	@Persistent
	String description;
	@Persistent
	String phoneNumber;
	@Persistent
	double price;
	@Persistent
	String pictureSmallUrl;
	@Persistent
	String pictureUrl;
	@Persistent
	boolean isSold;
	@Persistent
	boolean isBuyingRequest;
	
	@Persistent
	long collegeId;
	
	@Persistent
	Date datePosted;
	
	@Persistent
	private Set<String> tags;
	
	@Persistent
	private String imageBlobKey;

	

	@Override
	public String toString() {
		return "Textbook [id=" + id + ", title=" + title + ", isbn10=" + isbn10
				+ ", isbn13=" + isbn13 + ", publishers=" + publishers
				+ ", authors=" + authors + ", description=" + description
				+ ", phoneNumber=" + phoneNumber + ", price=" + price
				+ ", imageBlobkey=" + imageBlobKey + ", isSold=" + isSold + ", isBuyingRequest="
				+ isBuyingRequest + ", datePosted=" + datePosted + "]";
	}

	public boolean isSold() {
		return isSold;
	}

	public void setSold(boolean isSold) {
		this.isSold = isSold;
	}

	public boolean isBuyingRequest() {
		return isBuyingRequest;
	}

	public void setBuyingRequest(boolean isBuyingRequest) {
		this.isBuyingRequest = isBuyingRequest;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIsbn10() {
		return isbn10;
	}

	public void setIsbn10(String isbn10) {
		this.isbn10 = isbn10;
	}

	public String getIsbn13() {
		return isbn13;
	}

	public void setIsbn13(String isbn13) {
		this.isbn13 = isbn13;
	}

	public String getPublishers() {
		return publishers;
	}

	public long getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(long collegeId) {
		this.collegeId = collegeId;
	}

	public void setPublishers(String publishers) {
		this.publishers = publishers;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getPictureSmallUrl() {
		ImagesService imagesService = ImagesServiceFactory
				.getImagesService();
		BlobKey blobKey = new BlobKey(imageBlobKey);
		return imagesService.getServingUrl(blobKey, 48, false);
	}

//	public void setPictureSmallUrl(String pictureSmallUrl) {
//		this.pictureSmallUrl = pictureSmallUrl;
//	}

	public String getPictureUrl() {
		ImagesService imagesService = ImagesServiceFactory
				.getImagesService();
		BlobKey blobKey = new BlobKey(imageBlobKey);
		return imagesService.getServingUrl(blobKey, 128, false);
	}

//	public void setPictureUrl(String pictureUrl) {
//		this.pictureUrl = pictureUrl;
//	}

	public void validate(PersistenceManager pm)
			throws InvalidFieldValueException {
		if (null == phoneNumber || phoneNumber.length() == 0) {
			throw new InvalidFieldValueException("phoneNumber is empty");
		}

		try {
			getSeller(pm);
		} catch (Exception e) {
			throw new InvalidFieldValueException("sellerPhoneNumber "
					+ phoneNumber + " is not found");
		}

		if (null == title || title.trim().length() == 0) {
			throw new InvalidFieldValueException("title is empty");
		}

		if (null == isbn10 || isbn10.trim().length() == 0) {
			throw new InvalidFieldValueException("isbn10 is empty");
		}

		if (null == authors || authors.trim().length() == 0) {
			throw new InvalidFieldValueException("authors is empty");
		}

		if (price < 0) {
			throw new InvalidFieldValueException("price is invalid");
		}

		if (null == imageBlobKey || imageBlobKey.trim().length() == 0) {
			throw new InvalidFieldValueException("imageBlobkey is empty");
		}
		
		if (collegeId <= 0 ) {
			throw new InvalidFieldValueException("collegeId is empty");
		}
//
//		if (null == pictureUrl || pictureUrl.trim().length() == 0) {
//			throw new InvalidFieldValueException("pictureUrl is empty");
//		}
		
		String indexingString = title + " " + authors + " "  + isbn10;
		
		if( publishers != null && publishers.length() > 0){
			indexingString += " " + publishers;
		}
		if( isbn13 != null && isbn13.length() > 0){
			indexingString += " " + isbn13;
		}
		if( description != null && description.length() > 0){
			indexingString += " " + description;
		}
		

				
//		String[] unprocessedTags = indexingString.split("\\s");
		
		tags = FullTextSearchTokenizer.getTokensForIndexingOrQuery(indexingString, 20);
				
//				new ArrayList<String>();
//		for (String tag : unprocessedTags) {
//			if (0 < tag.trim().length()) {
//				tags.add(tag.toLowerCase());
//			}
//		}
		
		if( null == datePosted ){
			datePosted = new Date();
		}
	}

	public String getImageBlobKey() {
		return imageBlobKey;
	}

	public void setImageBlobKey(String imageBlobkey) {
		this.imageBlobKey = imageBlobkey;
	}

	public Account getSeller(PersistenceManager pm) {
		Key k = KeyFactory.createKey(Account.class.getSimpleName(),
				phoneNumber);
		return pm.getObjectById(Account.class, k);
	}

	public void save(PersistenceManager pm) throws InvalidFieldValueException {
		validate(pm);
		pm.makePersistent(this);
	}

	public JSONObject toJSONObject() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("id", id);
		jsonObject.put("isbn10", isbn10);
		jsonObject.put("isbn13", isbn13);
		jsonObject.put("collegeId", collegeId);
		jsonObject.put("publishers", publishers != null ? publishers : "");
		jsonObject.put("authors", authors);
		jsonObject.put("description", description != null ? description : "");
		jsonObject.put("title", title);
		jsonObject.put("phoneNumber", phoneNumber);
		jsonObject.put("imageBlobKey", imageBlobKey);
		jsonObject.put("price", price);
		jsonObject.put("pictureSmallUrl", this.getPictureSmallUrl());
		jsonObject.put("pictureUrl", this.getPictureUrl());
		jsonObject.put("isSold", isSold);
		jsonObject.put("isBuyingRequest", isBuyingRequest);
		jsonObject.put("datePosted", datePosted.getTime());

		return jsonObject;
	}
	
	
}
