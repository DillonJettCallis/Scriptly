/*

org.slf4j:slf4j-simple:1.7.21



 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestScript {

	private static final Logger log = LoggerFactory.getLogger(TestScript.class);

	public static void main(String[] args) {

		log.info("Running: {}", args[0]);

		File source = new File(args[0]);

		recurse(source);

		log.info("Finished");
	}

	private static boolean recurse(File source) {
		return getFiles(source).parallelStream().map(file -> !file.isDirectory() || recurse(file) || !file.delete()).reduce(false, (l, r) -> l || r);
	}

	private static List<File> getFiles(File file) {
		File[] files = file.listFiles();

		if(files == null)
			return Collections.emptyList();
		else
			return Arrays.asList(files);
	}
}