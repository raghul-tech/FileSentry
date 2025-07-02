package io.github.raghultech.filesentry.internal;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import io.github.raghultech.filesentry.config.FileChangeListener;
import io.github.raghultech.filesentry.config.FileChangeType;
import io.github.raghultech.filesentry.config.FileModifyListener;
import io.github.raghultech.filesentry.config.FileStateListener;




public class WindowsFileWatcher implements Runnable {
    private final File file;
  
    private volatile boolean keepWatching = true;
    private static final long DEBOUNCE_DELAY = 1000; // 1 second debounce delay
    private long lastChangeTime = 0; // Last time a change was detected
    private WatchService watchService;
    private ScheduledExecutorService executorService;
    
    private FileModifyListener modifyListener;
    private FileStateListener stateListener;
    private FileChangeListener changeListener;
    
    public WindowsFileWatcher(File file) {
        this.file = file;
        
    }

    @Override
    public void run() {
        try {
        	
        	File absoluteFile = file.getAbsoluteFile(); // ensures parent is not null
        	File parentFile = absoluteFile.getParentFile();
             if (parentFile == null)   return;
            watchService = FileSystems.getDefault().newWatchService();
            Path filePath = parentFile.toPath();
          //  filePath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            filePath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE);
            
            // Using ScheduledExecutorService for debounce handling
            executorService = Executors.newSingleThreadScheduledExecutor();

            while (keepWatching) {
            	try {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                 //   if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    if ((kind == StandardWatchEventKinds.ENTRY_MODIFY || kind == StandardWatchEventKinds.ENTRY_CREATE
                            || kind == StandardWatchEventKinds.ENTRY_DELETE) ) {
                        Path changed = filePath.resolve((Path) event.context());
                        if (changed.endsWith(file.getName())) {
                            long currentTime = System.currentTimeMillis();
                            if ( currentTime - lastChangeTime > DEBOUNCE_DELAY) {
                                lastChangeTime = currentTime;
           
                                    scheduleFileReload(kind);
                                
                            }
                        }
                    }
                }
                if (!key.reset()) break;
            }
            	  catch (ClosedWatchServiceException e) {
                      break; // Exit loop safely
                  }
            }
        }
        catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        finally {
            stopWatching();
        }
    }
    
 
    private void scheduleFileReload(WatchEvent.Kind<?> kind) {
        if (executorService != null) {
            executorService.schedule(() ->
                    SwingUtilities.invokeLater(() -> reloadFile(kind)),
                    500,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    
  

    public void setFileChangeListener(FileChangeListener listener) {
        this.changeListener = listener;
    }
    
    public void setFileModifyListener(FileModifyListener listener) {
        this.modifyListener = listener;
    }
    
    public void setFileStateListener(FileStateListener listener) {
        this.stateListener = listener;
    }
    
    
    
    private void reloadFile(WatchEvent.Kind<?> kind) {

        // it will tell if a change is made to a file
        if (modifyListener != null) {
        	 modifyListener.onFileModifyDetected(true);
        }
        
        // if create or modify means true if delete means false
        if (stateListener != null) {
       	 stateListener.onFileStateDetected(file.exists());
       }
        
        // If user wants detailed change type
        if (changeListener != null) {
            FileChangeType changeType;
            if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                changeType = FileChangeType.CREATED;
            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                changeType = FileChangeType.DELETED;
            } else {
                changeType = FileChangeType.MODIFIED;
            }
            changeListener.onFileChangeDetected(changeType);
        }
    }



  
    public void stopWatching() {
        keepWatching = false;
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (IOException e) {
           
        }
    }
}
