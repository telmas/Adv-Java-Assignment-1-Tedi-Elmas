package al.unyt.edu.advjava.fall2019.assign01;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Repository {

    private static final Repository INSTANCE = new Repository();

    private ArrayList<String> stopWords = new ArrayList<>();;
    private ConcurrentHashMap<String, Integer> wordsHashMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> unigramHashMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> bigramHashMap = new ConcurrentHashMap<>();

    private Repository() {
    }

    public static Repository getInstance() {
        return INSTANCE;
    }


    public boolean isStopWord(String word){
        return getStopWords().contains(word.toLowerCase());
    }

    public ConcurrentHashMap<String, Integer> getWordsHashMap() {
        return wordsHashMap;
    }

    public void setWordsHashMap(ConcurrentHashMap<String, Integer> wordsHashMap) {
        this.wordsHashMap = wordsHashMap;
    }

    public ConcurrentHashMap<String, Integer> getBigramHashMap() {
        return bigramHashMap;
    }

    public void setBigramHashMap(ConcurrentHashMap<String, Integer> bigramHashMap) {
        this.bigramHashMap = bigramHashMap;
    }

    public ArrayList<String> getStopWords() {
        return stopWords;
    }

    public void setStopWords(ArrayList<String> stopWords) {
        this.stopWords = stopWords;
    }

    public ConcurrentHashMap<String, Integer> getUnigramHashMap() {
        return unigramHashMap;
    }

    public void setUnigramHashMap(ConcurrentHashMap<String, Integer> unigramHashMap) {
        this.unigramHashMap = unigramHashMap;
    }
}
