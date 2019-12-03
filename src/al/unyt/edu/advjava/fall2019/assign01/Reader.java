package al.unyt.edu.advjava.fall2019.assign01;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reader {

    public void readStopWords(){
        Path stopWordsPath =  Paths.get("src/local/stopwords");
        List<Path> filteredAbsolutePaths = getFilePaths(stopWordsPath);
        filteredAbsolutePaths.forEach(path -> Repository.getInstance().getStopWords().addAll(getTextFileWords(path)));
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
            System.out.println("Text files is incorrect.");
        }
        return new ArrayList<Path>();
    }

    public synchronized List<String> getTextFileWords(Path filePath) {
        Pattern pattern = Pattern.compile("\\s+");
        try {
            return Files.lines(filePath, StandardCharsets.ISO_8859_1)
                    .map(line -> line.replaceAll("[?!',`.;:\\-(){}\\]\\[\"]", ""))
                    .flatMap(pattern::splitAsStream)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    public synchronized void readTextFile(Path filePath) {
        getTextFileWords(filePath)
                .stream()
                .filter(word -> !Repository.getInstance().isStopWord(word))
                .forEach(word -> {
                    updateWordsRepository(word);
                    updateUnigramRepository(word);
                    updateBigramRepository(word);
                });
    }

    private synchronized void updateWordsRepository(String word) {
        int counter = Repository.getInstance().getWordsHashMap().getOrDefault(word, 0);
        Repository.getInstance().getWordsHashMap().put(word, ++counter);
    }

    private static synchronized void updateUnigramRepository(String word){
        char[] wordChars = word.toCharArray();
        for (int i = 0; i < wordChars.length; i++) {
            String charAsString = String.valueOf(wordChars[i]);
            int counter = Repository.getInstance().getUnigramHashMap().getOrDefault(charAsString, 0);
            Repository.getInstance().getUnigramHashMap().put(charAsString, ++counter);
        }
    }

    private synchronized void updateBigramRepository(String word){
        int lastStringStartIndex = word.length() - 2; // (2 -> pair of letters)
        for (int i = 0; i < lastStringStartIndex; i++) {
            String charPair = word.substring(i, i + 2);
            int counter = Repository.getInstance().getBigramHashMap().getOrDefault(charPair, 0);
            Repository.getInstance().getBigramHashMap().put(charPair, ++counter);
        }
    }
}
