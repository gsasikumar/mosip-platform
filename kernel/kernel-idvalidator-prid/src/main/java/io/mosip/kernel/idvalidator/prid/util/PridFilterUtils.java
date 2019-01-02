package io.mosip.kernel.idvalidator.prid.util;

import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class PridFilterUtils {
	/**
	 * Ascending digits which will be checked for sequence in id
	 */
	private static final String SEQ_ASC = "0123456789";

	/**
	 * Descending digits which will be checked for sequence in id
	 */
	private static final String SEQ_DEC = "9876543210";

	/**
	 * Compiled regex pattern for 
	 */
	private Pattern repeatingPattern = null;

	/**
	 * Compiled regex pattern of {@link #repeatingBlockRegex}
	 */
	private Pattern repeatingBlockpattern = null;

	public void initializeRegEx(int repeatingLimit, int repeatingBlockLimit) {

		/**
		 * Regex for matching repeating digits like 11, 1x1, 1xx1, 1xxx1, etc.<br/>
		 * If repeating digit limit is 2, then <b>Regex:</b> (\d)\d{0,2}\1<br/>
		 * <b>Explanation:</b><br/>
		 * <b>1st Capturing Group (\d)</b><br/>
		 * <b>\d</b> matches a digit (equal to [0-9])<br/>
		 * <b>{0,2} Quantifier</b> — Matches between 0 and 2 times, as many times as
		 * possible, giving back as needed (greedy)<br/>
		 * <b>\1</b> matches the same text as most recently matched by the 1st capturing
		 * group<br/>
		 */
		String repeatingRegEx = "(\\d)\\d{0," + (repeatingLimit - 1) + "}\\1";

		/**
		 * Regex for matching repeating block of digits like 482xx482, 4827xx4827 (x is
		 * any digit).<br/>
		 * If repeating block limit is 3, then <b>Regex:</b> (\d{3,}).*?\1<br/>
		 * <b>Explanation:</b><br/>
		 * <b>1st Capturing Group (\d{3,})</b><br/>
		 * <b>\d{3,}</b> matches a digit (equal to [0-9])<br/>
		 * <b>{3,}</b> Quantifier — Matches between 3 and unlimited times, as many times
		 * as possible, giving back as needed (greedy)<br/>
		 * <b>.*?</b> matches any character (except for line terminators)<br/>
		 * <b>*?</b> Quantifier — Matches between zero and unlimited times, as few times
		 * as possible, expanding as needed (lazy)<br/>
		 * <b>\1</b> matches the same text as most recently matched by the 1st capturing
		 * group<br/>
		 */
		String repeatingBlockRegex = "(\\d{" + repeatingBlockLimit + ",}).*?\\1";

		/**
		 * Compiled regex pattern of {@link #repeatingRegEx}
		 */
		repeatingPattern = Pattern.compile(repeatingRegEx);
		/**
		 * Compiled regex pattern of {@link #repeatingBlockRegex}
		 */
		repeatingBlockpattern = Pattern.compile(repeatingBlockRegex);
	}

	/**
	 * Checks if the input id is valid by passing the id through
	 * {@link #sequenceLimit} filter, {@link #repeatingLimit} filter and
	 * {@link #repeatingBlockLimit} filters
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the input id is valid
	 */
	public boolean isValidId(String id, int sequenceLimit, int repeatingLimit, int repeatingBlockLimit) {
		initializeRegEx(repeatingLimit, repeatingBlockLimit);
		return !(sequenceFilter(id, sequenceLimit) || regexFilter(id, repeatingPattern)
				|| regexFilter(id, repeatingBlockpattern));
	}

	/**
	 * Checks the input id for {@link #sequenceLimit} filter
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the id matches the filter
	 */
	private boolean sequenceFilter(String id, int sequenceLimit) {
		return IntStream.rangeClosed(0, id.length() - sequenceLimit).parallel()
				.mapToObj(index -> id.subSequence(index, index + sequenceLimit))
				.anyMatch(idSubSequence -> SEQ_ASC.contains(idSubSequence) || SEQ_DEC.contains(idSubSequence));
	}

	/**
	 * Checks the input id if it matched the given regex pattern
	 * ({@link #REPEATING_PATTERN}, {@link #repeatingBlockpattern})
	 * 
	 * @param id
	 *            The input id to validate
	 * @param pattern
	 *            The input regex Pattern
	 * @return true if the id matches the given regex pattern
	 */
	private static boolean regexFilter(String id, Pattern pattern) {
		return pattern.matcher(id).find();
	}

}
