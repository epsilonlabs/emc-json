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

import org.eclipse.epsilon.emc.plainxml.PlainXmlProperty;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.introspection.java.JavaPropertyGetter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonPropertyGetter extends JavaPropertyGetter {
	
	@Override
	public Object invoke(Object object, String property, IEolContext context) throws EolRuntimeException {
		JSONObject jsonObject = (JSONObject) object;
		PlainXmlProperty p = PlainXmlProperty.parse(property);

		if (p != null) {
			if (p.isAttribute()) {
				return p.cast(jsonObject.get(p.getProperty()) + "");
			}
			else if (p.isElement()) {
				return jsonObject.get(p.getProperty());
			}
			else if (p.isMany()) {
				Object result = jsonObject.get(p.getProperty());
				if (result == null) {
					return new JSONArray();
				}
				else {
					return result;
				}
			}
		}
		
		return super.invoke(object, property, context);
	}
	
}
