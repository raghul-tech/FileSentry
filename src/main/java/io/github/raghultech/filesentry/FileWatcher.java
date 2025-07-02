package io.github.raghultech.filesentry;

import java.io.File;

import io.github.raghultech.filesentry.config.FileChangeListener;
import io.github.raghultech.filesentry.config.FileModifyListener;
import io.github.raghultech.filesentry.config.FileStateListener;
import io.github.raghultech.filesentry.internal.LinuxFileWatcher;
import io.github.raghultech.filesentry.internal.WindowsFileWatcher;

public class FileWatcher implements Runnable {
    
    private Object fileWatcher; // Generic Object to hold either Windows or Linux watcher

    public FileWatcher(File file) {
    
        String os = System.getProperty("os.name").toLowerCase();
     
        if (os.contains("win")) {
            fileWatcher = new WindowsFileWatcher(file);
        } else  {
            fileWatcher = new LinuxFileWatcher(file);
        } 
    }

 
  /*
   * This will tell exactly what happened to the file (create, Modify, Delete)
   */
    
    public void setFileChangeListener(FileChangeListener listener) {
        if (fileWatcher instanceof WindowsFileWatcher) {
            ((WindowsFileWatcher) fileWatcher).setFileChangeListener(listener);
        } else if (fileWatcher instanceof LinuxFileWatcher) {
            ((LinuxFileWatcher) fileWatcher).setFileChangeListener(listener);
        }        
    }
    
    /*
     * if modify occur in creating and modifying only (create, Modify)
     */
    
    public void setFileStateListener(FileStateListener listener) {
        if (fileWatcher instanceof WindowsFileWatcher) {
            ((WindowsFileWatcher) fileWatcher).setFileStateListener(listener);
        } else if (fileWatcher instanceof LinuxFileWatcher) {
            ((LinuxFileWatcher) fileWatcher).setFileStateListener(listener);
        }      
    }
    
    /*
     * if any modify noticed it will tell (create, Modify, Delete)
     */
    
    public void setFileModifyListener(FileModifyListener listener) {
        if (fileWatcher instanceof WindowsFileWatcher) {
            ((WindowsFileWatcher) fileWatcher).setFileModifyListener(listener);
        } else if (fileWatcher instanceof LinuxFileWatcher) {
            ((LinuxFileWatcher) fileWatcher).setFileModifyListener(listener);
        }      
    }
    

    public void stopWatching() {
        if (fileWatcher instanceof WindowsFileWatcher) {
            ((WindowsFileWatcher) fileWatcher).stopWatching();
        } else if (fileWatcher instanceof LinuxFileWatcher) {
            ((LinuxFileWatcher) fileWatcher).stopWatching();
        }
    }

    @Override
    public void run() {
        if (fileWatcher instanceof WindowsFileWatcher) {
            ((WindowsFileWatcher) fileWatcher).run();
        } else if (fileWatcher instanceof LinuxFileWatcher) {
            ((LinuxFileWatcher) fileWatcher).run();
        }
    }
}

