package io.mosip.kernel.auth.dto;

import java.util.List;

/**
 *  @author Sabbu Uday Kumar
 *  @since 1.0.0
 */
public class RolesListDto {

	private List<RoleDto> roles;

	public List<RoleDto> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleDto> roles) {
		this.roles = roles;
	}
}
