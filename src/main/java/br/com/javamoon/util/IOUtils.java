package br.com.javamoon.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IOUtils {

	public static byte[] getBytes(Path path) throws IOException {
		return Files.readAllBytes(path);
	}
}
