package io.mosip.kernel.masterdata.validator;

/**
 * @author Sagar Mahapatra
 * @since 1.0
 *
 */
public enum FilterColumnEnum {
	UNIQUE("unique"), ALL("all");

	private String filterColumn;

	private FilterColumnEnum(String filterColumn) {
		this.filterColumn = filterColumn;
	}

	@Override
	public String toString() {
		return filterColumn;
	}

}
