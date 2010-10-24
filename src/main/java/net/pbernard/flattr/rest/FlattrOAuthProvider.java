package net.pbernard.flattr.rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.basic.HttpURLConnectionRequestAdapter;
import oauth.signpost.http.HttpRequest;

@SuppressWarnings("serial")
public class FlattrOAuthProvider extends DefaultOAuthProvider {

	public FlattrOAuthProvider() {
		super(FlattrRestClient.REQUEST_TOKEN_ENDPOINT_URL,
				FlattrRestClient.ACCESS_TOKEN_ENDPOINT_URL,
				FlattrRestClient.AUTHORIZATION_WEBSITE_URL);
		// Enable v1.0a to have the verifier/PIN sent
		// See http://forum.flattr.net/showthread.php?tid=486
		setOAuth10a(true);
	}

	@Override
	protected HttpRequest createRequest(String endpointUrl)
			throws MalformedURLException, IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(endpointUrl)
				.openConnection();

		// On Android, there is an issue with HTTP headers.
		// See for example
		// http://mail-archives.apache.org/mod_mbox/harmony-commits/201002.mbox/%3C1938314103.387611266583707925.JavaMail.jira@brutus.apache.org%3E
		// That causes another issue with the Content-Length header,
		// which is not sent while mandatory in a POST request with Flattr.
		// Thus a GET is used insteadn abd Content-Length is not set anymore.
		connection.setRequestMethod("GET");

		connection.setAllowUserInteraction(false);
		return new HttpURLConnectionRequestAdapter(connection);
	}
}
