package io.mosip.kernel.uingenerator.util;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Filter class to validate a uin against custom filters
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class UinFilterUtil {

	/**
	 * Length of the uin
	 */
	@Value("${mosip.kernel.uin.length}")
	private int uinLength;

	/**
	 * Upper bound of number of digits in sequence allowed in id. For example if
	 * limit is 3, then 12 is allowed but 123 is not allowed in id (in both
	 * ascending and descending order)
	 */
	@Value("${mosip.kernel.uin.length.sequence-limit}")
	private int sequenceLimit;

	/**
	 * Number of digits in repeating block allowed in id. For example if limit is 2,
	 * then 4xxx4 is allowed but 48xxx48 is not allowed in id (x is any digit)
	 */
	@Value("${mosip.kernel.uin.length.repeating-block-limit}")
	private int repeatingBlockLimit;

	/**
	 * Lower bound of number of digits allowed in between two repeating digits in
	 * id. For example if limit is 2, then 11 and 1x1 is not allowed in id (x is any
	 * digit)
	 */
	@Value("${mosip.kernel.uin.length.repeating-limit}")
	private int repeatingLimit;

	/**
	 * Number of digits to check for reverse digits group limit, example if limit is
	 * 5 and UIN is 4345665434 then first 5 digits will be 43456, reverse 65434
	 */
	@Value("${mosip.kernel.uin.length.reverse-digits-limit}")
	private int reverseDigitsGroupLimit;

	/**
	 * Number of digits to check for digits group limit, example if limit is 5 and
	 * UIN is 4345643456 then 5 digits group will be 43456
	 */
	@Value("${mosip.kernel.uin.length.digits-limit}")
	private int digitsGroupLimit;

	/**
	 * Number of even adjacent digits limit, e.g if limit is 3 then any 3 even
	 * adjacent digits is not allowed
	 */
	@Value("${mosip.kernel.uin.length.conjugative-even-digits-limit}")
	private int conjugativeEvenDigitsLimit;

	/**
	 * List of restricted numbers
	 */
	@Value("#{'${mosip.kernel.uin.restricted-numbers}'.split(',')}")
	private List<String> restrictedAdminDigits;

	/**
	 * Ascending digits which will be checked for sequence in id
	 */
	private static final String SEQ_ASC = "01234567890123456789";

	/**
	 * Descending digits which will be checked for sequence in id
	 */
	private static final String SEQ_DEC = "98765432109876543210";

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
	private Pattern repeatingPattern = null;

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
	private Pattern repeatingBlockPattern = null;

	/**
	 * Regex for matching adjacent even block of digits like 482<br/>
	 * If limit is 3, then <b>Regex:</b> [2468]{3}<br/>
	 * <b>Explanation:</b><br/>
	 * Match a single character present in the list below: <br/>
	 * <b>[2468]{3}</b><br/>
	 * <b>{3}</b> Quantifier — Matches exactly 3 times<br/>
	 * 2468 matches a single character in the list 2468 (case sensitive)
	 */
	private Pattern conjugativeEvenDigitsLimitPattern = null;

	@PostConstruct
	public void initializeRegEx() {
		String repeatingRegEx = "(\\d)\\d{0," + (repeatingLimit - 1) + "}\\1";
		String repeatingBlockRegEx = "(\\d{" + repeatingBlockLimit + ",}).*?\\1";
		String conjugativeEvenDigitsLimitRegEx = "[2468]{" + conjugativeEvenDigitsLimit + "}";
		repeatingPattern = Pattern.compile(repeatingRegEx);
		repeatingBlockPattern = Pattern.compile(repeatingBlockRegEx);
		conjugativeEvenDigitsLimitPattern = Pattern.compile(conjugativeEvenDigitsLimitRegEx);
	}

	/**
	 * Checks if the input id is valid by passing the id through the filters
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the input id is valid
	 */
	public boolean isValidId(String id) {
		return !(sequenceFilter(id) || regexFilter(id, repeatingPattern) || regexFilter(id, repeatingBlockPattern)
				|| regexFilter(id, conjugativeEvenDigitsLimitPattern)
				|| firstAndLastDigitsValidation(id, digitsGroupLimit)
				|| firstAndLastDigitsReverseValidation(id, reverseDigitsGroupLimit) || restrictedAdminFilter(id));
	}

	/**
	 * Checks the input id for {@link #restrictedNumbers} filter
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the id matches the filter
	 */
	private boolean restrictedAdminFilter(String id) {
		return restrictedAdminDigits.parallelStream().anyMatch(id::contains);
	}

	/**
	 * Checks the input id for {@link #SEQUENCE_LIMIT} filter
	 * 
	 * @param id
	 *            The input id to validate
	 * @return true if the id matches the filter
	 */
	private boolean sequenceFilter(String id) {
		return IntStream.rangeClosed(0, id.length() - sequenceLimit).parallel()
				.mapToObj(index -> id.subSequence(index, index + sequenceLimit))
				.anyMatch(idSubSequence -> SEQ_ASC.contains(idSubSequence) || SEQ_DEC.contains(idSubSequence));
	}

	/**
	 * Checks the input id if it matched the given regex pattern
	 * ({@link #repeatingPattern}, {@link #repeatingBlockPattern})
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

	/**
	 * Checks whether the first x digits are different from the last x digits
	 * reversed
	 * 
	 * @param id
	 *            The input UIN id to validate
	 * @param reverseDigitsGroupLimit
	 *            reverseDigitsGroupLimit
	 * @return true if the filter matches
	 */
	private boolean firstAndLastDigitsReverseValidation(String id, int reverseDigitsGroupLimit) {
		StringBuilder lastGroupDigits = new StringBuilder(
				id.substring(id.length() - reverseDigitsGroupLimit, id.length()));
		String reverseLastGroupDigits = lastGroupDigits.reverse().toString();
		return (id.substring(0, reverseDigitsGroupLimit).equals(reverseLastGroupDigits));
	}

	/**
	 * Check whether the first x digits are different from the last x digits
	 * 
	 * 
	 * @param id
	 *            The input UIN id to validate
	 * @param digitsGroupLimit
	 *            digitsGroupLimit
	 * @return true if filter matches
	 */

	private boolean firstAndLastDigitsValidation(String id, int digitsGroupLimit) {
		return (id.substring(0, digitsGroupLimit).equals(id.substring(id.length() - digitsGroupLimit, id.length())));
	}
}
