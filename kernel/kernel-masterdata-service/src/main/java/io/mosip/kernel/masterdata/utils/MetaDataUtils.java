package io.mosip.kernel.masterdata.utils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EmbeddedId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.entity.BaseEntity;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.MachineHistory;

@Component
@SuppressWarnings("unchecked")
public class MetaDataUtils {

	@Autowired
	private DataMapper dataMapper;

	public <T, D extends BaseEntity> D setCreateMetaData(final T dto, Class<? extends BaseEntity> entityClass) {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		String contextUser = authN.getName();
	
		D entity = (D) dataMapper.map(dto, entityClass, true, null, null, true);

		Field[] fields = entity.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(EmbeddedId.class)) {
				try {
					Object id = field.getType().newInstance();
					dataMapper.map(dto, id, true, null, null, true);
					field.setAccessible(true);
					field.set(entity, id);
					field.setAccessible(false);
					break;
				} catch (Exception e) {
					throw new DataAccessLayerException("KER-MSD-000", "Error while mapping Embedded Id fields", e);
				}
			}
		}

		setCreatedDateTime(contextUser, entity);
		return entity;
	}

	public <T, D extends BaseEntity> List<D> setCreateMetaData(final Collection<T> dtoList,
			Class<? extends BaseEntity> entityClass) {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		String contextUser = authN.getName();
		List<D> entities = new ArrayList<>();

		dtoList.forEach(dto -> {
			D entity = (D) dataMapper.map(dto, entityClass, true, null, null, true);
			setCreatedDateTime(contextUser, entity);
			entities.add(entity);
		});
		
		return entities;

	}

	private <D extends BaseEntity> void setCreatedDateTime(String contextUser, D entity) {
		entity.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		entity.setCreatedBy(contextUser);
	}
	
	
public MachineHistory createdMachineHistory(Machine machine) {
		
		LocalDateTime etime = LocalDateTime.now(ZoneId.of("UTC"));
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		String contextUser = authN.getName();
		
		MachineHistory machineHistory = new MachineHistory() ;
		machineHistory.setId(machine.getId());
		machineHistory.setName(machine.getName());
		machineHistory.setMacAddress(machine.getMacAddress());
		machineHistory.setSerialNum(machine.getSerialNum());
		machineHistory.setIpAddress(machine.getIpAddress());
		machineHistory.setMspecId(machine.getMachineSpecId());
		machineHistory.setLangCode(machine.getLangCode());
		machineHistory.setIsActive(machine.getIsActive());
		machineHistory.setValEndDtimes(machine.getValidityDateTime());
		
		setCreatedDateTime(contextUser,machineHistory);
		machineHistory.setEffectDtimes(etime);
		
		
		return machineHistory;
		
	}

}

