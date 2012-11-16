package com.aduyng.textbooktrading.gea.db;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

public class FullTextSearchTokenizer {

	private static final Logger log = Logger
			.getLogger(FullTextSearchTokenizer.class.getName());

	/** From StopAnalyzer Lucene 2.9.1 */
	public final static String[] stopWords = new String[] { "a", "an", "and",
			"are", "as", "at", "be", "but", "by", "for", "if", "in", "into",
			"is", "it", "no", "not", "of", "on", "or", "such", "that", "the",
			"their", "then", "there", "these", "they", "this", "to", "was",
			"will", "with" };

	/**
	 * Uses english stemming (snowball + lucene) + stopwords for getting the
	 * words.
	 * 
	 * @param index
	 * @return
	 */
	public static Set<String> getTokensForIndexingOrQuery(
			String fulltextString, int maximumNumberOfTokensToReturn) {

		String indexCleanedOfHTMLTags = fulltextString.replaceAll("\\<.*?>",
				" ");

		Set<String> returnSet = new HashSet<String>();

		try {

			Analyzer analyzer = new SnowballAnalyzer(
					org.apache.lucene.util.Version.LUCENE_CURRENT, "English",
					stopWords);

			TokenStream tokenStream = analyzer.tokenStream("content",
					new StringReader(indexCleanedOfHTMLTags));
			OffsetAttribute offsetAttribute = (OffsetAttribute) tokenStream
					.getAttribute(OffsetAttribute.class);
			TermAttribute termAttribute = (TermAttribute) tokenStream
					.getAttribute(TermAttribute.class);

			while (tokenStream.incrementToken() && (returnSet.size() < maximumNumberOfTokensToReturn)) {
//				int startOffset = offsetAttribute.startOffset();
//				int endOffset = offsetAttribute.endOffset();
				String term = fulltextString.substring(offsetAttribute.startOffset(), offsetAttribute.endOffset());
				returnSet.add(term.toLowerCase());
			}

		} catch (IOException e) {
			log.severe(e.getMessage());
		}

		return returnSet;

	}

}
