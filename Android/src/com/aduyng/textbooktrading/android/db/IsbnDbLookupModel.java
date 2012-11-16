package com.aduyng.textbooktrading.android.db;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.methods.HttpGet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;

import com.aduyng.textbooktrading.android.common.RestClient;
import com.aduyng.textbooktrading.android.entity.Book;

public class IsbnDbLookupModel extends Model {
	public IsbnDbLookupModel(Context context) {
		super(context);
	}

	private static String getIsbnDbSearchUrl(String query, int pageNumber) {
		String url = "http://isbndb.com/api/books.xml?access_key=NBPFZESI&page_number=" + pageNumber + "&index1=";

		if (query.matches("^\\d+$")) {
			url += "isbn";
		} else {
			url += "title";
		}

		url += "&value1=" + URLEncoder.encode(query);

		return url;
	}

	public List<Book> search(String query, int pageNumber) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		List<Book> books = new ArrayList<Book>();
		HttpGet request = new HttpGet(
				IsbnDbLookupModel.getIsbnDbSearchUrl(query, pageNumber));
		byte[] response = new RestClient(context).execute(request, false);

		if (null == response) {
			return null;
		}
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource inStream = new org.xml.sax.InputSource();
	    inStream.setCharacterStream(new java.io.StringReader(new String(response)));
		Document dom = builder.parse(inStream);
		Element root = dom.getDocumentElement();
		NodeList items = root.getElementsByTagName("BookData");

		for (int i = 0; i < items.getLength(); i++) {
			Book book = new Book();
			Node item = items.item(i);
			NamedNodeMap attributes = item.getAttributes();
			book.setIsbn(attributes.getNamedItem("isbn").getNodeValue());
			book.setIsbn13(attributes.getNamedItem("isbn13").getNodeValue());

			NodeList children = item.getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {
				Node property = children.item(j);
				String name = property.getNodeName();
				Node firstChild = property.getFirstChild();
				if (name.equalsIgnoreCase("Title")) {
					if (null != firstChild) {
						book.setTitle(firstChild.getNodeValue());

					}
				} else if (name.equalsIgnoreCase("AuthorsText")) {
					if (null != firstChild) {
						book.setAuthors(firstChild.getNodeValue());
					}
				} else if (name.equalsIgnoreCase("PublisherText")) {

					if (null != firstChild) {
						book.setPublisher(firstChild.getNodeValue());
					}
				}
			}
			books.add(book);
		}

		return books;

	}

}
