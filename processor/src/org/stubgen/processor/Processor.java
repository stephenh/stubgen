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

import joist.sourcegen.GClass;
import joist.sourcegen.GField;
import joist.sourcegen.GMethod;
import joist.util.Join;

import org.exigencecorp.aptutil.PrimitivesUtil;
import org.exigencecorp.aptutil.PropUtil;
import org.exigencecorp.aptutil.TypeVarUtil;
import org.exigencecorp.aptutil.Util;
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

		List<ExecutableElement> taken = new ArrayList<ExecutableElement>();
		for (ExecutableElement getter : methodsIn(pe.getElementUtils().getAllMembers((TypeElement) stubInterface.asElement()))) {
			if (getter.getParameters().size() > 0 || getter.getThrownTypes().size() > 0) {
				continue;
			}
			String methodName = getter.getSimpleName().toString();

			if (methodName.equals("getClass")) {
				continue;
			}

			final String propName;
			if (methodName.startsWith("get") && methodName.length() > 3) {
				propName = Util.lower(methodName.substring(3));
			} else if (methodName.startsWith("is") && methodName.length() > 2) {
				propName = Util.lower(methodName.substring(2));
			} else {
				continue;
			}

			String setterName = "set" + Util.upper(propName);
			ExecutableElement setter = null;
			for (ExecutableElement setter2 : methodsIn(pe.getElementUtils().getAllMembers((TypeElement) stubInterface.asElement()))) {
				if (setter2.getSimpleName().toString().equals(setterName)//
					&& setter2.getThrownTypes().size() == 0 //
					&& setter2.getParameters().size() == 1 //
					&& pe.getTypeUtils().isSameType(setter2.getParameters().get(0).asType(), getter.getReturnType())) {
					setter = setter2;
					taken.add(setter2);
					break;
				}
			}

			String propType = TypeVarUtil.resolve(pe.getTypeUtils(), getter.getReturnType(), stubInterface).toString();

			String fieldName = "_" + propName;
			g.getField(fieldName).type(propType).setProtected();
			g.getMethod(methodName).returnType(propType).body.line("return {};", fieldName);
			if (setter != null) {
				g.getMethod(setterName).argument(propType, fieldName).body.line("this.{} = {};", fieldName, fieldName);
			}

			taken.add(getter);
		}

		for (ExecutableElement method : methodsIn(pe.getElementUtils().getAllMembers((TypeElement) stubInterface.asElement()))) {
			if (taken.contains(method)) {
				continue;
			}
			if ("java.lang.Object".equals(method.getEnclosingElement().toString())) {
				continue;
			}
			if (method.getSimpleName().toString().equals("hashCode") || method.getSimpleName().toString().equals("equals")) {
				continue;
			}

			List<String> params = new ArrayList<String>();
			for (VariableElement p : method.getParameters()) {
				String parameterType = TypeVarUtil.resolve(pe.getTypeUtils(), p.asType(), stubInterface).toString();
				params.add(parameterType + " " + p.getSimpleName().toString());
			}
			String name = method.getSimpleName().toString() + "(" + Join.commaSpace(params) + ")";

			String returnType = TypeVarUtil.resolve(pe.getTypeUtils(), method.getReturnType(), stubInterface).toString();

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
				if (PrimitivesUtil.isPrimitive(returnType)) {
					m.body.line("return {};", PrimitivesUtil.getDefault(returnType));
				} else {
					m.body.line("return null;");
				}
			}
		}

		Util.saveCode(processingEnv, g, userClass);
	}

}
