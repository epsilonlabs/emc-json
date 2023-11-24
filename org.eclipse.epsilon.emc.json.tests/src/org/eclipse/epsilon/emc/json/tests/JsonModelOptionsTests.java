package org.eclipse.epsilon.emc.json.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.json.JsonModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.junit.Test;

public class JsonModelOptionsTests {

	@Test
	public void needUriOrFile() {
		try (JsonModel model = new JsonModel()) {
			model.setReadOnLoad(true);
			model.load();
			fail("An EolModelLoadingException is expected");
		} catch (EolModelLoadingException ex) {
			// pass!
		}
	}

	@Test
	public void loadFileWithProperty() throws Exception {
		try (JsonModel model = new JsonModel()) {
			StringProperties props = new StringProperties();
			props.put(JsonModel.PROPERTY_FILE, "resources/commits.json");
			props.put(JsonModel.PROPERTY_READONLOAD, "true");
			props.put(JsonModel.PROPERTY_STOREONDISPOSAL, "false");

			model.load(props);

			assertNotNull(model.getRoot());
		}
	}

	@Test
	public void loadFileWithFileURI() throws Exception {
		try (JsonModel model = new JsonModel()) {
			StringProperties props = new StringProperties();
			props.put(JsonModel.PROPERTY_URI, new File("resources/commits.json").getAbsoluteFile().toURI().toString());
			props.put(JsonModel.PROPERTY_READONLOAD, "true");
			props.put(JsonModel.PROPERTY_STOREONDISPOSAL, "false");

			model.load(props);

			assertNotNull(model.getRoot());
		}
	}

}
