package org.eclipse.epsilon.emc.json.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.epsilon.emc.json.JsonModel;
import org.eclipse.epsilon.eol.EolEvaluator;
import org.eclipse.epsilon.eol.types.EolSequence;
import org.junit.Before;
import org.junit.Test;

public class JSONModelReadTests {

	protected JsonModel model;
	protected EolEvaluator evaluator;

	@Before
	public void setUp() throws Exception {
		model = new JsonModel();
		model.setName("M");
		model.setReadOnLoad(true);
		model.setFile(new File("resources/commits.json"));

		assertFalse(model.isLoaded());
		model.load();
		assertTrue(model.isLoaded());

		evaluator = new EolEvaluator(model);
	}

	@Test
	public void rootIsArrayWithOneElement() {
		assertEquals(1, evaluator.evaluate("M.root.size()"));
	}

	@Test
	public void authorName() {
		assertEquals("Louis Rose", evaluator.evaluate("M.root.get(0).commit.author.name"));
	}

	@Test
	public void parentKeys() {
		assertEquals(sequence(1L, 23L),
			evaluator.evaluate("M.root.get(0).parents.collect(e|e.key)"));
	}

	@Test
	public void keySetMethod() {
		// A JsonObject is a Map, so we can use the usual Map methods. It's important
		// to note that JsonObject method names take precedence over actual property
		// names: when there is a property that has the same name as one of these methods,
		// we can say we want that property with a "p_" prefix (e.g. x.p_keySet).

		assertEquals(9, evaluator.evaluate("M.root.get(0).keySet().size()"));
	}

	@Test
	public void keySetProperty() {
		assertEquals(3, evaluator.evaluate("M.root.get(0).p_keySet.size()"));
	}

	@Test
	public void nonExistingProperty() {
		assertTrue((Boolean) evaluator.evaluate("M.root.nonExisting.isUndefined()"));
	}

	@Test
	public void nonExistingPrefixedProperty() {
		assertFalse((Boolean) evaluator.evaluate("M.root.p_nonExisting.isDefined()"));
	}

	@SafeVarargs
	protected final <T> EolSequence<T> sequence(T... values) {
		EolSequence<T> seq = new EolSequence<>();
		for (T value : values) {
			seq.add(value);
		}
		return seq;
	}
}
