package io.github.raghultech.filesentry.config;

public interface FileChangeListener {
    void onFileChangeDetected(FileChangeType changeType);
}