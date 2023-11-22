/*******************************************************************************
 * Copyright (c) 2022-2023 The University of York.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Sina Madani - initial API and implementation
 *     Antonio Garcia-Dominguez - complete implementation and add tests/docs
 ******************************************************************************/
package org.eclipse.epsilon.emc.json;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.epsilon.common.util.FileUtil;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolEnumerationValueNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.exceptions.models.EolNotInstantiableModelElementTypeException;
import org.eclipse.epsilon.eol.models.CachedModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JsonModel extends CachedModel<Object> {

	public static final String PROPERTY_FILE = "file";
	public static final String PROPERTY_URI = "uri";
	public static final String PROPERTY_USERNAME = "username";
	public static final String PROPERTY_PASSWORD = "password";

	protected File file;
	protected String uri;

	protected String username;
	protected String password;

	protected Object root;

	public JsonModel() {
		propertyGetter = new JsonPropertyGetter();
	}

	public Object getRoot() {
		return root;
	}

	public void setRoot(Object root) {
		this.root = root;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public Object getEnumerationValue(String enumeration, String label) throws EolEnumerationValueNotFoundException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getTypeNameOf(Object instance) {
		return instance.getClass().getSimpleName();
	}

	@Override
	public Object getElementById(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getElementId(Object instance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setElementId(Object instance, String newId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean owns(Object instance) {
		return instance instanceof JSONObject || instance instanceof JSONArray;
	}

	@Override
	public boolean isInstantiable(String type) {
		return hasType(type);
	}

	@Override
	public boolean hasType(String type) {
		return JSONObject.class.getSimpleName().equals(type) || JSONArray.class.getSimpleName().equals(type);
	}

	@Override
	public boolean store(String location) {
		try {
			FileUtil.setFileContents(JSONValue.toJSONString(root), new File(location));
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean store() {
		if (file != null) {
			return store(file.getAbsolutePath());
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	protected Collection<Object> allContentsFromModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<Object> getAllOfTypeFromModel(String type) throws EolModelElementTypeNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<Object> getAllOfKindFromModel(String kind) throws EolModelElementTypeNotFoundException {
		return getAllOfTypeFromModel(kind);
	}

	@Override
	protected Object createInstanceInModel(String type)
			throws EolModelElementTypeNotFoundException, EolNotInstantiableModelElementTypeException {

		if (JSONObject.class.getSimpleName().equals(type)) {
			return new JSONObject();
		} else if (JSONArray.class.getSimpleName().equals(type)) {
			return new JSONArray();
		}
		throw new EolModelElementTypeNotFoundException(this.getName(), type);
	}

	@Override
	public void load(StringProperties properties, IRelativePathResolver resolver) throws EolModelLoadingException {
		super.load(properties, resolver);

		String filePath = properties.getProperty(JsonModel.PROPERTY_FILE);

		if (filePath != null && filePath.trim().length() > 0) {
			file = new File(resolver.resolve(filePath));
		} else {
			uri = properties.getProperty(JsonModel.PROPERTY_URI);
			username = properties.getProperty(JsonModel.PROPERTY_USERNAME);
			password = properties.getProperty(JsonModel.PROPERTY_PASSWORD);
		}

		load();
	}

	@Override
	protected void loadModel() throws EolModelLoadingException {
		if (!readOnLoad)
			return;

		try {
			if (uri != null) {
				URI parsedUri = new URI(uri);

				HttpClientBuilder builder = HttpClients.custom();
				if (username != null) {
					BasicCredentialsProvider creds = new BasicCredentialsProvider();
					creds.setCredentials(new AuthScope(parsedUri.getHost(), parsedUri.getPort()),
							new UsernamePasswordCredentials(username, password));

					builder.setDefaultCredentialsProvider(creds);
				}

				try (CloseableHttpClient httpClient = builder.build()) {
					HttpGet httpGet = new HttpGet(uri);

					HttpResponse httpResponse = httpClient.execute(httpGet);
					HttpEntity responseEntity = httpResponse.getEntity();

					Reader reader = new InputStreamReader(responseEntity.getContent(), StandardCharsets.UTF_8);
					root = JSONValue.parse(reader);
				}
			} else if (file != null) {
				try (Reader reader = new FileReader(file, StandardCharsets.UTF_8)) {
					root = JSONValue.parse(reader);
				}
			} else {
				throw new IllegalStateException("Neither URI nor file path have been set");
			}
		} catch (Exception ex) {
			throw new EolModelLoadingException(ex, this);
		}
	}

	@Override
	protected void disposeModel() {
		root = null;
	}

	@Override
	protected boolean deleteElementInModel(Object instance) throws EolRuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Object getCacheKeyForType(String type) throws EolModelElementTypeNotFoundException {
		return null;
	}

	@Override
	protected Collection<String> getAllTypeNamesOf(Object instance) {
		return Arrays.asList(instance.getClass().getSimpleName());
	}

	public boolean isLoaded() {
		return root != null;
	}
}
