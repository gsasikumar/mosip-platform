package org.mosip.kernel.dataaccess.repository.impl;

import java.util.List;
import java.util.Map;

import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.HibernateException;
import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.kernel.dataaccess.constants.HibernateErrorCodes;
import org.mosip.kernel.dataaccess.constants.HibernatePersistenceConstants;
import org.mosip.kernel.dataaccess.exception.DataAccessLayerException;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of the {@link BaseRepository} interface.
 * 
 * @author Dharmesh Khandelwal
 * @author Shashank Agrawal
 * @since 1.0.0
 * 
 * @param <E>
 *            the type of the entity to handle
 * @param <T>
 *            the type of the entity's identifier
 */
@Transactional
@SuppressWarnings({ "unchecked", "rawtypes" })
public class HibernateRepositoryImpl<E, T> extends SimpleJpaRepository<E, T> implements BaseRepository<E, T> {

	/**
	 * Field for interface used to interact with the persistence context.
	 */
	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Constructor for HibernateRepositoryImpl having JpaEntityInformation and
	 * EntityManager
	 * 
	 * @param entityInformation
	 *            The entityInformation
	 * @param entityManager
	 *            The entityManager
	 */
	public HibernateRepositoryImpl(JpaEntityInformation<E, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.dao.repository.BaseRepository#create(java.lang.Object)
	 */
	@Override
	public E create(E entity) {
		try {
			entityManager.persist(entity);
			entityManager.flush();
		} catch (HibernateException hibernateException) {
			throw new DataAccessLayerException(HibernateErrorCodes.HIBERNATE_EXCEPTION, hibernateException.getMessage(),
					hibernateException);
		} catch (RuntimeException exception) {
			throw new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, exception.getMessage(), exception);
		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.dao.repository.BaseRepository#update(java.lang.Object)
	 */
	@Override
	public E update(E entity) {
		try {
			entity = entityManager.merge(entity);
			entityManager.flush();
		} catch (HibernateException hibernateException) {
			throw new DataAccessLayerException(HibernateErrorCodes.HIBERNATE_EXCEPTION, hibernateException.getMessage(),
					hibernateException);
		} catch (RuntimeException exception) {
			throw new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, exception.getMessage(), exception);
		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.dao.repository.BaseRepository#findById(java.lang.Class,
	 * java.lang.Object)
	 */
	@Override
	public E findById(Class<E> entityClass, T id) {
		try {
			return entityManager.find(entityClass, id);
		} catch (HibernateException hibernateException) {
			throw new DataAccessLayerException(HibernateErrorCodes.HIBERNATE_EXCEPTION, hibernateException.getMessage(),
					hibernateException);
		} catch (RuntimeException exception) {
			throw new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, exception.getMessage(), exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.dao.repository.BaseRepository#findAll(java.lang.Class)
	 */
	@Override
	public List<E> findAll(Class<E> entityClass) {
		List<E> entityList = null;
		try {
			CriteriaQuery criteriaQuery = entityManager.getCriteriaBuilder().createQuery();
			criteriaQuery.select(criteriaQuery.from(entityClass));
			Query query = entityManager.createQuery(criteriaQuery);
			query.setHint(HibernatePersistenceConstants.CACHE_QUERY_PROPERTY, CacheStoreMode.REFRESH);
			entityList = query.getResultList();
		} catch (HibernateException hibernateException) {
			throw new DataAccessLayerException(HibernateErrorCodes.HIBERNATE_EXCEPTION, hibernateException.getMessage(),
					hibernateException);
		} catch (RuntimeException exception) {
			throw new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, exception.getMessage(), exception);
		}
		return entityList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.dao.repository.BaseRepository#delete(java.lang.Class,
	 * java.lang.Object)
	 */
	@Override
	public T delete(Class<E> entityClass, T id) {
		try {
			E entityToBeDeleted = findById(entityClass, id);
			entityManager.remove(entityToBeDeleted);
			return id;
		} catch (HibernateException hibernateException) {
			throw new DataAccessLayerException(HibernateErrorCodes.HIBERNATE_EXCEPTION, hibernateException.getMessage(),
					hibernateException);
		} catch (RuntimeException exception) {
			throw new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, exception.getMessage(), exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.dao.repository.BaseRepository#createQuerySelect(java.
	 * lang.String, java.util.Map)
	 */
	@Override
	public List<E> createQuerySelect(String qlString, Map<String, Object> params) {
		List<E> entityList = null;
		try {
			Query query = entityManager.createQuery(qlString);
			query.setHint(HibernatePersistenceConstants.CACHE_QUERY_PROPERTY, CacheStoreMode.REFRESH);
			if (params != null) {
				params.forEach(query::setParameter);
			}
			entityList = query.getResultList();
		} catch (NoResultException noResultException) {
			throw new DataAccessLayerException(HibernateErrorCodes.NO_RESULT_EXCEPTION, noResultException.getMessage(),
					noResultException);
		} catch (HibernateException hibernateException) {
			throw new DataAccessLayerException(HibernateErrorCodes.HIBERNATE_EXCEPTION, hibernateException.getMessage(),
					hibernateException);
		} catch (RuntimeException exception) {
			throw new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, exception.getMessage(), exception);
		}
		return entityList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.dao.repository.BaseRepository#createQuerySelect(java.
	 * lang.String, java.util.Map, int)
	 */
	@Override
	public List<E> createQuerySelect(String qlString, Map<String, Object> params, int limit) {
		List<E> entityList = null;
		try {
			Query query = entityManager.createQuery(qlString);
			query.setHint(HibernatePersistenceConstants.CACHE_QUERY_PROPERTY, CacheStoreMode.REFRESH);
			if (params != null) {
				params.forEach(query::setParameter);
			}
			query.setMaxResults(limit);
			entityList = query.getResultList();
		} catch (NoResultException noResultException) {
			throw new DataAccessLayerException(HibernateErrorCodes.NO_RESULT_EXCEPTION, noResultException.getMessage(),
					noResultException);
		} catch (HibernateException hibernateException) {
			throw new DataAccessLayerException(HibernateErrorCodes.HIBERNATE_EXCEPTION, hibernateException.getMessage(),
					hibernateException);
		} catch (RuntimeException exception) {
			throw new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, exception.getMessage(), exception);
		}
		return entityList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.dao.repository.BaseRepository#createQueryUpdateOrDelete
	 * (java.lang.String, java.util.Map)
	 */
	@Override
	public int createQueryUpdateOrDelete(String qlString, Map<String, Object> params) {
		int entitiesCount;
		try {
			Query query = entityManager.createQuery(qlString);
			if (params != null) {
				params.forEach(query::setParameter);
			}
			entitiesCount = query.executeUpdate();
		} catch (NoResultException noResultException) {
			throw new DataAccessLayerException(HibernateErrorCodes.NO_RESULT_EXCEPTION, noResultException.getMessage(),
					noResultException);
		} catch (HibernateException hibernateException) {
			throw new DataAccessLayerException(HibernateErrorCodes.HIBERNATE_EXCEPTION, hibernateException.getMessage(),
					hibernateException);
		} catch (RuntimeException exception) {
			throw new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, exception.getMessage(), exception);
		}
		return entitiesCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.dao.repository.BaseRepository#createNamedQuerySelect(
	 * java.lang.String, java.lang.Class, java.util.Map)
	 */
	@Override
	public List<E> createNamedQuerySelect(String name, Class<E> entityClass, Map<String, Object> params) {
		List<E> entityList = null;
		try {
			Query query = entityManager.createNamedQuery(name, entityClass);
			query.setHint(HibernatePersistenceConstants.CACHE_QUERY_PROPERTY, CacheStoreMode.REFRESH);
			if (params != null) {
				params.forEach(query::setParameter);
			}
			entityList = query.getResultList();
		} catch (NoResultException noResultException) {
			throw new DataAccessLayerException(HibernateErrorCodes.NO_RESULT_EXCEPTION, noResultException.getMessage(),
					noResultException);
		} catch (HibernateException hibernateException) {
			throw new DataAccessLayerException(HibernateErrorCodes.HIBERNATE_EXCEPTION, hibernateException.getMessage(),
					hibernateException);
		} catch (RuntimeException exception) {
			throw new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, exception.getMessage(), exception);
		}
		return entityList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.kernel.core.dao.repository.BaseRepository#
	 * createNamedQueryUpdateOrDelete(java.lang.String, java.lang.Class,
	 * java.util.Map)
	 */
	@Override
	public int createNamedQueryUpdateOrDelete(String name, Class<E> entityClass, Map<String, Object> params) {
		int entitiesCount;
		try {
			Query query = entityManager.createNamedQuery(name, entityClass);
			if (params != null) {
				params.forEach(query::setParameter);
			}
			entitiesCount = query.executeUpdate();
		} catch (NoResultException noResultException) {
			throw new DataAccessLayerException(HibernateErrorCodes.NO_RESULT_EXCEPTION, noResultException.getMessage(),
					noResultException);
		} catch (HibernateException hibernateException) {
			throw new DataAccessLayerException(HibernateErrorCodes.HIBERNATE_EXCEPTION, hibernateException.getMessage(),
					hibernateException);
		} catch (RuntimeException exception) {
			throw new DataAccessLayerException(HibernateErrorCodes.ERR_DATABASE, exception.getMessage(), exception);
		}
		return entitiesCount;
	}
}
