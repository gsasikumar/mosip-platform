package io.mosip.registration.processor.core.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class PropertyFileUtil {

	static Logger log = LoggerFactory.getLogger(PropertyFileUtil.class);

	private PropertyFileUtil() {

	}

	/**
	 * Gets the property.
	 *
	 * @param clazz the clazz
	 * @param fileName the file name
	 * @param key the key
	 * @return the property
	 */
	public static String getProperty(Class<?> clazz, String fileName, String key) {

		Properties prop = new Properties();
		String value = null;

		try (InputStream input = clazz.getClassLoader().getResourceAsStream(fileName)) {

			if (input == null) {
				throw new FileNotFoundException("File Not available " + fileName);
			}
			prop.load(input);
			value = prop.getProperty(key);
		} catch (IOException ex) {
			log.error("Failed to read properties: " + ex.getCause());
		}
		return value;
	}
}
