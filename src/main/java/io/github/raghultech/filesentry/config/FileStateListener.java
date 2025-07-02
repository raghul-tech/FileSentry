package io.github.raghultech.filesentry.config;


@FunctionalInterface
public interface FileStateListener {
    void onFileStateDetected(boolean changed);
}
