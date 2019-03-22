package io.mosip.registration.context;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.AuthTokenDTO;
import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;

public class SessionContext {

	private static SessionContext sessionContext;

	private SessionContext() {

	}

	private UUID id;
	private static UserContext userContext;
	private Date loginTime;
	private long refreshedLoginTime;
	private long timeoutInterval;
	private long idealTime;
	private Map<String, Object> mapObject;
	private AuthTokenDTO authTokenDTO;

	public static SessionContext getInstance() {
		if (sessionContext == null) {
			sessionContext = new SessionContext();
			sessionContext.setId(UUID.randomUUID());
			sessionContext.setMapObject(new HashMap<>());
			userContext = sessionContext.new UserContext();
			sessionContext.authTokenDTO = new AuthTokenDTO();
			return sessionContext;
		} else {
			return sessionContext;
		}
	}

	public static Map<String, Object> map() {
		return sessionContext.getMapObject();
	}

	public static UserContext userContext() {
		return sessionContext.getUserContext();
	}

	public static Map<String, Object> userMap() {
		return sessionContext.getUserContext().getUserMap();
	}

	public static long refreshedLoginTime() {
		return sessionContext.getRefreshedLoginTime();
	}

	public static Date loginTime() {
		return sessionContext.getLoginTime();
	}

	public static long timeoutInterval() {
		return sessionContext.getTimeoutInterval();
	}

	public static long idealTime() {
		return sessionContext.getIdealTime();
	}

	public static void setAuthTokenDTO(AuthTokenDTO authTokenDTO) {
		sessionContext.authTokenDTO = authTokenDTO;
	}

	public static AuthTokenDTO authTokenDTO() {
		return sessionContext.authTokenDTO;
	}

	public static String userId() {
		if (sessionContext == null || sessionContext.getUserContext().getUserId() == null) {
			return RegistrationConstants.AUDIT_DEFAULT_USER;
		} else {
			return sessionContext.getUserContext().getUserId();
		}
	}

	public static String userName() {
		if (sessionContext == null || sessionContext.getUserContext().getName() == null) {
			return RegistrationConstants.AUDIT_DEFAULT_USER;
		} else {
			return sessionContext.getUserContext().getName();
		}
	}

	public static boolean isSessionContextAvailable() {
		return sessionContext != null;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UserContext getUserContext() {
		return userContext;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public long getRefreshedLoginTime() {
		return refreshedLoginTime;
	}

	public void setRefreshedLoginTime(long refreshedLoginTime) {
		this.refreshedLoginTime = refreshedLoginTime;
	}

	public long getTimeoutInterval() {
		return timeoutInterval;
	}

	public void setTimeoutInterval(long timeoutInterval) {
		this.timeoutInterval = timeoutInterval;
	}

	public long getIdealTime() {
		return idealTime;
	}

	public void setIdealTime(long idealTime) {
		this.idealTime = idealTime;
	}

	public Map<String, Object> getMapObject() {
		return mapObject;
	}

	public void setMapObject(Map<String, Object> mapObject) {
		this.mapObject = mapObject;
	}

	public static void destroySession() {
		sessionContext = null;
	}

	public class UserContext {
		private String userId;
		private String name;
		private RegistrationCenterDetailDTO registrationCenterDetailDTO;
		private List<String> roles;
		private AuthorizationDTO authorizationDTO;
		private Map<String, Object> userMap;

		private UserContext() {

		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public RegistrationCenterDetailDTO getRegistrationCenterDetailDTO() {
			return registrationCenterDetailDTO;
		}

		public void setRegistrationCenterDetailDTO(RegistrationCenterDetailDTO registrationCenterDetailDTO) {
			this.registrationCenterDetailDTO = registrationCenterDetailDTO;
		}

		public List<String> getRoles() {
			return roles;
		}

		public void setRoles(List<String> roles) {
			this.roles = roles;
		}

		public AuthorizationDTO getAuthorizationDTO() {
			return authorizationDTO;
		}

		public void setAuthorizationDTO(AuthorizationDTO authorizationDTO) {
			this.authorizationDTO = authorizationDTO;
		}

		public Map<String, Object> getUserMap() {
			return userMap;
		}

		public void setUserMap(Map<String, Object> userMap) {
			this.userMap = userMap;
		}

	}

}
