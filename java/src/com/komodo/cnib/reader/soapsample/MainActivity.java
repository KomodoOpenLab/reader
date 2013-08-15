package com.komodo.cnib.reader.soapsample;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.komodo.cnib.reader.utils.HttpTransportSE;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
    private static final String TAG = "CNIBClient";
    //private static final String SOAP_ACTION = "https://dodp.cniblibrary.com/DaisyOnlineService/DaisyOnlineService/logOn";
    private static final String METHOD_NAME = "logOn";
    private static final String NAMESPACE = "http://www.daisy.org/ns/daisy-online/";
    private static final String URL = "https://dodp.cniblibrary.com/DaisyOnlineService/DaisyOnlineService?WSDL";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void init() {
		
		Button btn_login = (Button)findViewById(R.id.button1);
		btn_login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new CNIBRequest().execute(METHOD_NAME);
			}
		});
		
	}
	
	private class CNIBRequest extends AsyncTask<String, Integer, Long> {
	    // Do the long-running work in here
	    protected Long doInBackground(String... methods) {
	        int count = methods.length;
	        long totalSize = 0;

	        try {
	            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

//	            PropertyInfo username = new PropertyInfo();
//	            username.name = "username";
//	            username.namespace = NAMESPACE;
//	            username.setValue("CNIB");
//	            PropertyInfo pwd = new PropertyInfo();
//	            pwd.name = "password";
//	            pwd.namespace = NAMESPACE;
//	            pwd.setValue("");
//	            request.addProperty(username);
//	            request.addProperty(pwd);
	            
	            //request.addProperty("SERVER_UID", "CNIB_204320");
	            request.addProperty("username", "cnib");
	            request.addProperty("password", "");

	            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
	            //envelope.dotNet=true;
	            envelope.setOutputSoapObject(request);
	            envelope.dotNet = true;

	            HttpTransportSE httpTransport = new HttpTransportSE(URL);
	            //HttpsTransportSE httpTransport = new HttpsTransportSE("dodp.cniblibrary.com", 443, "/DaisyOnlineService/DaisyOnlineService", 1000);

	            httpTransport.debug = true;
	            
                //final List<HeaderProperty> headerList = new ArrayList<HeaderProperty>();
                //headerList.add(new HeaderProperty("Authorization", "Basic "
                //        + org.kobjects.base64.Base64.encode((CNIBServer.CERTIFICATE_LOGIN + ":" + CNIBServer.CERTIFICATE_MDP).getBytes())));

                //FakeX509TrustManager.allowAllSSL();
	            String action = URL + "/" + METHOD_NAME;
                Log.d(TAG,action);
	            httpTransport.call(action, envelope);

	            //androidHttpsTransport.call(SOAP_ACTION, envelope);

	            SoapPrimitive result = (SoapPrimitive)envelope.getResponse();

	            //String resultData = result.getProperty(0).toString();
	            //tv.setText(resultData);
	            Log.d(TAG, "Response: " + result.toString());
	        } catch (Exception e) {
	        	//tv.setText(e.toString());
	            Log.e(TAG, "Exception: " + e.toString());
	            //Log.d(TAG, e.getMessage());
	        }
/*	        for (int i = 0; i < count; i++) {
	            totalSize += Downloader.downloadFile(urls[i]);
	            publishProgress((int) ((i / (float) count) * 100));
	            // Escape early if cancel() is called
	            if (isCancelled()) break;
	        }
*/	        return totalSize;
	    }

	    // This is called each time you call publishProgress()
	    protected void onProgressUpdate(Integer... progress) {
	        //setProgressPercent(progress[0]);
	    }

	    // This is called when doInBackground() is finished
	    protected void onPostExecute(Long result) {
	        //showNotification("Downloaded " + result + " bytes");
	    }
	}

    private static class FakeX509TrustManager implements X509TrustManager {
        private static TrustManager[] trustManagers;
        private final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return _AcceptedIssuers;
        }

        public static void allowAllSSL() {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(final String hostname, final SSLSession session) {
                    return true;
                }

            });
            SSLContext context = null;
            if (trustManagers == null) {
                trustManagers = new TrustManager[] { new FakeX509TrustManager() };
            }
            try {
                context = SSLContext.getInstance("TLS");
                context.init(null, trustManagers, new SecureRandom());
            }
            catch (final NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            catch (final KeyManagementException e) {
                e.printStackTrace();
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        }

		@Override
		public void checkClientTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
				throws java.security.cert.CertificateException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void checkServerTrusted(
				java.security.cert.X509Certificate[] chain, String authType)
				throws java.security.cert.CertificateException {
			// TODO Auto-generated method stub
			
		}
    }
    
    private class CNIBServer {
       	public final static String CERTIFICATE_LOGIN = " ";
       	public final static String CERTIFICATE_MDP = "";   	
    }
}
