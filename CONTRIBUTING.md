# Contributing to Facial Photo Management

Thank you for your interest in contributing! This document provides guidelines and instructions for contributing.

## 📋 Code of Conduct

Please be respectful and constructive in all interactions. We're building something together!

## 🚀 Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/saurabhkumar1432/facialPhotoManagement.git
   ```
3. **Create a branch** for your changes:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## 💻 Development Setup

### Requirements
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API 34

### Building
```bash
# Debug build
./gradlew assembleDebug

# Run tests
./gradlew test

# Lint check
./gradlew lint
```

## 📝 Making Changes

### Code Style
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add comments for complex logic
- Keep functions small and focused

### Commit Messages
Use clear, descriptive commit messages:
```
type: brief description

Longer explanation if needed.
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

Examples:
- `feat: add face grouping threshold setting`
- `fix: resolve crash on photo deletion`
- `docs: update README with new features`

## 🔄 Pull Request Process

1. **Update documentation** if needed
2. **Test your changes** thoroughly
3. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```
4. **Create a Pull Request** on GitHub
5. **Describe your changes** in the PR description
6. **Wait for review** - we'll respond as soon as possible

### PR Checklist
- [ ] Code follows project style
- [ ] Self-reviewed the code
- [ ] Added/updated comments where needed
- [ ] Changes don't break existing functionality
- [ ] New features include documentation updates

## 🐛 Reporting Bugs

When reporting bugs, please include:
- Device model and Android version
- Steps to reproduce
- Expected vs actual behavior
- Screenshots if applicable
- Logcat output if available

## 💡 Feature Requests

We welcome feature ideas! Please:
- Check existing issues first
- Describe the use case
- Explain why it would be valuable

## ❓ Questions?

Open an issue with the `question` label or reach out to the maintainers.

---

Thank you for contributing! 🙏
