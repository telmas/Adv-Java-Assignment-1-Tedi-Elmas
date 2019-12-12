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
            System.out.println("Text files folder path is incorrect.");
        }
        return new ArrayList<Path>();
    }

    public synchronized List<String> getTextFileWords(Path filePath, boolean readingStopWordsFile) {
        Pattern pattern = Pattern.compile("\\s+");
        try {
            return Files.lines(filePath, StandardCharsets.ISO_8859_1)
                    .filter(line -> (!readingStopWordsFile || !line.startsWith("#")))
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
                    Repository.getInstance().updateWordsRepository(word);
                    Repository.getInstance().updateUnigramRepository(word);
                    Repository.getInstance().updateBigramRepository(word);
                    fileWordCount.getAndSet(fileWordCount.get() + 1);
                });
        String fileName = filePath.getFileName().toString();
        String textFileName = fileName.substring(0, fileName.lastIndexOf(".txt"));
        Repository.getInstance().updateFileWordCountRepository(textFileName, fileWordCount.get());
    }
}
