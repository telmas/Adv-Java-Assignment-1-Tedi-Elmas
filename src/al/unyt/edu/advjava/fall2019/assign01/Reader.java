package al.unyt.edu.advjava.fall2019.assign01;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reader {

    public void readStopWords(){
        Path stopWordsPath =  Paths.get("src/local/stopwords");
        List<Path> filteredAbsolutePaths = getFilePaths(stopWordsPath);
        filteredAbsolutePaths.forEach(path -> Repository.getInstance().getStopWords().addAll(getTextFileWords(path, true)));
    }

    public List<Path> getFilePaths(Path folderPath) {
       return getFilePaths(folderPath, Integer.MAX_VALUE);
    }

    public List<Path> getFilePaths(Path folderPath, int limit) {
        try (Stream<Path> paths = Files.walk(folderPath)) {
            return paths
                    .limit(limit)
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().endsWith(".txt"))
                    .map(Path::toAbsolutePath)
                    .collect(Collectors.toList());
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println("Text files is   incorrect.");
        }
        return new ArrayList<Path>();
    }

    public synchronized List<String> getTextFileWords(Path filePath, boolean readingStopWordsFile) {
        Pattern pattern = Pattern.compile("\\s+");
        try {
            return Files.lines(filePath, StandardCharsets.ISO_8859_1)
                    .filter(line -> (readingStopWordsFile? !line.startsWith("#") : true))
                    .flatMap(pattern::splitAsStream)
                    .map(word -> word.replaceAll("[^A-Za-z0-9]+", ""))
                    .filter(word -> !word.trim().equals(""))
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    public synchronized void readTextFile(Path filePath) {
        AtomicReference<Long> fileWordCount = new AtomicReference<>(0L);
        getTextFileWords(filePath, false)
                .stream()
                .filter(word -> !Repository.getInstance().isStopWord(word))
                .forEach(word -> {
                    updateWordsRepository(word);
                    updateUnigramRepository(word);
                    updateBigramRepository(word);
                    fileWordCount.getAndSet(fileWordCount.get() + 1);
                });
        Repository.getInstance().getFileWordCount().add(fileWordCount.get());
    }

    private synchronized void updateWordsRepository(String word) {
        Long counter = Repository.getInstance().getWordsHashMap().getOrDefault(word, 0L);
        Repository.getInstance().getWordsHashMap().put(word, ++counter);
    }

    private synchronized void updateUnigramRepository(String word){
        char[] wordChars = word.toCharArray();
        for (int i = 0; i < wordChars.length; i++) {
            String charAsString = String.valueOf(wordChars[i]);
            Long counter = Repository.getInstance().getUnigramHashMap().getOrDefault(charAsString, 0L);
            Repository.getInstance().getUnigramHashMap().put(charAsString, ++counter);
        }
    }

    private synchronized void updateBigramRepository(String word){
        int lastStringStartIndex = word.length() - 1 ;
        for (int i = 0; i < lastStringStartIndex; i++) {
            String charPair = word.substring(i, i + 2);
            Long counter = Repository.getInstance().getBigramHashMap().getOrDefault(charPair, 0L);
            Repository.getInstance().getBigramHashMap().put(charPair, ++counter);
        }
    }



//    double average = wordsPerFile.stream().mapToLong(Long::longValue).average().orElse(0L);
//    double sum = (wordsPerFile.stream().mapToDouble(value -> Math.pow(value - average, 2)).sum()) / wordsPerFile.size();
//        return Math.sqrt(sum);
}
