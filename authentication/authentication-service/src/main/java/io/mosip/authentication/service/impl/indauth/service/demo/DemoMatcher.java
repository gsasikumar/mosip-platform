package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntBiFunction;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.dto.indauth.DemoDTO;

/**
 * @author Arun Bose The Class DemoMatcher.
 */
@Component

public class DemoMatcher {

	/**
	 * Match demo data.
	 *
	 * @param demoDTO
	 *            the demo DTO
	 * @param demoEntity
	 *            the demo entity
	 * @param locationInfoFetcher
	 * @param matchInput
	 *            the match input
	 * @return the list
	 */
	public List<MatchOutput> matchDemoData(DemoDTO demoDTO, DemoEntity demoEntity, Collection<MatchInput> listMatchInputs,
			LocationInfoFetcher locationInfoFetcher) {
		return listMatchInputs.parallelStream().map(input -> matchType(demoDTO, demoEntity, input, locationInfoFetcher))
				.filter(output -> output != null).collect(Collectors.toList());
	}

	/**
	 * Match type.
	 *
	 * @param demoDTO
	 *            the demo DTO
	 * @param demoEntity
	 *            the demo entity
	 * @param input
	 *            the input
	 * @return the match output
	 */
	private static MatchOutput matchType(DemoDTO demoDTO, DemoEntity demoEntity, MatchInput input,
			LocationInfoFetcher locationInfoFetcher) {
		String matchStrategyTypeStr = input.getMatchStrategyType();
		if (matchStrategyTypeStr == null) {
			matchStrategyTypeStr = MatchingStrategyType.EXACT.getType();
		}

		Optional<MatchingStrategyType> matchStrategyType = MatchingStrategyType
				.getMatchStrategyType(matchStrategyTypeStr);
		if (matchStrategyType.isPresent()) {
			MatchingStrategyType strategyType = matchStrategyType.get();
			Optional<MatchingStrategy> matchingStrategy = input.getDemoMatchType()
					.getAllowedMatchingStrategy(strategyType);
			if (matchingStrategy.isPresent()) {
				MatchingStrategy strategy = matchingStrategy.get();
				Optional<Object> reqInfoOpt = input.getDemoMatchType().getDemoInfoFetcher().apply(demoDTO);
				if(reqInfoOpt.isPresent()) {
					Object reqInfo = reqInfoOpt.get();
					Object entityInfo = input.getDemoMatchType().getEntityInfoFetcher().getInfo(demoEntity,
							locationInfoFetcher);
					ToIntBiFunction<Object, Object> matchFunction = strategy.getMatchFunction();
					int mtOut = matchFunction.applyAsInt(reqInfo, entityInfo);
					boolean matchOutput = mtOut >= input.getMatchValue();
					return new MatchOutput(mtOut, matchOutput, input.getMatchStrategyType(), input.getDemoMatchType());
				}
			}
		}
		return null;
	}

}
