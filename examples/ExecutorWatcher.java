import io.github.raghultech.filesentry.FileWatcher;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorWatcher {
    private ExecutorService executor;
 private FileWatcher watcher;
    public void startWatcher() {
        File file = new File("sample.txt");
  watcher = new FileWatcher(file);
        
 // Listener 1: General modify awareness
        watcher.setFileModifyListener(changed -> {
            if (changed) {
                System.out.println("✅ Modify/Create/Delete detected.");
            }
        });

        // Listener 2: File existence state
        watcher.setFileStateListener(exists -> {
            if (exists) {
                System.out.println("📂 File exists (create or modify).");
            } else {
                System.out.println("🗑️ File deleted.");
            }
        });

        // Listener 3: Precise change type
        watcher.setFileChangeListener(type -> {
            switch (type) {
                case CREATED -> System.out.println("🟢 File CREATED.");
                case MODIFIED -> System.out.println("✏️ File MODIFIED.");
                case DELETED -> System.out.println("🔴 File DELETED.");
            }
        });
        executor = Executors.newSingleThreadExecutor();
        executor.submit(watcher);
    }

    public void stopWatcher() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorWatcher watcherApp = new ExecutorWatcher();
        watcherApp.startWatcher();

        System.out.println("Watching file for 60 seconds...");
        Thread.sleep(60_000);

        watcherApp.stopWatcher();
        System.out.println("Stopped watching.");
    }
}
