package com.flattr4android.rest;

import oauth.signpost.basic.DefaultOAuthConsumer;

@SuppressWarnings("serial")
public class FlattrOAuthConsumer extends DefaultOAuthConsumer {

	public FlattrOAuthConsumer(String consumerKey, String consumerSecret) {
		super(consumerKey, consumerSecret);
	}

}
