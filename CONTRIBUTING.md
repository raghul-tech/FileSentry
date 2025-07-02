# 🙌 Contributing to FileSentry

Thank you for your interest in contributing to **FileSentry**! 🎉  
We welcome all kinds of contributions — from bug fixes and documentation improvements to new features and performance enhancements.

---

## 💡 How to Contribute

### 1️⃣ Fork the Repository
Click the **"Fork"** button at the top right of the [FileSentry repository](https://github.com/raghul-tech/FileSentry) to create your own copy.

---

### 2️⃣ Clone Your Fork
Clone your forked repository to your local machine:

```bash
git clone https://github.com/YOUR_USERNAME/FileSentry.git
cd FileSentry
```

*(Replace `YOUR_USERNAME` with your GitHub username.)*

---

### 3️⃣ Create a Branch
Create a new branch for your changes:

```bash
git checkout -b fix/file-change-detection
```

Use a **descriptive branch name** that explains what you’re working on.

---

### 4️⃣ Make Your Changes
- Update code.
- Add or improve tests.
- Write or update documentation.
- Verify your changes work as expected.

---

### 5️⃣ Commit and Push
Commit your changes with a clear message:

```bash
git add .
git commit -m "Fix: handle edge case when watched file is deleted"
git push origin fix/file-change-detection
```

---

### 6️⃣ Open a Pull Request
1. Go to your forked repository on GitHub.
2. Click **“Compare & pull request.”**
3. Provide a clear description of your changes.
4. Click **“Create pull request.”**

We’ll review it as soon as possible!

---

## 🧪 Adding Examples
If you’d like to contribute example usage:

- Place examples in the `examples/` directory.
- Keep examples **simple**, showing one concept at a time.

---

## ✅ Code Style Guidelines
- Use clear, descriptive names.
- Keep methods small and focused.
- Prefer `StandardCharsets.UTF_8` over string literals.
- Use `try-catch` responsibly and avoid swallowing exceptions silently.
- Follow existing formatting and indentation patterns.

---

## 🧹 Running Tests
Before opening your pull request, please run:

```bash
mvn clean verify
```

This ensures your changes pass all checks.

---

## 📜 License
By contributing, you agree that your contributions will be licensed under the [MIT License](LICENSE).

---

## ❤️ Thank You
Your help makes **FileSentry** better for everyone.  
Thank you for contributing!
