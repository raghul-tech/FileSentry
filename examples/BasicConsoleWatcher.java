import io.github.raghultech.filesentry.FileWatcher;

import java.io.File;

public class BasicConsoleWatcher {
    public static void main(String[] args) {
        File file = new File("sample.txt");

        FileWatcher watcher = new FileWatcher(file);
          // Listener: Simple file change (modify, create, delete = true)
        watcher.setFileModifyListener(changed -> {
            if (changed) {
                System.out.println("✅ File modified (or created or deleted).");
                // Example: reloadFileContent();
            }
        });

        // Listener: State awareness (created/modified = true, deleted = false)
        watcher.setFileStateListener(exists -> {
            if (exists) {
                System.out.println("📂 File exists (created or modified).");
                // Example: reloadFileContent();
            } else {
                System.out.println("🗑️ File deleted.");
            }
        });

        // Listener: Precise change type enum
        watcher.setFileChangeListener(type -> {
            switch (type) {
                case CREATED:
                    System.out.println("🟢 File CREATED.");
                    break;
                case MODIFIED:
                    System.out.println("✏️ File MODIFIED.");
                    break;
                case DELETED:
                    System.out.println("🔴 File DELETED.");
                    break;
            }
        });

        Thread watcherThread = new Thread(watcher, "FileWatcher-Thread");
        watcherThread.setDaemon(true);
        watcherThread.start();

        // Keep the app alive for demonstration
        try {
            System.out.println("Watching file for 60 seconds...");
            Thread.sleep(60_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        watcher.stopWatching();
        System.out.println("Stopped watching.");
    }
}
