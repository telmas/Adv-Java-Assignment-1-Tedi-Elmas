package al.unyt.edu.advjava.fall2019.assign01;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Processor {

    private  ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private ExecutorService executorService;
    LocalTime startTime;
    LocalTime endTime;

    public Processor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    void processProgressBuffer() {
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (getExecutorActiveThreadCount() == 0) {
                endTime = LocalTime.now();
                scheduledExecutorService.shutdown();
                printFinalStatistics();
//                System.out.println(Repository.getInstance().getWordsHashMap().entrySet().toString());
//                System.out.println(Repository.getInstance().getUnigramHashMap().entrySet().toString());
//                System.out.println(Repository.getInstance().getBigramHashMap().entrySet().toString());
//                System.out.println(Repository.getInstance().getStopWords().toString());
                return;
            }
            printCurrentStatistics();
        }, 500, 500, TimeUnit.MILLISECONDS);
    }

    private int getExecutorActiveThreadCount(){
        return ((ThreadPoolExecutor) executorService).getActiveCount();
    }

    private long getExecutorCompletedTaskCount() {
        return ((ThreadPoolExecutor) executorService).getCompletedTaskCount();
    }

    private long getFinaExecutionTime() {
        return Duration.between(startTime, endTime).getNano() / 1_000_000;
    }

    private void printCurrentStatistics() {
        System.out.printf("%s",
                "\tElapsed 500 ms: \n" +
                        "\t\t " + getExecutorCompletedTaskCount() + " files processed\n" +
                        "\t\t " + getExecutorActiveThreadCount() + " files processing\n" +
                        "\t\t Letters: " + getMostFrequentEntries(Repository.getInstance().getUnigramHashMap(), 5) + "\n" +
                        "\t\t Pairs: " + getMostFrequentEntries(Repository.getInstance().getBigramHashMap(), 5) + "\n" +
                        "\t\t Words: " + getMostFrequentEntries(Repository.getInstance().getWordsHashMap(), 5) + "\n" +
                        "\t\t Unigram entropy: " + calculateEntropy(Repository.getInstance().getUnigramHashMap())  + "\n" +
                        "\t\t Bigram entropy: " + calculateEntropy(Repository.getInstance().getBigramHashMap())  + "\n"
        );
    }

    private void printFinalStatistics() {
        System.out.printf("%s",
                "\tFinal execution time: " + getFinaExecutionTime() + " ms\n" +
                        "\t\t " + getExecutorCompletedTaskCount() + " files processed\n" +
                        "\t\t Total words: " + Repository.getInstance().getWordsHashMap().entrySet().size() + "\n"+
                        "\t\t Std. Dev: " + calculateStandardDeviationOnNumberOfWords(Repository.getInstance().getWordsHashMap()) + "\n" +
                        "\t\t Letters: " + getMostFrequentEntries(Repository.getInstance().getUnigramHashMap(), 5) + "\n" +
                        "\t\t Pairs: " + getMostFrequentEntries(Repository.getInstance().getBigramHashMap(), 5) + "\n" +
                        "\t\t Words: " + getMostFrequentEntries(Repository.getInstance().getWordsHashMap(), 5) + "\n" +
                        "\t\t Unigram entropy: " + calculateEntropy(Repository.getInstance().getUnigramHashMap())  + "\n" +
                        "\t\t Bigram entropy: " + calculateEntropy(Repository.getInstance().getBigramHashMap())   + "\n"
        );
    }

    public List<Map.Entry<String, Integer>> getMostFrequentEntries(ConcurrentHashMap<String, Integer> hashMap) {
        return getMostFrequentEntries(hashMap, Integer.MAX_VALUE);
    }

    public List<Map.Entry<String, Integer>> getMostFrequentEntries(ConcurrentHashMap<String, Integer> hashMap, int topEntriesNumber) {
        return hashMap
                .entrySet()
                .parallelStream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topEntriesNumber)
                .collect(Collectors.toList());
    }

    public double calculateStandardDeviationOnNumberOfWords(ConcurrentHashMap<String, Integer> wordsHashMap) {
        double wordCountMean = wordsHashMap.values().stream().mapToLong(Integer::longValue).average().orElse(0d);
        double sum = wordsHashMap.values().stream().mapToDouble(count -> (count - wordCountMean) * (count - wordCountMean)).sum();
        return Math.sqrt(sum / wordsHashMap.size());
    }

    public double calculateEntropy(ConcurrentHashMap<String, Integer> hashMap) {
        return hashMap
                .values()
                .parallelStream()
                .map(count -> (double) count / hashMap
                        .values()
                        .parallelStream()
                        .mapToDouble(Integer::doubleValue)
                        .sum())
                .map(frequency -> (frequency * (Math.log(frequency) / Math.log(2))))
                .reduce(0d, Double::sum) * -1;
    }
}