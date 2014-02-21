import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// See source code of org.eclipse.emf.codegen.ecore.Generator
		
		ResourceSet set = new ResourceSetImpl();
		EcoreResourceFactoryImpl ecoreFactory = new EcoreResourceFactoryImpl();
		Resource.Factory.Registry registry = set.getResourceFactoryRegistry();
		Map<String, Object> map = registry.getExtensionToFactoryMap();
		map.put("ecore", ecoreFactory);
		map.put("genmodel", ecoreFactory);

		GenModelPackage.eINSTANCE.eClass();

		Resource res = set.getResource(URI.createFileURI(new File("model/My.genmodel").getAbsolutePath()), true);
		res.load(new HashMap());
		GenModel genModel = null;
		TreeIterator<EObject> list = res.getAllContents();
		while (list.hasNext())
		{
		EObject obj = list.next();
		if (obj instanceof GenModel)
		{
		genModel = (GenModel) obj;
		break;
		}
		}
		genModel.reconcile();
		String modelName = genModel.getModelName();
		System.out.println("genModel name: " + modelName);

		String dir = genModel.getTemplateDirectory();
		System.out.println(" - template did: " + dir);

		List<String> staticPkgList = genModel.getStaticPackages();
		System.out.println("staticPkgList size: " + staticPkgList.size());

		List<GenPackage> genPkgs = genModel.getGenPackages();
		System.out.println("genPkgs size: " + genPkgs.size());
		
		
		Monitor monitor = new BasicMonitor.Printing(System.out);
		
		EcorePlugin.getPlatformResourceMap().put("out", URI.createFileURI(new File("target").getAbsolutePath() + "/"));
		
		Generator gen = new Generator();
//		gen.getOptions().resourceSet = set;
		gen.getAdapterFactoryDescriptorRegistry().addDescriptor("http://www.eclipse.org/emf/2002/GenModel", GenModelGeneratorAdapterFactory.DESCRIPTOR);
		gen.setInput(genModel);
		genModel.setCanGenerate(true);
		System.out.println(gen.canGenerate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE));
//		genModel.setModelDirectory(URI.createFileURI(new File("gen-src").getAbsolutePath()).toString());
		genModel.setModelDirectory("out/gen-src");
		gen.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, monitor);
	}

}
