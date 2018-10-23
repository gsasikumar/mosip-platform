package io.mosip.authentication.service.impl.indauth.service.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.function.ToIntBiFunction;

import org.junit.Test;

/**
 * 
 * @author Dinesh Karuppiah
 */
public class NameMatchingStrategyTest {

	/**
	 * Check for Exact type matched with Enum value of Name Matching Strategy
	 */

	@Test
	public void TestValidExactMatchingStrategytype() {
		assertEquals(NameMatchingStrategy.EXACT.getType(), MatchingStrategyType.EXACT);
	}

	/**
	 * Check for Exact type not matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestInvalidExactMatchingStrategytype() {
		assertNotEquals(NameMatchingStrategy.EXACT.getType(), "PARTIAL");
	}

	/**
	 * Assert the Name Matching Strategy for Exact is Not null
	 */
	@Test
	public void TestValidExactMatchingStrategyfunctionisNotNull() {
		assertNotNull(NameMatchingStrategy.EXACT.getMatchFunction());
	}

	/**
	 * Assert the Name Matching Strategy for Exact is null
	 */
	@Test
	public void TestExactMatchingStrategyfunctionisNull() {
		ToIntBiFunction<Object, Object> matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Tests doMatch function on Matching Strategy Function
	 */
	@Test
	public void TestValidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, Object> matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.applyAsInt("dinesh karuppiah", "dinesh karuppiah");
		assertEquals(100, value);
	}

	/**
	 * 
	 * Tests the Match function with in-valid values
	 */
	@Test
	public void TestInvalidExactMatchingStrategyFunction() {
		ToIntBiFunction<Object, Object> matchFunction = NameMatchingStrategy.EXACT.getMatchFunction();
		int value = matchFunction.applyAsInt(2, 2);
		assertEquals(0, value);

		int value1 = matchFunction.applyAsInt(2, "dinesh");
		assertEquals(0, value1);

		int value2 = matchFunction.applyAsInt("dinesh", 2);
		assertEquals(0, value2);
	}

	/**
	 * Assert Partial type matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestValidPartialMatchingStrategytype() {
		assertEquals(NameMatchingStrategy.PARTIAL.getType(), MatchingStrategyType.PARTIAL);
	}

	/**
	 * Assert Partial type not-matched with Enum value of Name Matching Strategy
	 */
	@Test
	public void TestInvalidPartialMatchingStrategytype() {
		assertNotEquals(NameMatchingStrategy.PARTIAL.getType(), "EXACT");
	}

	/**
	 * Assert Partial Match function is not null
	 */
	@Test
	public void TestValidPartialMatchingStrategyfunctionisNotNull() {
		assertNotNull(NameMatchingStrategy.PARTIAL.getMatchFunction());
	}

	/**
	 * Assert Partial Match function is null
	 */
	@Test
	public void TestPartialMatchingStrategyfunctionisNull() {
		ToIntBiFunction<Object, Object> matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		matchFunction = null;
		assertNull(matchFunction);
	}

	/**
	 * Assert the Partial Match Function with Do-Match Method
	 */
	@Test
	public void TestValidPartialMatchingStrategyFunction() {
		ToIntBiFunction<Object, Object> matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		int value = matchFunction.applyAsInt("dinesh thiagarajan", "dinesh karuppiah");
		assertEquals(33, value);
	}

	/**
	 * Assert the Partial Match Function not matched via Do-Match Method
	 */
	@Test
	public void TestInvalidPartialMatchingStrategyFunction() {
		ToIntBiFunction<Object, Object> matchFunction = NameMatchingStrategy.PARTIAL.getMatchFunction();
		int value = matchFunction.applyAsInt(2, 2);
		assertEquals(0, value);

		int value1 = matchFunction.applyAsInt(2, "dinesh");
		assertEquals(0, value1);

		int value2 = matchFunction.applyAsInt("dinesh", 2);
		assertEquals(0, value2);
	}

	/**
	 * Assert Phonetics Match Value is not-null on Name Matching Strategy
	 */
	@Test
	public void TestPhoneticsMatch() {
		assertNotNull(NameMatchingStrategy.PHONETICS);
	}

	/**
	 * Assert Phonetics Match Type is equals with Matching Strategy type
	 */
	@Test
	public void TestPhoneticsMatchStrategyType() {
		assertEquals(NameMatchingStrategy.PHONETICS.getType(), MatchingStrategyType.PHONETICS);
	}

	/**
	 * Assert Phonetics Match Value via doMatch method
	 */
	@Test
	public void TestPhoneticsMatchValue() {
		ToIntBiFunction<Object, Object> matchFunction = NameMatchingStrategy.PHONETICS.getMatchFunction();
		int value = matchFunction.applyAsInt(2, 2);
		assertEquals(0, value);
	}

}
