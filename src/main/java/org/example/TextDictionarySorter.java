package org.example;

import java.io.*;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Pair<K,V> {
    private final K k;
    private final V v;

    public Pair(K k, V v) {
        this.k = k;
        this.v = v;
    }

    public K getK() {
        return k;
    }

    public V getV() {
        return v;
    }

}

public class TextDictionarySorter {
    public static final char LINE_SEPARATOR_R = '\r';
    public static final char LINE_SEPARATOR_N = '\n';

    private final Pattern pattern = Pattern.compile("(.*):.*");

    public TextDictionarySorter() {
        /* empty */
    }

    /*
        There should not be lines of length more than 2GiB
     */
    private SortedMap<String, Pair<Long, Integer>> readFile(String fileName) {

        SortedMap<String, Pair<Long, Integer>> keysToPositions = new TreeMap<>(/* new Comparator */);

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
             BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(fileName))) {
            Scanner scanner = new Scanner(bufferedInputStream);
            long position = 0L;
            int[] lineSeparators = new int[2];
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    long prevPosition = position;
                    randomAccessFile.seek(position + line.length());
                    lineSeparators[0] = randomAccessFile.read();
                    lineSeparators[1] = randomAccessFile.read();
                    position += line.length() + 1;
                    if (lineSeparators[1] == LINE_SEPARATOR_N || lineSeparators[1] == LINE_SEPARATOR_R) {
                        position++;
                    }
                    keysToPositions.put(matcher.group(1), new Pair<>(prevPosition, Math.toIntExact(position - prevPosition)));
                }
            }
            if (scanner.ioException() != null) {
                throw new IllegalStateException(scanner.ioException());
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return keysToPositions;
    }

    private void writeSortedFile(String inputFileName, String outputFileName, SortedMap<String, Pair<Long, Integer>> sortedMap) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(inputFileName, "r");
             FileOutputStream fileOutputStream = new FileOutputStream(outputFileName)
        ) {
            for (Pair<Long, Integer> pair : sortedMap.values()) {
                randomAccessFile.seek(pair.getK());
                byte[] line = new byte[pair.getV()]; // TODO make some optimization to get maximum line length
                randomAccessFile.readFully(line);
                fileOutputStream.write(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        TextDictionarySorter sorter = new TextDictionarySorter();
        SortedMap<String, Pair<Long, Integer>> sortedMap = sorter.readFile("test.file");
        sorter.writeSortedFile("test.file", "out.file", sortedMap);
    }

}
