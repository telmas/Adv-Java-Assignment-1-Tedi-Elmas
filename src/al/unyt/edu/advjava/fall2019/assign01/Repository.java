package al.unyt.edu.advjava.fall2019.assign01;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Repository {

    private static final Repository INSTANCE = new Repository();

    private ArrayList<String> stopWordsArrayList = new ArrayList<>();
    private ConcurrentHashMap<String, Long> fileWordCountHashMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> wordsHashMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> unigramHashMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Long> bigramHashMap = new ConcurrentHashMap<>();

    private Repository() {
    }

    public static Repository getInstance() {
        return INSTANCE;
    }


    public boolean isStopWord(String word){
        return getStopWordsArrayList().contains(word.toLowerCase());
    }


    public ArrayList<String> getStopWordsArrayList() {
        return stopWordsArrayList;
    }

    public void setStopWordsArrayList(ArrayList<String> stopWordsArrayList) {
        this.stopWordsArrayList = stopWordsArrayList;
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

    public ConcurrentHashMap<String, Long> getFileWordCountHashMap() {
        return fileWordCountHashMap;
    }

    public void setFileWordCountHashMap(ConcurrentHashMap<String, Long> fileWordCountHashMap) {
        this.fileWordCountHashMap = fileWordCountHashMap;
    }

    synchronized void updateFileWordCountRepository(String path, long count){
        getFileWordCountHashMap().put(path, count);
    }

    synchronized void updateWordsRepository(String word) {
        Long counter = Repository.getInstance().getWordsHashMap().getOrDefault(word, 0L);
        getWordsHashMap().put(word, ++counter);
    }

    synchronized void updateUnigramRepository(String word){
        char[] wordChars = word.toCharArray();
        for (int i = 0; i < wordChars.length; i++) {
            String charAsString = String.valueOf(wordChars[i]);
            Long counter = Repository.getInstance().getUnigramHashMap().getOrDefault(charAsString, 0L);
            getUnigramHashMap().put(charAsString, ++counter);
        }
    }

    synchronized void updateBigramRepository(String word){
        int lastStringStartIndex = word.length() - 1 ;
        for (int i = 0; i < lastStringStartIndex; i++) {
            String charPair = word.substring(i, i + 2);
            Long counter = Repository.getInstance().getBigramHashMap().getOrDefault(charPair, 0L);
            getBigramHashMap().put(charPair, ++counter);
        }
    }
}
