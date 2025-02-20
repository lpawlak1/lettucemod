package com.redis.lettucemod.util;

import java.time.Duration;
import java.util.Optional;

import io.lettuce.core.RedisCredentialsProvider;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SslVerifyMode;

public class RedisURIBuilder {

	public static final String DEFAULT_HOST = "localhost";
	public static final int DEFAULT_PORT = RedisURI.DEFAULT_REDIS_PORT;
	private static final int DEFAULT_DATABASE = 0;

	private Optional<RedisURI> uri = Optional.empty();
	private String host = DEFAULT_HOST;
	private int port = DEFAULT_PORT;
	private boolean ssl;
	private boolean startTls;
	private Optional<SslVerifyMode> sslVerifyMode = Optional.empty();
	private Optional<String> socket = Optional.empty();
	private String username;
	private char[] password;
	private Optional<RedisCredentialsProvider> credentialsProvider = Optional.empty();
	private int database = DEFAULT_DATABASE;
	private Optional<Duration> timeout = Optional.empty();
	private Optional<String> clientName = Optional.empty();

	public RedisURIBuilder host(String host) {
		this.host = host;
		return this;
	}

	public RedisURIBuilder port(int port) {
		this.port = port;
		return this;
	}

	public RedisURIBuilder uri(RedisURI uri) {
		this.uri = Optional.of(uri);
		return this;
	}

	public RedisURIBuilder uri(String uri) {
		return uri(Optional.ofNullable(uri));
	}

	public RedisURIBuilder uri(Optional<String> uri) {
		this.uri = uri.map(RedisURI::create);
		return this;
	}

	public RedisURIBuilder ssl(boolean ssl) {
		this.ssl = ssl;
		return this;
	}

	public RedisURIBuilder startTls(boolean startTls) {
		this.startTls = startTls;
		return this;
	}

	public RedisURIBuilder sslVerifyMode(Optional<SslVerifyMode> sslVerifyMode) {
		this.sslVerifyMode = sslVerifyMode;
		return this;
	}

	public RedisURIBuilder sslVerifyMode(SslVerifyMode sslVerifyMode) {
		return sslVerifyMode(Optional.of(sslVerifyMode));
	}

	public RedisURIBuilder socket(String socket) {
		return socket(Optional.ofNullable(socket));
	}

	public RedisURIBuilder socket(Optional<String> socket) {
		this.socket = socket;
		return this;
	}

	public RedisURIBuilder username(String username) {
		this.username = username;
		return this;
	}

	public RedisURIBuilder password(String password) {
		if (password == null) {
			return this;
		}
		return password(password.toCharArray());
	}

	public RedisURIBuilder password(char[] password) {
		this.password = password;
		return this;
	}

	public RedisURIBuilder credentialsProvider(RedisCredentialsProvider credentialsProvider) {
		return credentialsProvider(Optional.ofNullable(credentialsProvider));
	}

	public RedisURIBuilder credentialsProvider(Optional<RedisCredentialsProvider> credentialsProvider) {
		this.credentialsProvider = credentialsProvider;
		return this;
	}

	public RedisURIBuilder database(int database) {
		this.database = database;
		return this;
	}

	public RedisURIBuilder timeoutInSeconds(long timeout) {
		return timeout(Duration.ofSeconds(timeout));
	}

	public RedisURIBuilder timeout(Duration timeout) {
		return timeout(Optional.ofNullable(timeout));
	}

	public RedisURIBuilder timeout(Optional<Duration> timeout) {
		this.timeout = timeout;
		return this;
	}

	public RedisURIBuilder clientName(String clientName) {
		return clientName(Optional.ofNullable(clientName));
	}

	public RedisURIBuilder clientName(Optional<String> clientName) {
		this.clientName = clientName;
		return this;
	}

	public RedisURI build() {
		RedisURI.Builder builder = uri.map(RedisURI::builder).orElse(RedisURI.builder().withHost(host).withPort(port));
		if (database != DEFAULT_DATABASE) {
			builder.withDatabase(database);
		}
		if (ssl) {
			builder.withSsl(ssl);
		}
		if (startTls) {
			builder.withStartTls(startTls);
		}
		sslVerifyMode.ifPresent(builder::withVerifyPeer);
		credentialsProvider.ifPresent(builder::withAuthentication);
		if (password != null && password.length > 0) {
			if (username == null) {
				builder.withPassword(password);
			} else {
				builder.withAuthentication(username, password);
			}
		}
		clientName.ifPresent(builder::withClientName);
		timeout.ifPresent(builder::withTimeout);
		RedisURI redisURI = builder.build();
		socket.ifPresent(redisURI::setSocket);
		return redisURI;
	}

	public static RedisURIBuilder create(String uri) {
		return new RedisURIBuilder().uri(uri);
	}

	public static RedisURIBuilder create() {
		return new RedisURIBuilder();
	}

}