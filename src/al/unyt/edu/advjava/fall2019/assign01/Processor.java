package al.unyt.edu.advjava.fall2019.assign01;

import java.time.LocalTime;
import java.util.concurrent.*;

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
                        "\t\t Letters: " + Utils.getMostFrequentEntries(Repository.getInstance().getUnigramHashMap(), 5) + "\n" +
                        "\t\t Pairs: " + Utils.getMostFrequentEntries(Repository.getInstance().getBigramHashMap(), 5) + "\n" +
                        "\t\t Words: " + Utils.getMostFrequentEntries(Repository.getInstance().getWordsHashMap(), 5) + "\n" +
                        "\t\t Unigram entropy: " + Utils.calculateEntropy(Repository.getInstance().getUnigramHashMap())  + "\n" +
                        "\t\t Bigram entropy: " + Utils.calculateEntropy(Repository.getInstance().getBigramHashMap())  + "\n"
        );
    }

    private void printFinalStatistics() {
        System.out.printf("%s",
                "Final execution time: " + getFinalExecutionTime() + " ms:\n" +
                        "\t\t " + getExecutorCompletedTaskCount() + " files processed\n" +
                        "\t\t Total words: " + Repository.getInstance().getWordsHashMap().values().stream().reduce(0L, Long::sum) + "\n"+
                        "\t\t Std. Dev: " + Utils.calculateStandardDeviationOnNumberOfWords(Repository.getInstance().getFileWordCountHashMap()) + "\n" +
                        "\t\t Letters: " + Utils.getMostFrequentEntries(Repository.getInstance().getUnigramHashMap(), 5) + "\n" +
                        "\t\t Pairs: " + Utils.getMostFrequentEntries(Repository.getInstance().getBigramHashMap(), 5) + "\n" +
                        "\t\t Words: " + Utils.getMostFrequentEntries(Repository.getInstance().getWordsHashMap(), 5) + "\n" +
                        "\t\t Unigram entropy: " + Utils.calculateEntropy(Repository.getInstance().getUnigramHashMap())  + "\n" +
                        "\t\t Bigram entropy: " + Utils.calculateEntropy(Repository.getInstance().getBigramHashMap())   + "\n"
        );
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