/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.app.cdi.jpa.hibernate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Fernando
 */
public abstract class HibernateFactory {

	protected SessionFactory sf;
	protected Configuration cfg;
	// protected MetadataSources cfg;
	protected List<Class> classesExtras = new LinkedList<>();
	protected List<String> pakcagesEntityClass = new LinkedList<>();

	public HibernateFactory() {
	}

	protected synchronized void configure() {
		if (sf != null) {
			return;
		}

		cfg = new Configuration();
		cfg.configure(getHibernateCfgXml());
		configNamingStrategy();
		fireAddClassesEvent();
		configExtrasClasses();
		// build
		sf = cfg.buildSessionFactory();

		// StandardServiceRegistry standardRegistry = new
		// StandardServiceRegistryBuilder().configure(getHibernateCfgXml())
		// .build();
		// cfg = new MetadataSources(standardRegistry);
		// Metadata metaData = cfg.getMetadataBuilder().build();
		// configNamingStrategy();
		// // add and config extras entities from modules, via events:
		// fireAddClassesEvent();
		// configExtrasClasses();
		// sf = metaData.getSessionFactoryBuilder().build();
	}

	protected void fireAddClassesEvent() {
	}

	@SuppressWarnings("rawtypes")
	protected void configExtrasClasses() {
		try {
			for (Class cl1 : classesExtras) {
				cfg.addAnnotatedClass(cl1);
			}
			for (String pk : pakcagesEntityClass) {
				cfg.addPackage(pk);
			}

		} catch (MappingException me) {
			System.out.println("Error al agregar clases " + me);
		}
	}

	protected String getHibernateCfgXml() {
		return "hibernate.cfg.xml";
	}

	protected void configNamingStrategy() {
	}

	@SuppressWarnings("rawtypes")
	public void addEntityClasses(List<Class> classes) {
		for (Class class1 : classes) {
			classesExtras.add(class1);
		}
	}

	/**
	 * Permite Agregar todo un paquete donde se encuentran las entites.
	 *
	 * @param packegeEntityClass
	 *            Nombre del paquete donde se encuentran las entities.
	 */
	@SuppressWarnings("rawtypes")
	public void addPackageyClasses(String packegeEntityClass) throws ClassNotFoundException {
		pakcagesEntityClass.add(packegeEntityClass);
		try {
			List<Class> clazzPackage = tEntityClassesFromPackage(packegeEntityClass);
			if (clazzPackage != null)
				addEntityClasses(clazzPackage);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public SessionFactory getFactory() {
		if (sf == null) {
			configure();
		}
		return sf;
	}

	public Configuration getCfg() {
		return cfg;
	}

	/**
	 * Busca en todo el proyecto del paquete que se la paso.
	 * 
	 * @param packageName
	 *            Nombre del paquete donde se encuentran las entities, para esta
	 *            version solo con pasarle busca en todo el proyecto.
	 * @return Listado de clases
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("resource")
	public static ArrayList<String> getClassNamesFromPackage(String packageName)
			throws IOException, URISyntaxException, ClassNotFoundException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ArrayList<String> names = new ArrayList<String>();
		File folder = null;
		File[] files = null;

		packageName = packageName.replace(".", "/");
		URL packageURL = classLoader.getResource(packageName);
		if (packageURL.toString().startsWith("jar:file:")) {
			System.out.println("Cargando entities desde Jar...");
			int idx = packageURL.toString().indexOf('!');
			if (idx == -1)
				throw new IllegalStateException(
						" ###You appear to have loaded this class from a local jar file, but I can't make sense of the URL!");

			String fileName = URLDecoder.decode(packageURL.toString().substring("jar:file:".length(), idx),
					Charset.defaultCharset().name());
			folder = new File(fileName);
			JarInputStream jarFile = new JarInputStream(new FileInputStream(fileName));
			JarEntry jarEntry;
			while (true) {
				jarEntry = jarFile.getNextJarEntry();
				if (jarEntry == null) {
					break;
				}
				if ((jarEntry.getName().endsWith(".class"))) {
					String className = jarEntry.getName().replaceAll("/", "\\.");
					String myClass = className.substring(0, className.lastIndexOf('.'));
					names.add(myClass);
				}
			}
		} else {
			System.out.println("Cargando entities desde Directorio...");
			URI uri = new URI(packageURL.toString());
			folder = new File(uri.getPath());
		}
		if (folder != null) {
			files = folder.listFiles();
			if (files != null) {
				for (File file : files) {
					String name = file.getName();
					System.out.println(name);
					name = name.substring(0, name.lastIndexOf('.')); // remove ".class"
					names.add(name);
				}
			}
		}
		return names;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Class> tEntityClassesFromPackage(String packageName)
			throws ClassNotFoundException, IOException, URISyntaxException {
		List<String> classNames = getClassNamesFromPackage(packageName);
		List<Class> classes = new ArrayList<Class>();
		int count = 0;
		for (String className : classNames) {
			Class cls = null;
			if (!className.contains("package-info")) {
				cls = Class.forName(className);
				if (cls.isAnnotationPresent(javax.persistence.Entity.class)) {
					classes.add(cls);
					count++;
				}
			}
		}
		System.out.println(" ****** Clases Registradas >> " + count + " ****** ");
		return classes;
	}

}
