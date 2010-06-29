package org.stubgen.processor;

import static javax.lang.model.util.ElementFilter.methodsIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;
import joist.util.Join;

import org.stubgen.GenStub;

@SupportedAnnotationTypes({ "org.stubgen.GenStub" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Processor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getElementsAnnotatedWith(GenStub.class)) {
			if (element.getKind() == ElementKind.CLASS) {
				TypeElement te = (TypeElement) element;
				if (te.getInterfaces().size() > 0) {
					generate((TypeElement) element, (DeclaredType) te.getInterfaces().get(0));
				}
			}
		}
		return true;
	}

	private void generate(TypeElement userClass, DeclaredType stubInterface) {
		final ProcessingEnvironment pe = processingEnv;
		TypeElement stubInterfaceType = (TypeElement) stubInterface.asElement();

		String stubClassName = userClass.toString() + "Base";
		String interfaceName = stubInterface.toString();
		// if the TypeElement has parameters, but the TypeMirror does not, it's a raw type, so we add on the type parameters
		if (stubInterface.getTypeArguments().size() == 0 && stubInterfaceType.getTypeParameters().size() > 0) {
			List<String> tps = new ArrayList<String>();
			for (TypeParameterElement tpse : stubInterfaceType.getTypeParameters()) {
				tps.add(tpse.toString());
			}
			interfaceName += "<" + Join.commaSpace(tps) + ">";
			stubClassName += "<" + Join.commaSpace(tps) + ">";
		}

		GClass g = new GClass(stubClassName);
		g.implementsInterface(interfaceName);
		g.addAnnotation("@SuppressWarnings(\"all\")");

		for (ExecutableElement method : methodsIn(pe.getElementUtils().getAllMembers((TypeElement) stubInterface.asElement()))) {
			if ("java.lang.Object".equals(method.getEnclosingElement().toString())) {
				continue;
			}
			if (method.getSimpleName().toString().equals("hashCode") || method.getSimpleName().toString().equals("equals")) {
				continue;
			}

			List<String> params = new ArrayList<String>();
			for (VariableElement p : method.getParameters()) {
				String parameterType = resolveTypeVars(p.asType(), stubInterface).toString();
				params.add(parameterType + " " + p.getSimpleName().toString());
			}
			String name = method.getSimpleName().toString() + "(" + Join.commaSpace(params) + ")";

			String returnType = resolveTypeVars(method.getReturnType(), stubInterface).toString();

			GMethod m = g.getMethod(name).addAnnotation("@Override");
			m.returnType(returnType);

			if (method.getTypeParameters().size() > 0) {
				List<String> tpes = new ArrayList<String>();
				for (TypeParameterElement tpe : method.getTypeParameters()) {
					tpes.add(tpe.toString());
				}
				m.typeParameters(Join.commaSpace(tpes));
			}

			if (!"void".equals(returnType)) {
				if (Primitives.isPrimitive(returnType)) {
					m.body.line("return {};", Primitives.getDefault(returnType));
				} else {
					m.body.line("return null;");
				}
			}
		}

		Util.saveCode(processingEnv, g, userClass);
	}

	private TypeMirror resolveTypeVars(TypeMirror type, DeclaredType stubInterface) {
		TypeElement stubInterfaceType = (TypeElement) stubInterface.asElement();
		if (type == null) {
			return type;
		} else if (type.getKind() == TypeKind.TYPEVAR) {
			if (stubInterface.getTypeArguments().size() > 0) {
				boolean found = false;
				int i = 0;
				for (TypeParameterElement tpe : stubInterfaceType.getTypeParameters()) {
					if (processingEnv.getTypeUtils().isSameType(tpe.asType(), type)) {
						found = true;
						break;
					}
					i++;
				}
				if (found) {
					return stubInterface.getTypeArguments().get(i);
				}
			}
			return type;
		} else if (type.getKind() == TypeKind.WILDCARD) {
			WildcardType typew = (WildcardType) type;
			return processingEnv.getTypeUtils().getWildcardType(//
				resolveTypeVars(typew.getExtendsBound(), stubInterface),//
				resolveTypeVars(typew.getSuperBound(), stubInterface));
		} else if (type.getKind() == TypeKind.DECLARED) {
			DeclaredType typed = (DeclaredType) type;
			if (typed.getTypeArguments().size() == 0) {
				return type;
			}
			TypeMirror[] resolved = new TypeMirror[typed.getTypeArguments().size()];
			for (int i = 0; i < typed.getTypeArguments().size(); i++) {
				resolved[i] = resolveTypeVars(typed.getTypeArguments().get(i), stubInterface);
			}
			return processingEnv.getTypeUtils().getDeclaredType((TypeElement) typed.asElement(), resolved);
		} else {
			return type;
		}
	}

}
