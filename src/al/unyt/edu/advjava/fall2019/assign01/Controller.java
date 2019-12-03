package al.unyt.edu.advjava.fall2019.assign01;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.*;

public class Controller {

    private ExecutorService executorService = Executors.newFixedThreadPool(50);
    private final Path localFolderPath;
    private Processor processor;
    private Reader reader;

    public Controller(String localFolderPath) {
        this.localFolderPath = Paths.get(localFolderPath);
        this.processor = new Processor(executorService);
        this.reader = new Reader();
    }

    public void beginProcessing() {

        reader.readStopWords();

        try {
            List<Path> filteredAbsolutePaths = reader.getFilePaths(localFolderPath, 1000);

            processor.startTime = LocalTime.now();
            filteredAbsolutePaths.forEach(filePath -> {
                executorService.execute(() -> reader.readTextFile(filePath));
            });

            processor.processProgressBuffer();

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}