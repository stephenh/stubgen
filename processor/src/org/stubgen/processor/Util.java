package org.stubgen.processor;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import joist.sourcegen.GClass;

public class Util {

	public static void saveCode(ProcessingEnvironment env, GClass g, Element... originals) {
		try {
			JavaFileObject jfo = env.getFiler().createSourceFile(g.getFullClassNameWithoutGeneric(), originals);
			Writer w = jfo.openWriter();
			w.write(g.toCode());
			w.close();
		} catch (IOException io) {
			env.getMessager().printMessage(Kind.ERROR, io.getMessage());
		}
	}

}
