import io.github.raghultech.filesentry.FileWatcher;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Example of using FileSentry to monitor file changes.
 */
public class ExampleFileWatcher {

    private FileWatcher fileWatcher;
    private transient WeakReference<Thread> fileWatcherThread;
    private File currentFile = new File("sample.txt"); 

    public void watchFileForChanges() {
        stopFileWatcher(); // Stop previous watcher if running

        if (currentFile == null || !currentFile.exists()) {
            System.out.println("File not found: " + currentFile.getAbsolutePath());
            return;
        }

        fileWatcher = new FileWatcher(currentFile);
        
        // 1️⃣ Simple modify/create/delete awareness
        fileWatcher.setFileModifyListener(changed -> {
            if (changed) {
                System.out.println("✅ Simple: File changed.");
                // your reload logic
            }
        });

        // 2️⃣ State listener: existence
        fileWatcher.setFileStateListener(exists -> {
            if (exists) {
                System.out.println("📂 State: File exists (create/modify).");
            } else {
                System.out.println("🗑️ State: File deleted.");
            }
        });

        // 3️⃣ Detailed change type
        fileWatcher.setFileChangeListener(type -> {
            switch (type) {
                case CREATED -> System.out.println("🟢 Detailed: File CREATED.");
                case MODIFIED -> System.out.println("✏️ Detailed: File MODIFIED.");
                case DELETED -> System.out.println("🔴 Detailed: File DELETED.");
            }
        });

        Thread watcherThread = new Thread(fileWatcher, "FileWatcher-Thread");
        watcherThread.setDaemon(true); // Allow JVM to exit if only daemon threads remain
        fileWatcherThread = new WeakReference<>(watcherThread);
        watcherThread.start();
    }

    public void stopFileWatcher() {
        if (fileWatcher != null) {
            fileWatcher.stopWatching();
        }

        Thread watcherThread = fileWatcherThread != null ? fileWatcherThread.get() : null;
        if (watcherThread != null && watcherThread.isAlive()) {
            watcherThread.interrupt();
            try {
                watcherThread.join(1000); // Wait for thread to exit
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        ExampleFileWatcher watcher = new ExampleFileWatcher();
        watcher.watchFileForChanges();

        // Keep the main thread alive for demonstration
        try {
        	 System.out.println("👁️ Watching file for 60 seconds...");
            Thread.sleep(60000); // Run for 60 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        watcher.stopFileWatcher();
         System.out.println("🛑 Stopped watching.");
    }
}
