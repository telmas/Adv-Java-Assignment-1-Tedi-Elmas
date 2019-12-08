package al.unyt.edu.advjava.fall2019.assign01;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Processor {

    private  ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private ExecutorService executorService;
    private LocalTime startTime;
    private LocalTime endTime;

    public Processor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    void processProgressBuffer() {
        ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (getExecutorActiveThreadCount() == 0) {
                setEndTime(LocalTime.now());
                scheduledExecutorService.shutdown();
                printFinalStatistics();
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

    private long getFinalExecutionTime() {
        return (getEndTime().toNanoOfDay() - getStartTime().toNanoOfDay()) / 1_000_000;
    }

    private void printCurrentStatistics() {
        System.out.printf("%s",
                "Elapsed 500 ms: \n" +
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
                "Final execution time: " + getFinalExecutionTime() + " ms:\n" +
                        "\t\t " + getExecutorCompletedTaskCount() + " files processed\n" +
                        "\t\t Total words: " + Repository.getInstance().getWordsHashMap().values().stream().reduce(0L, Long::sum) + "\n"+
                        "\t\t Std. Dev: " + calculateStandardDeviationOnNumberOfWords(Repository.getInstance().getFileWordCountHashMap()) + "\n" +
                        "\t\t Letters: " + getMostFrequentEntries(Repository.getInstance().getUnigramHashMap(), 5) + "\n" +
                        "\t\t Pairs: " + getMostFrequentEntries(Repository.getInstance().getBigramHashMap(), 5) + "\n" +
                        "\t\t Words: " + getMostFrequentEntries(Repository.getInstance().getWordsHashMap(), 5) + "\n" +
                        "\t\t Unigram entropy: " + calculateEntropy(Repository.getInstance().getUnigramHashMap())  + "\n" +
                        "\t\t Bigram entropy: " + calculateEntropy(Repository.getInstance().getBigramHashMap())   + "\n"
        );
    }

    public List<Map.Entry<String, Long>> getMostFrequentEntries(ConcurrentHashMap<String, Long> hashMap) {
        return getMostFrequentEntries(hashMap, Integer.MAX_VALUE);
    }

    public List<Map.Entry<String, Long>> getMostFrequentEntries(ConcurrentHashMap<String, Long> hashMap, int topEntriesNumber) {
        return hashMap
                .entrySet()
                .parallelStream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topEntriesNumber)
                .collect(Collectors.toList());
    }

    public double calculateStandardDeviationOnNumberOfWords(ConcurrentHashMap<String, Long> fileWordCountHashMap) {
        double wordCountMean = fileWordCountHashMap.values().stream().mapToLong(Long::longValue).average().orElse(0d);
        double sum = fileWordCountHashMap.values().stream().mapToDouble(count -> (count - wordCountMean) * (count - wordCountMean)).sum();
        return Math.sqrt(sum / fileWordCountHashMap.size());
    }

    public double calculateEntropy(ConcurrentHashMap<String, Long> hashMap) {
        return hashMap
                .values()
                .parallelStream()
                .map(count -> (double) count / hashMap
                        .values()
                        .parallelStream()
                        .mapToDouble(Long::doubleValue)
                        .sum())
                .map(frequency -> (frequency * (Math.log(frequency) / Math.log(2))))
                .reduce(0d, Double::sum) * -1;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}