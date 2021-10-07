package de.uniba.wiai.dsg.pks.assignment1.histogram.sequential;

import de.uniba.wiai.dsg.pks.assignment.model.Histogram;
import de.uniba.wiai.dsg.pks.assignment.model.HistogramService;
import de.uniba.wiai.dsg.pks.assignment.model.HistogramServiceException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class SequentialHistogramService implements HistogramService {

	private Histogram histogram;

	public SequentialHistogramService() {
		// REQUIRED FOR GRADING - DO NOT REMOVE DEFAULT CONSTRUCTOR
		// but you can add code below
	}

	@Override
	public Histogram calculateHistogram(String rootDirectory, String fileExtension) throws HistogramServiceException {
		this.histogram = new Histogram();
		return calculateRecursiveHistogram(rootDirectory, fileExtension, histogram);
	}

	/**
	 * Recursive helper method to calculate our histogram
	 *
	 * @param rootDirectory the directory the method works on
	 * @param fileExtension the file extension the method is looking for
	 * @param histogram the histogram which is passed from the previous recursion step
	 * @return the calculated histogram
	 * @throws HistogramServiceException
	 */
	private Histogram calculateRecursiveHistogram(String rootDirectory, String fileExtension, Histogram histogram)
			throws HistogramServiceException {

		Path path = Path.of(rootDirectory);

		histogram.setDirectories(histogram.getDirectories() + 1);

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path entry : stream) {
				if (Files.isRegularFile(entry)) {
					histogram.setFiles(histogram.getFiles() + 1);
					if (entry.getFileName().toString().endsWith(fileExtension)) {
						List<String> lines = Files.readAllLines(entry, StandardCharsets.UTF_8);
						aggregateDistribution(computeDistribution(lines));
						histogram.setLines(histogram.getLines() + lines.size());
						histogram.setProcessedFiles(histogram.getProcessedFiles() + 1);
					}
					System.out.println(String.format("File: \t\t%s finished!", entry.toString()));
				} else if (Files.isDirectory(entry)) {
					this.calculateRecursiveHistogram(entry.toString(), fileExtension, histogram);
					System.out.println(String.format("Directory: \t%s finished!\n [distr: %s, lines=%d, files=%d, " +
									"processedFiles=%d, " + "directories=%d]", entry.toString(),
							Arrays.toString(histogram.getDistribution()), histogram.getLines(), histogram.getFiles(),
							histogram.getProcessedFiles(), histogram.getDirectories()));
				}
			}
		} catch (IOException ex) {
			throw new HistogramServiceException(ex);
		}

		return histogram;
	}

	@Override
	public String toString() {
		return "SequentialHistogramService";
	}

	/**
	 * Calculates the distribution for the given lines with the help of the characters ascii codes to sort them into
	 * the right bin
	 *
	 * @param lines the lines which the distribution should be calculated for
	 * @return the calculated distribution
	 */
	private long[] computeDistribution(List<String> lines) {
		long[] distribution = new long[26];

		for (String line : lines) {
			for (char character : line.toLowerCase().toCharArray()) {
				int ascii = character - 97;
				if (ascii >= 0 && ascii < 26) {
					distribution[ascii]++;
				}
			}
		}
		return distribution;
	}

	/**
	 * Aggregates the passed distribution into the distribution of the histogram instance variable
	 *
	 * @param distribution the distribution which should be aggregated
	 */
	private void aggregateDistribution(long[] distribution) {
		long[] currentDistribution = histogram.getDistribution();
		for (int i = 0; i < 26; i++) {
			currentDistribution[i] += distribution[i];
		}
	}

}
