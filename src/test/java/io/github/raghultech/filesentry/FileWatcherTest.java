package io.github.raghultech.filesentry;

import io.github.raghultech.filesentry.config.FileChangeType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileWatcherTest {

    private FileWatcher fileWatcher;
    private Thread watcherThread;

    @AfterEach
    public void cleanup() {
        if (fileWatcher != null) {
            fileWatcher.stopWatching();
        }
        if (watcherThread != null && watcherThread.isAlive()) {
            watcherThread.interrupt();
        }
    }

    @Test
    public void testModifyListenerDetectsChange() throws Exception {
        File tempFile = Files.createTempFile("filewatcher-modify-test", ".txt").toFile();
        tempFile.deleteOnExit();

        CountDownLatch modifyLatch = new CountDownLatch(1);

        fileWatcher = new FileWatcher(tempFile);
        fileWatcher.setFileModifyListener(changed -> {
            if (changed) {
                modifyLatch.countDown();
            }
        });

        startWatcher();
        Thread.sleep(500);

        try (FileWriter writer = new FileWriter(tempFile, true)) {
            writer.write("Test modify listener.\n");
            writer.flush();
        }

        boolean modifiedDetected = modifyLatch.await(5, TimeUnit.SECONDS);
        assertTrue(modifiedDetected, "Modify listener did not detect change.");
    }

    @Test
    public void testStateListenerDetectsDeletion() throws Exception {
        File tempFile = Files.createTempFile("filewatcher-state-test", ".txt").toFile();
        tempFile.deleteOnExit();

        CountDownLatch stateLatch = new CountDownLatch(1);

        fileWatcher = new FileWatcher(tempFile);
        fileWatcher.setFileStateListener(exists -> {
            if (!exists) {
                stateLatch.countDown();
            }
        });

        startWatcher();
        Thread.sleep(1000);

        boolean deleted = tempFile.delete();
        assertTrue(deleted, "Failed to delete temp file");

        boolean deletionDetected = stateLatch.await(5, TimeUnit.SECONDS);
        assertTrue(deletionDetected, "State listener did not detect file deletion.");
    }

    @Test
    public void testChangeTypeListenerDetectsCreation() throws Exception {
        File tempDir = Files.createTempDirectory("filewatcher-create-test").toFile();
        tempDir.deleteOnExit();
        File createdFile = new File(tempDir, "newfile.txt");

        CountDownLatch changeLatch = new CountDownLatch(1);

        fileWatcher = new FileWatcher(createdFile);
        fileWatcher.setFileChangeListener(type -> {
            if (type == FileChangeType.CREATED) {
                changeLatch.countDown();
            }
        });

        startWatcher();
        Thread.sleep(500);

        boolean created = createdFile.createNewFile();
        assertTrue(created, "Failed to create new file");

        boolean creationDetected = changeLatch.await(5, TimeUnit.SECONDS);
        assertTrue(creationDetected, "ChangeType listener did not detect file creation.");
    }

    @Test
    public void testAllListenersAreCalledOnModify() throws Exception {
        File tempFile = Files.createTempFile("filewatcher-all-test", ".txt").toFile();
        tempFile.deleteOnExit();

        CountDownLatch modifyLatch = new CountDownLatch(1);
        CountDownLatch stateLatch = new CountDownLatch(1);
        CountDownLatch changeLatch = new CountDownLatch(1);

        fileWatcher = new FileWatcher(tempFile);
        fileWatcher.setFileModifyListener(changed -> {
            if (changed) {
                modifyLatch.countDown();
            }
        });
        fileWatcher.setFileStateListener(exists -> {
            if (exists) {
                stateLatch.countDown();
            }
        });
        fileWatcher.setFileChangeListener(type -> {
            if (type == FileChangeType.MODIFIED) {
                changeLatch.countDown();
            }
        });

        startWatcher();
        Thread.sleep(500);

        try (FileWriter writer = new FileWriter(tempFile, true)) {
            writer.write("Trigger all listeners.\n");
            writer.flush();
        }

        boolean modifyDetected = modifyLatch.await(5, TimeUnit.SECONDS);
        boolean stateDetected = stateLatch.await(5, TimeUnit.SECONDS);
        boolean changeDetected = changeLatch.await(5, TimeUnit.SECONDS);

        assertTrue(modifyDetected, "Modify listener did not detect modification.");
        assertTrue(stateDetected, "State listener did not detect existing file.");
        assertTrue(changeDetected, "ChangeType listener did not detect modification.");
    }

    private void startWatcher() {
        watcherThread = new Thread(fileWatcher);
        watcherThread.setDaemon(true);
        watcherThread.start();
    }
}
