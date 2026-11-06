# GitHub project setup

This checkout was fetched from https://github.com/Ankit-Panday258/daily-kharcha.git, main commit b7f2ac4558c814f750bde451fe4ca60bcb0498cd.

The local build setup uses Gradle 8.9 and JDK 17 with the repository's Android Gradle Plugin 8.5.2. The Gradle wrapper scripts, wrapper JAR, distribution checksum and daemon Java requirement were corrected. Android SDK Platform 34 and Build-Tools 34.0.0 are required.

Open this folder in Android Studio and select JDK 17 as the Gradle JDK. The included README contains build instructions.

From this folder, with JDK 17 and an Android SDK configured, run

```sh
./gradlew assembleDebug testDebugUnitTest lintDebug
```

The APK is generated at `app/build/outputs/apk/debug/app-debug.apk`. It is a debug build of package `com.dailykharcha.app`, supporting Android 7.0 and later.

For installation, copy the APK to an Android phone, open it in Files and allow installation from that Files app if Android asks. Development tools are only needed to rebuild the source.

## Verification results

The GitHub checkout built successfully with the corrected toolchain. All 16 existing JVM unit tests passed. Android lint completed with zero errors and 78 warnings. The warnings include dependency updates, deprecated or redundant API usage, and resource/manifest advisories.

The generated APK passed its APK Signature Scheme v2 check. It installed and launched on an isolated Android 13 ARM64 emulator. Scrolling the Home screen confirmed the empty initial diary. The original launch assertion failed because that empty-diary message was below the visible part of the screen. The corrected check scrolls to the message.

These checks cover this GitHub checkout. Full add/edit/delete/chart interaction testing and physical-phone testing have not been performed for this version. The delivered APK is debug-signed.

Build configuration fixes are included in this checkout. The verified debug APK is included in `apk/Daily-Kharcha-debug.apk`, together with its SHA-256 checksum.
