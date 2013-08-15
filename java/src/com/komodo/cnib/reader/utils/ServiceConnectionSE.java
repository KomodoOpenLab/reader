/* Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 * Copyright (c) 2006, James Seigel, Calgary, AB., Canada
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */

package com.komodo.cnib.reader.utils;

import java.io.*;
import java.net.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

/**
 * Connection for J2SE environments.
 */
public class ServiceConnectionSE implements ServiceConnection {

    private HttpURLConnection connection;
    
    private TrustManager[] trustAllCerts = new TrustManager[] {
		new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		}
    };

    /**
     * Constructor taking the url to the endpoint for this soap communication
     * @param url the url to open the connection to.
     */
//    public ServiceConnectionSE(String url) throws IOException {
//        connection = (HttpURLConnection) new URL(url).openConnection();
//        connection.setUseCaches(false);
//        connection.setDoOutput(true);
//        connection.setDoInput(true);
//    }

    public ServiceConnectionSE(String url) throws IOException {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.getMessage();
        }
        connection = (HttpsURLConnection) new URL(url).openConnection();
        ((HttpsURLConnection) connection).setHostnameVerifier(new AllowAllHostnameVerifier());
    }
    
    public ServiceConnectionSE(Proxy proxy, String url) throws IOException {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
         } catch (Exception e) {
            e.getMessage();
         }
         connection = (HttpsURLConnection) new URL(url).openConnection();
        ((HttpsURLConnection) connection).setHostnameVerifier(new AllowAllHostnameVerifier());

        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.setDoInput(true);
    }
    
    public void connect() throws IOException {
        connection.connect();
    }

    public void disconnect() {
        connection.disconnect();
    }

    public void setRequestProperty(String string, String soapAction) {
        connection.setRequestProperty(string, soapAction);
    }

    public void setRequestMethod(String requestMethod) throws IOException {
        connection.setRequestMethod(requestMethod);
    }

    public OutputStream openOutputStream() throws IOException {
        return connection.getOutputStream();
    }

    public InputStream openInputStream() throws IOException {
        return connection.getInputStream();
    }

    public InputStream getErrorStream() {
        return connection.getErrorStream();
    }

    
}
