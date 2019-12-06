package al.unyt.edu.advjava.fall2019.assign01;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Repository {

    private static final Repository INSTANCE = new Repository();

    private ArrayList<String> stopWords = new ArrayList<>();
    private CopyOnWriteArrayList<Long> fileWordCount = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<String, Long> wordsHashMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> unigramHashMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> bigramHashMap = new ConcurrentHashMap<>();

    private Repository() {
    }

    public static Repository getInstance() {
        return INSTANCE;
    }


    public boolean isStopWord(String word){
        return getStopWords().contains(word.toLowerCase());
    }


    public ArrayList<String> getStopWords() {
        return stopWords;
    }

    public void setStopWords(ArrayList<String> stopWords) {
        this.stopWords = stopWords;
    }

    public ConcurrentHashMap<String, Long> getWordsHashMap() {
        return wordsHashMap;
    }

    public void setWordsHashMap(ConcurrentHashMap<String, Long> wordsHashMap) {
        this.wordsHashMap = wordsHashMap;
    }

    public ConcurrentHashMap<String, Long> getUnigramHashMap() {
        return unigramHashMap;
    }

    public void setUnigramHashMap(ConcurrentHashMap<String, Long> unigramHashMap) {
        this.unigramHashMap = unigramHashMap;
    }

    public ConcurrentHashMap<String, Long> getBigramHashMap() {
        return bigramHashMap;
    }

    public void setBigramHashMap(ConcurrentHashMap<String, Long> bigramHashMap) {
        this.bigramHashMap = bigramHashMap;
    }

    public CopyOnWriteArrayList<Long> getFileWordCount() {
        return fileWordCount;
    }

    public void setFileWordCount(CopyOnWriteArrayList<Long> fileWordCount) {
        this.fileWordCount = fileWordCount;
    }
}
