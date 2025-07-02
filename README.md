<h1 align="center">📂 FileSentry</h1>

<p align="center">

  <!-- Maven Central -->
  <a href="https://central.sonatype.com/artifact/io.github.raghul-tech/filesentry">
    <img src="https://img.shields.io/maven-central/v/io.github.raghul-tech/filesentry?style=for-the-badge&color=blueviolet" alt="Maven Central" />
  </a>

  <!-- GitHub Release -->
  <a href="https://github.com/raghul-tech/FileSentry/releases">
    <img src="https://img.shields.io/github/release/raghul-tech/FileSentry.svg?label=Latest%20Release&style=for-the-badge&color=success" alt="GitHub Release" />
  </a>

  <!-- Maven Build Workflow -->
  <a href="https://github.com/raghul-tech/FileSentry/actions/workflows/maven.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/raghul-tech/FileSentry/maven.yml?label=Build&style=for-the-badge&color=brightgreen" alt="Maven Build Status" />
  </a>

  <!-- CodeQL Analysis -->
  <a href="https://github.com/raghul-tech/FileSentry/actions/workflows/codeql.yml">
    <img src="https://img.shields.io/github/actions/workflow/status/raghul-tech/FileSentry/codeql.yml?label=CodeQL&logo=github&style=for-the-badge&color=informational" alt="CodeQL Status" />
  </a>

  <!-- Javadoc -->
  <a href="https://javadoc.io/doc/io.github.raghul-tech/filesentry/1.0.0">
    <img src="https://img.shields.io/badge/Javadoc-1.0.0-blue?style=for-the-badge&logo=java" alt="Javadoc (1.0.0)" />
  </a>

  <!-- Support -->
  <a href="https://buymeacoffee.com/raghultech">
    <img src="https://img.shields.io/badge/Buy%20Me%20a%20Coffee-Support-orange?style=for-the-badge&logo=buy-me-a-coffee" alt="Buy Me A Coffee" />
  </a>
</p>


---

# About  FileSentry

**FileSentry** is a lightweight, cross-platform Java library for monitoring file changes in real time.  
It provides a simple API built on Java NIO’s `WatchService`, with built-in debouncing and event callbacks.

Supports **Windows**, **Linux**, and **macOS**.

---

## ✨ Features

✅ Watch any file for changes in real time  
✅ Get notified when a file is modified  
✅ Easy to integrate into Swing, JavaFX, or command-line apps  
✅ Minimal dependencies (pure Java)  
✅ Automatically stops when you’re done  
✅ Suitable for:

- Text editors (live reload)
- Log viewers
- Developer tools
- Custom synchronization utilities

---

## 🚀 Getting Started

### 📦 Installation

**Maven:**

```xml
<dependency>
  <groupId>io.github.raghul-tech</groupId>
  <artifactId>filesentry</artifactId>
  <version>1.0.0</version>
</dependency>
```

---

## 💡 Example Usage
Below is a complete example that:

- Watches sample.txt

- Prints a message when the file changes

- Automatically stops when the program ends

```java
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
                System.out.println(" Simple: File changed.");
                // your reload logic 
            }
        });

        // 2️⃣ State listener: existence
        fileWatcher.setFileStateListener(exists -> {
            if (exists) {
                System.out.println(" State: File exists (create/modify).");
            } else {
                System.out.println(" State: File deleted.");
            }
        });

        // 3️⃣ Detailed change type
        fileWatcher.setFileChangeListener(type -> {
            switch (type) {
                case CREATED -> System.out.println(" Detailed: File CREATED.");
                case MODIFIED -> System.out.println("️ Detailed: File MODIFIED.");
                case DELETED -> System.out.println(" Detailed: File DELETED.");
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
        	 System.out.println(" Watching file for 60 seconds...");
            Thread.sleep(60000); // Run for 60 seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        watcher.stopFileWatcher();
         System.out.println(" Stopped watching.");
    }
}

```

---

## 📝 Clarify **listener behaviors** in the Features section

* Just a short note describing exactly what each listener returns, so no confusion (since you had that question earlier):

> 📝 **Note about Listeners**
>
> - `FileModifyListener`: always `true` on any event (create, modify, delete)
> - `FileStateListener`: `true` if it is modified or created or file currently exists, `false` if deleted
> - `FileChangeListener`: returns a `FileChangeType` enum (`CREATED`, `MODIFIED`, `DELETED`)

---

## 🖥️ How to Run
### ✅ Using Command Line
1. Compile:
```bash
javac -cp filesentry-1.0.0.jar ExampleFileWatcher.java
```

2.Run:
```bash
java -cp .;filesentry-1.0.0.jar ExampleFileWatcher
```

>(On Linux or mac, replace ; with :)

---

## 🧭 Where to Use
- Command-line tools: watch logs or configuration files.

- GUI applications: detect when a file changes and prompt the user to reload.

- Development tools: implement live-reload behavior.

- Scripting: monitor scripts or data files.

---

## 🛠️ Why Use FileSentry?
- Zero configuration: Just point it to a file.

- Cross-platform: Works on Windows, Linux, macOS.

- Small footprint: No heavy dependencies.

- Easy to stop: Clean shutdown with stopWatching().

---

## ✅ Good to Know
- FileSentry uses Java NIO’s WatchService internally.

- To avoid memory leaks, always stop your watcher when you no longer need it.

- You can integrate it into larger apps (editors, tools) by starting/stopping in your app lifecycle hooks.

---

## 🆕 Changelog:

* View all releases on the [Releases Page.](https://github.com/raghul-tech/FileSentry/releases)
* For a detailed log of all changes, refer to the [CHANGELOG.md](CHANGELOG.md) file.

---

## 🤝 Contributing
* We welcome contributions of all kinds:

   * 🛠️ Bug fixes

   * 🌟 Feature suggestions

   * 📚 Documentation improvements

   * 🧪 More usage examples

> Please check the [Contributing Guide](CONTRIBUTING.md) before starting.

---

## 🐞 Report a Bug
   * If you've encountered a bug, please report it by clicking the link below. 
   	This will guide you through the bug-reporting process:
   	➡️ [Click here to report a bug](https://github.com/raghul-tech/FileSentry/issues)
 
---

## 📄 License
- This project is licensed under the [MIT License](LICENSE).

---

## 📬 Contact
Email: [raghultech.app@gmail.com](mailto:raghultech.app@gmail.com)

---

## ☕ Support
> If you find this project useful, consider buying me a coffee!

<a href="https://buymeacoffee.com/raghultech"> <img src="https://img.shields.io/badge/Buy%20Me%20A%20Coffee-Support-orange.svg?style=flat-square" alt="Buy Me A Coffee" /> </a> 
