package org.eclipse.epsilon.emc.json;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.epsilon.common.util.FileUtil;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.plainxml.PlainXmlModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolEnumerationValueNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.exceptions.models.EolNotInstantiableModelElementTypeException;
import org.eclipse.epsilon.eol.execute.introspection.IPropertyGetter;
import org.eclipse.epsilon.eol.models.CachedModel;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JsonModel extends CachedModel<Object>{

	public static String PROPERTY_FILE = "file";
	public static String PROPERTY_URI = "uri";
	public static String PROPERTY_USERNAME = "username";
	public static String PROPERTY_PASSWORD = "password";
	
	protected File file;
	protected String uri;
	protected String username;
	protected String password;
	protected Object root = null;
	
	public static void main(String[] args) throws Exception {
		JsonModel model = new JsonModel();
		model.setName("M");
		model.setFile(new File("commits.json"));
		model.load();
		
		EolModule module = new EolModule();
		module.getContext().getModelRepository().addModel(model);
		module.parse("M.root[0].e_commit.e_author.a_name.println();");
		module.execute();		
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
	public Object getEnumerationValue(String enumeration, String label)
			throws EolEnumerationValueNotFoundException {
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
		return instance instanceof JSONObject /*||
			instance instanceof JSONArray*/;
	}

	@Override
	public boolean isInstantiable(String type) {
		return hasType(type);
	}

	@Override
	public boolean hasType(String type) {
		return JSONObject.class.getSimpleName().equals(type) || 
			JSONArray.class.getSimpleName().equals(type);
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
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	protected Collection<Object> allContentsFromModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<Object> getAllOfTypeFromModel(String type)
			throws EolModelElementTypeNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<Object> getAllOfKindFromModel(String kind)
			throws EolModelElementTypeNotFoundException {
		return getAllOfTypeFromModel(kind);
	}

	@Override
	protected Object createInstanceInModel(String type)
			throws EolModelElementTypeNotFoundException,
			EolNotInstantiableModelElementTypeException {
		
		if (JSONObject.class.getSimpleName().equals(type)) {
			return new JSONObject();
		}
		else if (JSONArray.class.getSimpleName().equals(type)) {
			return new JSONArray();
		}
		throw new EolModelElementTypeNotFoundException(this.getName(), type);
	}

	@Override
	public void load(StringProperties properties, IRelativePathResolver resolver)
			throws EolModelLoadingException {
		
		super.load(properties, resolver);
		
		String filePath = properties.getProperty(PlainXmlModel.PROPERTY_FILE);
		
		if (filePath != null && filePath.trim().length() > 0) {
			file = new File(resolver.resolve(filePath));
		}
		else {
			uri = properties.getProperty(JsonModel.PROPERTY_URI);
			username = properties.getProperty(JsonModel.PROPERTY_USERNAME);
			password = properties.getProperty(JsonModel.PROPERTY_PASSWORD);
		}
		
		load();
	}
	
	@Override
	protected void loadModel() throws EolModelLoadingException {
		try {
			if (readOnLoad) {
				Reader reader = null;
				if (file == null) {
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet(uri);
					if (username != null) {
						httpGet.addHeader(BasicScheme.authenticate(
								 new UsernamePasswordCredentials(username, password),
								 "UTF-8", false));
					}
					HttpResponse httpResponse = httpClient.execute(httpGet);
					HttpEntity responseEntity = httpResponse.getEntity();
					reader = new InputStreamReader(responseEntity.getContent());
				}
				else {
					reader = new FileReader(file);
				}
				root = JSONValue.parse(reader);
			}
		}
		catch (Exception ex) {
			throw new EolModelLoadingException(ex, this);
		}
	}

	@Override
	protected void disposeModel() {
		root = null;
	}

	protected JsonPropertyGetter propertyGetter = new JsonPropertyGetter();
	
	@Override
	public IPropertyGetter getPropertyGetter() {
		return propertyGetter;
	}
	
	
	@Override
	protected boolean deleteElementInModel(Object instance)
			throws EolRuntimeException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Object getCacheKeyForType(String type)
			throws EolModelElementTypeNotFoundException {
		return null;
	}

	@Override
	protected Collection<String> getAllTypeNamesOf(Object instance) {
		return Arrays.asList(instance.getClass().getSimpleName());
	}
	
}
