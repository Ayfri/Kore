package io.github.ayfri.kore.gradle.internal;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * Starts the entry point with the compiled sources on a child class loader, since a JDK AOT cache refuses non-empty
 * directories on the class path. Written in Java so it runs without the Kotlin standard library.
 *
 * <p>Usage: {@code AotLauncher <directories separated by File.pathSeparator> <main class> <arguments...>}
 */
public final class AotLauncher {
	private AotLauncher() {
	}

	public static void main(String[] args) throws Throwable {
		String[] directories = args[0].isEmpty() ? new String[0] : args[0].split(File.pathSeparator);
		URL[] urls = new URL[directories.length];
		for (int i = 0; i < directories.length; i++) urls[i] = new File(directories[i]).toURI().toURL();

		ClassLoader loader = new URLClassLoader(urls, AotLauncher.class.getClassLoader());
		Thread.currentThread().setContextClassLoader(loader);

		try {
			Class.forName(args[1], true, loader)
				.getMethod("main", String[].class)
				.invoke(null, (Object) Arrays.copyOfRange(args, 2, args.length));
		} catch (java.lang.reflect.InvocationTargetException exception) {
			throw exception.getCause();
		}
	}
}
