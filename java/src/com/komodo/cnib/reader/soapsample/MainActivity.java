package com.komodo.cnib.reader.soapsample;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.xmlpull.v1.XmlPullParserException;

//import com.komodo.cnib.reader.utils.HttpTransportSE;

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
    private Session mSession = new Session();
    //private static final String SOAP_ACTION = "https://dodp.cniblibrary.com/DaisyOnlineService/DaisyOnlineService/logOn";

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
		
		Button btn_logon = (Button)findViewById(R.id.button1);
		Button btn_logoff = (Button)findViewById(R.id.button2);
		btn_logon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Thread th = new Thread(mLogOn);
				th.start();
			}
		});
		btn_logoff.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Thread th = new Thread(mLogOff);
				th.start();
			}
		});
		
	}
	
	private Runnable mLogOn = new Runnable () {

		@Override
		public void run() {
			ServerReply server_reply = serviceCall("logOn", null);
			processReply(server_reply);
		}
		
	};
	
	private Runnable mLogOff = new Runnable () {

		@Override
		public void run() {
			ServerReply server_reply = serviceCall("logOff", mSession.getHeaders());
			processReply(server_reply);
		}
		
	};
	
//	private class CNIBRequest extends AsyncTask<String, Integer, Long> {
//	    // Do the long-running work in here
//	    protected Long doInBackground(String... methods) {
//	        int count = methods.length;
//	        long totalSize = 0;
//	        return totalSize;
//	    }
//
//	    // This is called each time you call publishProgress()
//	    protected void onProgressUpdate(Integer... progress) {
//	        //setProgressPercent(progress[0]);
//	    }
//
//	    // This is called when doInBackground() is finished
//	    protected void onPostExecute(Long result) {
//	        //showNotification("Downloaded " + result + " bytes");
//	    }
//	}

	private void processReply(ServerReply reply) {
		String key = "";
		String value = "";
		String response = "";
		List<HeaderProperty> headers = reply.getHeaders();
		SoapSerializationEnvelope envelope = reply.getEnvelope();
		
		if (headers != null) {
			for (int i=0; i < headers.size(); i++) {
				key = headers.get(i).getKey();
				value = headers.get(i).getValue();
				if ((key != null) && (value != null)) {
					if (key.equalsIgnoreCase(Session.KEY_SET_COOKIE) && value.startsWith(Session.KEY_JSESSION_ID)) {
						Log.d(TAG, key + " found!: " + value);
//						int start_pos = Session.KEY_JSESSION_ID.length() + 1;
//						int end_pos = value.indexOf(";");
//						String cookie = value.substring(start_pos, end_pos);
						List<HeaderProperty> out_headers = new ArrayList<HeaderProperty>();
						out_headers.add(new HeaderProperty(Session.KEY_COOKIE, value));
						mSession.setHeaders(out_headers);
					}
				}
			}
		}

		if (envelope != null) {
			try {
				response = reply.getEnvelope().getResponse().toString();
				Log.d(TAG, response);
			} catch (SoapFault e) {
				e.printStackTrace();
			}
		}
	}
	
    private ServerReply serviceCall(String method, List<HeaderProperty> headers) {
    	ServerReply reply = new ServerReply();
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        SoapObject request = new SoapObject(CNIBServer.NAMESPACE, method);
        request.addProperty("username", "CNIB_204320");
        envelope.setOutputSoapObject(request);
        envelope.dotNet = true;

        HttpTransportSE httpTransport = new HttpTransportSE(CNIBServer.URL);
        //httpTransport.debug = true;

        String action = CNIBServer.URL + "/" + method;
        Log.d(TAG,action);
    	if (headers != null) {
    		Log.d(TAG, "Headers sent:");
    		logHeaders(headers);
    	}
        try {
			List<HeaderProperty> reply_headers = httpTransport.call(action, envelope, headers);
	    	if (reply_headers != null) {
	    		Log.d(TAG, "Headers received:");
	    		logHeaders(reply_headers);
	    	}
			reply.setHeaders(reply_headers);
			reply.setEnvelope(envelope);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

    	return reply;
    }
    
    private void logHeaders(List<HeaderProperty> headers) {
		String key = "";
		String value = "";
		if (headers != null) {
			for (int i=0; i < headers.size(); i++) {
				key = headers.get(i).getKey();
				value = headers.get(i).getValue();
				Log.d(TAG, key +" : " + value);
			}
		}
    }
    
    private class Session {
    	private static final String KEY_COOKIE = "Cookie";
    	private static final String KEY_SET_COOKIE = "Set-Cookie";
    	private static final String KEY_JSESSION_ID = "JSESSIONID";
    	private List<HeaderProperty> headers;
//    	private String cookie;

//		public String getCookie() {
//			return this.cookie;
//		}
//
//		public void setCookie(String cookie) {
//			String key = "";
//			Boolean has_cookie = false;
//			for (int i = 0; i < this.headers.size() ;i++) {
//				key = this.headers.get(i).getKey();
//				if (key != null) {
//					if (key.equals(KEY_COOKIE)) {
//						this.headers.set(i, new HeaderProperty(KEY_COOKIE, cookie));
//						has_cookie = true;
//					}
//				}
//			}
//			if (!has_cookie) {
//				headers.add(new HeaderProperty(KEY_COOKIE, cookie));
//			}
//			this.cookie = cookie;
//			Log.d(TAG, Session.KEY_COOKIE +" : " + cookie);
//		}

		public List<HeaderProperty> getHeaders() {
			return headers;
		}

		public void setHeaders(List<HeaderProperty> headers) {
			String key;
			for (int i = 0; i < headers.size() ;i++) {
				key = headers.get(i).getKey();
				if (key == null) {
					headers.remove(i);
				}
			}
			this.headers = headers;
		}
    }
    
    private class ServerReply {
    	private List<HeaderProperty> headers;
    	private SoapSerializationEnvelope envelope;
    	
		public List<HeaderProperty> getHeaders() {
			return headers;
		}
		public void setHeaders(List<HeaderProperty> headers) {
			this.headers = headers;
		}
		public SoapSerializationEnvelope getEnvelope() {
			return envelope;
		}
		public void setEnvelope(SoapSerializationEnvelope envelope) {
			this.envelope = envelope;
		}
    }
    
    private class CNIBServer {
    	public static final String NAMESPACE = "http://www.daisy.org/ns/daisy-online/";
    	public static final String URL = "https://dodp.cniblibrary.com/DaisyOnlineService/DaisyOnlineService?WSDL";
    }
}
