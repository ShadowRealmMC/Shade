package io.shadowrealm.shade.common;

import java.io.IOException;

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class BasicAuthInterceptor implements Interceptor {

	private String credentials;

	public BasicAuthInterceptor(String user, String password) {
		this.credentials = Credentials.basic(user, password);
	}

	@Override
	public Response intercept(Chain chain) throws IOException {
		Request request = chain.request();
		Request authenticatedRequest = request.newBuilder()
				.header("Authorization", credentials).build();
		return chain.proceed(authenticatedRequest);
	}

}