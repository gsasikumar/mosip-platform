/**
 * 
 */
package io.mosip.kernel.auth.factory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.bouncycastle.util.Arrays;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auth.constant.AuthConstant;
import io.mosip.kernel.auth.entities.ClientSecret;
import io.mosip.kernel.auth.entities.LoginUser;
import io.mosip.kernel.auth.entities.MosipUserDto;
import io.mosip.kernel.auth.entities.MosipUserListDto;
import io.mosip.kernel.auth.entities.RolesListDto;
import io.mosip.kernel.auth.entities.UserOtp;
import io.mosip.kernel.auth.entities.otp.OtpUser;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class DBDataStore implements IDataStore {
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	private static final String NEW_USER_OTP = "INSERT INTO iam.user_detail(id,name,email,mobile,lang_code,cr_dtimes,is_active,status_code,cr_by)VALUES ( :userName,:name,:email,:phone,:langcode,NOW(),true,'ACT','Admin')";
	
	private static final String GET_USER="select use.id,use.name,use.email,use.mobile,use.lang_code,role.code from iam.user_detail use left outer join iam.user_role userrole on use.id=userrole.usr_id left outer join iam.role_list role on role.code =userrole.role_code where use.id like :userName ";
	
	private static final String GET_PASSWORD="select pwd from iam.user_pwd where usr_id like :userName ";
	
	private static final String GET_ROLE="select code from iam.role_list where code like :role ";
	
	private static final String NEW_ROLE_OTP="insert into iam.role_list(code,descr,lang_code,cr_dtimes,is_active,cr_by) values(:role,:description,:langCode,NOW(),true,'Admin')";
	
	private static final String USER_ROLE_MAPPING="insert into iam.user_role(role_code,usr_id,lang_code,cr_dtimes,is_active,cr_by) values(:roleId,:userId,'eng',NOW(),true,'Admin');";
	
	
	public DBDataStore()
	{
		
	}
	
	public DBDataStore(DataBaseConfig dataBaseConfig)
	{
		setUpConnection(dataBaseConfig);
	}

	private void setUpConnection(DataBaseConfig dataBaseConfig) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(dataBaseConfig.getDriverName());
		dataSource.setUrl(dataBaseConfig.getUrl());
		dataSource.setUsername(dataBaseConfig.getUsername());
		dataSource.setPassword(dataBaseConfig.getPassword());
		this.jdbcTemplate=new NamedParameterJdbcTemplate(dataSource);
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.AuthNDataService#authenticateUser(io.mosip.kernel.auth.entities.LoginUser)
	 */
	
	
	@Override
	public MosipUserDto authenticateUser(LoginUser loginUser) throws Exception {
		MosipUserDto mosipUserDto = getUser(loginUser.getUserName());
		byte[] password = getPassword(loginUser.getUserName());
		byte[] test = loginUser.getPassword().getBytes();
		if(mosipUserDto!=null && (Arrays.areEqual(password, test)))
		{
			return mosipUserDto;
		}
		else
		{
			throw new RuntimeException("Incorrect Password");
		}
	}

	private String getRole(String role) {
		return jdbcTemplate.query(GET_ROLE,new MapSqlParameterSource().addValue("role", role),new ResultSetExtractor<String>()
				{

					@Override
					public String extractData(ResultSet rs) throws SQLException, DataAccessException {
						while(rs.next())
						{
							return rs.getString("code");
						}
						return null;
					}
			
				});
	}
	private byte[] getPassword(String userName) {
		return jdbcTemplate.query(GET_PASSWORD,new MapSqlParameterSource().addValue("userName", userName),new ResultSetExtractor<byte[]>()
				{

					@Override
					public byte[] extractData(ResultSet rs) throws SQLException, DataAccessException {
						while(rs.next())
						{
							return rs.getString("pwd").getBytes();
						}
						return null;
					}
			
				});
	}

	private MosipUserDto getUser(String userName) {
		return jdbcTemplate.query(GET_USER,new MapSqlParameterSource().addValue("userName", userName), new ResultSetExtractor<MosipUserDto>(){

			@Override
			public MosipUserDto extractData(ResultSet rs) throws SQLException, DataAccessException {
				while(rs.next())
				{
					MosipUserDto mosipUserDto = new MosipUserDto();
					mosipUserDto.setName(rs.getString("name"));
					mosipUserDto.setRole(rs.getString("code"));
					mosipUserDto.setMail(rs.getString("email"));
					mosipUserDto.setMobile(rs.getString("mobile"));
					mosipUserDto.setUserId(rs.getString("id"));
					return mosipUserDto;
				}
				return null;
			}
			
		});
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.AuthNDataService#authenticateWithOtp(io.mosip.kernel.auth.entities.otp.OtpUser)
	 */
	@Override
	public MosipUserDto authenticateWithOtp(OtpUser otpUser) throws Exception {
		MosipUserDto mosipUserDto = getUser(otpUser.getUserId());
		String roleId=null;
		if(mosipUserDto==null)
		{
			String userId =createUser(otpUser);
			roleId = getRole(AuthConstant.INDIVIDUAL);
			if(roleId==null)
			{
				roleId=createRole(userId,otpUser);
			}
			createMapping(userId,roleId);
		}
		else
		{
			throw new RuntimeException("Please login to get the details");
		}
		return getUser(otpUser.getUserId());
	}

	private void createMapping(String userId, String roleId) {
		jdbcTemplate.update(USER_ROLE_MAPPING, 
				new MapSqlParameterSource().addValue("userId", userId)
				.addValue("roleId", roleId));
	}

	private String createRole(String userId, OtpUser otpUser) {
		jdbcTemplate.update(NEW_ROLE_OTP, 
				new MapSqlParameterSource()
				.addValue("role", AuthConstant.INDIVIDUAL)
				.addValue("description", "Individual User")
				.addValue("langCode", otpUser.getLangCode()));
		return AuthConstant.INDIVIDUAL;
		
	}

	private String createUser(OtpUser otpUser) {
		jdbcTemplate.update(NEW_USER_OTP, 
				new MapSqlParameterSource().addValue("userName", otpUser.getUserId())
				.addValue("name", otpUser.getUserId())
				.addValue("langcode", otpUser.getLangCode())
				.addValue("email", AuthConstant.EMAIL.equals(otpUser.getOtpChannel().get(0))?otpUser.getUserId():"")
				.addValue("phone", AuthConstant.PHONE.equals(otpUser.getOtpChannel().get(0))?otpUser.getUserId():""));
		return otpUser.getUserId();
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.AuthNDataService#authenticateUserWithOtp(io.mosip.kernel.auth.entities.UserOtp)
	 */
	@Override
	public MosipUserDto authenticateUserWithOtp(UserOtp loginUser) throws Exception {
		MosipUserDto mosipUserDto = getUser(loginUser.getUserId());
		return mosipUserDto;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.auth.service.AuthNDataService#authenticateWithSecretKey(io.mosip.kernel.auth.entities.ClientSecret)
	 */
	@Override
	public MosipUserDto authenticateWithSecretKey(ClientSecret clientSecret) throws Exception {
		MosipUserDto mosipUserDto = getUser(clientSecret.getClientId());
		return mosipUserDto;
	}

	@Override
	public RolesListDto getAllRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MosipUserListDto getListOfUsersDetails(List<String> userDetails) {
		// TODO Auto-generated method stub
		return null;
	}

}
