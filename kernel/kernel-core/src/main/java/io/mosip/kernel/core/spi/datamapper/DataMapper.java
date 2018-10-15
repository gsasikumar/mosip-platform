package io.mosip.kernel.core.spi.datamapper;

/**
 * The main runtime interface between a Java application and a Data Mapper. This is the
 * central interface abstracting the service of a Java bean mapping.
 * 
 * The operation of mapping may include : <br>
 * <ul>
 * <li>Creation of new objects : <code>newObject()</code></li>
 * <li>Conversion object to another type: <code>convert()</code></li>
 * </ul>
 * <br>
 * 
 * Example of code to map an instance of <code>Entity</code>(<code>entity</code>
 * ) to <code>DTO</code> class:<br>
 * 
 * <pre>
 * ...
 * DTO newDTO = mapperFacade.map(entity, DTO.class);
 * ...
 * </pre>
 * 
 * @author Neha
 * @since 1.0.0
 * 
 * @param <S>
 *            the type of the source object
 * @param <D>
 *            the type of the destination object
 */
public interface DataMapper <S, D> {

	/**
     * Create and return a new instance of type D mapped with the properties of
     * <code>sourceObject</code>.
     * 
     * @param sourceObject
     *            the object to map from
     * @param destinationClass
     *            the type of the new object to return
     * @return a new instance of type D mapped with the properties of
     *         <code>sourceObject</code>
     */
	public D map(S source, Class<D> destinationClass);

	/**
     * Maps the properties of <code>sourceObject</code> onto
     * <code>destinationObject</code>.
     * 
     * @param sourceObject
     *            the object from which to read the properties
     * @param destinationObject
     *            the object onto which the properties should be mapped
     */
	public void mapObjects(S source, D destination);

}
