package com.aduyng.textbooktrading.android.entity;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Textbook {
	long id;
	String title;
	String isbn10;
	String isbn13;
	String publishers;
	String authors;
	String description;
	String phoneNumber;
	double price;
	String pictureSmallUrl;
	String pictureUrl;
	boolean isSold;
	boolean isBuyingRequest;
	Date datePosted;
	String imageBlobKey;
	
	long collegeId; 
	
	FavoriteTextbook favoriteTextbook;
	Account seller;
	


	public Account getSeller() {
		return seller;
	}

	public void setSeller(Account seller) {
		this.seller = seller;
	}

	public FavoriteTextbook getFavoriteTextbook() {
		return favoriteTextbook;
	}

	public void setFavoriteTextbook(FavoriteTextbook favoriteTextbook) {
		this.favoriteTextbook = favoriteTextbook;
	}

	public long getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(long collegeId) {
		this.collegeId = collegeId;
	}

	public Date getDatePosted() {
		return datePosted;
	}

	public void setDatePosted(Date datePosted) {
		this.datePosted = datePosted;
	}

	@Override
	public String toString() {
		return "Textbook [id=" + id + ", title=" + title + ", isbn10=" + isbn10
				+ ", isbn13=" + isbn13 + ", publishers=" + publishers
				+ ", authors=" + authors + ", description=" + description
				+ ", phoneNumber=" + phoneNumber + ", price=" + price
				+ ", pictureSmallUrl=" + pictureSmallUrl + ", pictureUrl="
				+ pictureUrl + ", isSold=" + isSold + ", isBuyingRequest="
				+ isBuyingRequest + ", datePosted=" + datePosted
				+ ", imageBlobKey=" + imageBlobKey + "]";
	}

	public String getImageBlobKey() {
		return imageBlobKey;
	}

	public void setImageBlobKey(String imageBlobKey) {
		this.imageBlobKey = imageBlobKey;
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
	
	public long getId() {
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
		return pictureSmallUrl;
	}

	public void setPictureSmallUrl(String pictureSmallUrl) {
		this.pictureSmallUrl = pictureSmallUrl;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}


	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("id", id);
		jsonObject.put("isbn10", isbn10);
		jsonObject.put("isbn13", isbn13);
		jsonObject.put("publishers", publishers);
		jsonObject.put("authors", authors);
		jsonObject.put("description", description);
		jsonObject.put("phoneNumber", phoneNumber);
		jsonObject.put("price", price);
		jsonObject.put("imageBlobKey", imageBlobKey);
		jsonObject.put("pictureUrl", pictureUrl);
		jsonObject.put("pictureSmallUrl", pictureSmallUrl);
		jsonObject.put("isSold", isSold);
		jsonObject.put("title", title);
		jsonObject.put("isBuyingRequest", isBuyingRequest);
		jsonObject.put("datePosted", datePosted);
		jsonObject.put("collegeId", collegeId);

		return jsonObject;
	}
	
	public static Textbook fromJSONObject(JSONObject jsonObject) throws JSONException {
		Textbook textbook = new Textbook();
		textbook.id = jsonObject.getLong("id");
		textbook.collegeId = jsonObject.getLong("collegeId");
		textbook.isbn10 = jsonObject.getString("isbn10");
		textbook.isbn13 = jsonObject.getString("isbn13");
		textbook.publishers = jsonObject.getString("publishers");
		textbook.authors = jsonObject.getString("authors");
		textbook.description = jsonObject.getString("description");
		textbook.phoneNumber = jsonObject.getString("phoneNumber");
		textbook.price = jsonObject.getDouble("price");
		textbook.imageBlobKey = jsonObject.getString("imageBlobKey");
		textbook.pictureSmallUrl = jsonObject.getString("pictureSmallUrl");
//		if( null != textbook.pictureSmallUrl ){
//			textbook.pictureSmallUrl = textbook.pictureSmallUrl.trim();
//		}
		textbook.pictureUrl = jsonObject.getString("pictureUrl");
//		if( null != textbook.pictureUrl ){
//			textbook.pictureUrl = textbook.pictureUrl.trim();
//		}
		
		textbook.isSold = jsonObject.getBoolean("isSold");
		textbook.isBuyingRequest = jsonObject.getBoolean("isBuyingRequest");
		textbook.title = jsonObject.getString("title");
		textbook.datePosted = new Date(jsonObject.getLong("datePosted"));
		
		if( jsonObject.has("favorite")){
			textbook.favoriteTextbook = FavoriteTextbook.fromJSONObject(jsonObject.getJSONObject("favorite"));
		}
		
		if( jsonObject.has("seller")){
			textbook.seller = Account.fromJSONObject(jsonObject.getJSONObject("seller"));
		}
		
		return textbook;
	}
}

