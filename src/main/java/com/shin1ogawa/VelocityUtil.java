package com.shin1ogawa;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.log.JdkLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Appengine上でVelocityを使用するためのユーティリティクラス。
 * @author shin1ogawa
 */
public class VelocityUtil {

	static boolean initialized = false;

	static final Logger logger = Logger.getLogger(VelocityUtil.class.getName());


	private VelocityUtil() {
	}


	/**
	 * Velocityが投げたExceptionをラップするException.
	 * @author shin1ogawa
	 */
	@SuppressWarnings("serial")
	public static class VelocityRuntimeException extends RuntimeException {

		/**
		 * the constructor.
		 * @param cause
		 * @category constructor
		 */
		public VelocityRuntimeException(Throwable cause) {
			super(cause);
		}
	}


	/**
	 * VelocityのテンプレートとVelocityContextをマージし、クライアントへ応答を返す。
	 * @param context {@link VelocityContext#put(String, Object)}で値が設定された{@link VelocityContext}
	 * @param templateName リソースの名称。 例) "/template/hello.vm"
	 * @param response HttpServletResponse
	 * @throws VelocityRuntimeException 例外が発生した際にそれをラップした例外として投げる
	 * @see Template#merge(org.apache.velocity.context.Context, java.io.Writer)
	 */
	public static void merge(VelocityContext context, String templateName,
			HttpServletResponse response) throws VelocityRuntimeException {
		try {
			if (initialized == false) {
				initVelocity();
				initialized = true;
			}
			Template template = Velocity.getTemplate(templateName);
			template.merge(context, response.getWriter());
			response.getWriter().flush();
		} catch (Exception ex) {
			logger.log(Level.WARNING, "", ex);
			throw new VelocityRuntimeException(ex);
		}
	}

	/**
	 * Velocityの初期化を行う。
	 * @throws Exception
	 */
	static void initVelocity() throws Exception {
		Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM, new JdkLogChute());
		Properties properties = new Properties();
		properties.setProperty(Velocity.RESOURCE_LOADER, "CLASSPATH");
		properties.setProperty("CLASSPATH.resource.loader.class", ClasspathResourceLoader.class
			.getName());
		properties.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
		properties.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
		Velocity.init(properties);
	}
}
