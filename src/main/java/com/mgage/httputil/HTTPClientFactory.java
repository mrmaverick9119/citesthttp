/* @author Ratan Pandey
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mgage.httputil;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * 
 * @author Ratan
 */

class DisposableMultiThreadedHttpConnectionManagerV1 extends
		PoolingHttpClientConnectionManager {

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		try {
			closeIdleConnections(0, TimeUnit.SECONDS);
		} catch (Exception e) {
		}

		try {
			closeExpiredConnections();
		} catch (Exception e) {
		}

		try {
			shutdown();
		} catch (Exception e) {
		}

	}
}

public class HTTPClientFactory {

	private static DisposableMultiThreadedHttpConnectionManagerV1 connectionManager = null;
	private CloseableHttpClient httpClient = null;
	private CloseableHttpClient httpsClient = null;
	private static HTTPClientFactory httpClientFactory;
	RequestConfig requestConfig;
	public static Properties configAttributes = new Properties();

	public static String config_file_name;

	public static void initUtilityConfigurations() {
		configAttributes.clear();

		
		
		FileInputStream fis = null;

		try {
			File file = new File(config_file_name);
			fis = new FileInputStream(file);
			configAttributes.load(fis);
		} catch (Exception e) {
			System.out.println("All conf to be taken as default because Config properties Supplied is Invalid path:"+config_file_name);
			configAttributes = new Properties();
		} finally {
			closeResource(fis);
		}
	}


	private HTTPClientFactory() {

	}

	public static HTTPClientFactory getInstance() {
		if (httpClientFactory == null) {
			initializeHTTPClientFactory();
		}

		return httpClientFactory;
	}

	private static synchronized void initializeHTTPClientFactory() {
		// TODO Auto-generated method stub
		httpClientFactory = new HTTPClientFactory();
		initUtilityConfigurations();
		try {
			httpClientFactory.initializeConnectionManager();
		} catch (Exception e) {
			System.out.println("Exception initializeHTTPClientFactory"
					+ e.toString());
			e.printStackTrace();
		}
	}

	private void initializeConnectionManager() throws Exception {
		
		connectionManager = new DisposableMultiThreadedHttpConnectionManagerV1();
		// Increase max total connection to 200
		connectionManager.setMaxTotal(Integer.parseInt(configAttributes
				.getProperty("maxConnectionsPerHost", "20000")));
		connectionManager.setDefaultMaxPerRoute(Integer
				.parseInt(configAttributes.getProperty("maxHttpConnections",
						"1000")));
		requestConfig = RequestConfig
				.custom()
				.setSocketTimeout(
						Integer.parseInt(configAttributes.getProperty(
								"socketTimeOut", "2500")))
				.setConnectTimeout(
						Integer.parseInt(configAttributes.getProperty(
								"connectTimeOut", "2500"))).build();

		createHttpClient();
		createHttpsClient();
	}

	private void createHttpsClient() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("------------VV1createHttpsClient ----------------------------");
		try{
			String proptocol_conf = configAttributes.getProperty("ssl_instance");
			String protocol[] = null;
			if(proptocol_conf != null && proptocol_conf.length() > 1)
			{
				protocol= configAttributes.getProperty("ssl_instance", "").split(",");
				System.out.println("Adding  protocols  to "+protocol);
			}
			
			else
				{
				System.out.println("V1Adding Default protocols  to TLSv1 , SSLv3,TLSv1.1,TLSv1.2");
				//protocol = new String[] { "SSLv3" , "TLSv1" ,"TLSv1.1","TLSv1.2"};
				protocol = new String[] { "SSLv3" , "TLSv1" ,"TLSv1.1","TLSv1.2"};
				//protocol = new String[] { "SSLv3" , "TLSv1" };
				}

		String key_file_name = configAttributes.getProperty("sslpath");

		if(key_file_name == null)
		{
			// As of now just to refer  coded it  we need to have this configured in the property file .
			key_file_name = System.getProperty("java.home")+"/lib/security/cacerts";
		}

		System.out.println("Final  key_file_name  cerpath is taken as"+key_file_name);
		
		String pass = configAttributes.getProperty("sslp", "changeit");

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		FileInputStream instream = new FileInputStream(new File(key_file_name));
		try {
			trustStore.load(instream, pass.toCharArray());
		} finally {
			instream.close();
		}

		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContexts.custom()
				.loadTrustMaterial(null, new TrustStrategy(){
					public boolean isTrusted(
							java.security.cert.X509Certificate[] chain,
							String authType)
							throws java.security.cert.CertificateException {
						// TODO Auto-generated method stub
						return true;
					}
				    })
				.build();
	
		// Need to check setting configurable for
		// BROWSER_COMPATIBLE_HOSTNAME_VERIFIER
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext, protocol, null,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		
				
		//CustumSSLFactory factory = new CustumSSLFactory(key_file_name);
		
		httpsClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
	
		}
		catch(Exception e)
		{
			System.out.println("Exception createHttpsClient ----------------------------"+e.getMessage());	
		}

	}

	private void createHttpClient() {
		// TODO Auto-generated method stub
		httpClient = HttpClients.custom()
				.setConnectionManager(connectionManager).build();

	}

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public CloseableHttpClient getHttpsClient() {
		return httpsClient;
	}

	public void setHttpSSLClient(CloseableHttpClient httpsClient) {
		this.httpsClient = httpsClient;
	}

	public static void closeResource(Closeable closeable) {
		try {
			closeable.close();
		} catch (Exception e) {
		}
	}

	void closeHttpClients() {
		try {

			if (httpClient != null) {
				httpClient.close();
			}

			if (httpsClient != null) {
				httpsClient.close();
			}
		} catch (Exception e) {

		}
	}

	public static void Destroy() {

		try {
			httpClientFactory.closeHttpClients();
		} catch (Exception e) {

		}
		try {

			connectionManager.closeIdleConnections(0, TimeUnit.SECONDS);
			connectionManager.shutdown();

		} catch (Exception e) {
		}

		try {
			connectionManager.shutdown();
			;
		} catch (Exception e) {
		}

	}
}
