# AGENTS.md

## Cursor Cloud specific instructions

This repo is a single **Android application** (Guitar Tuner) written in Kotlin + Jetpack
Compose, built with Gradle. There is one Gradle module: `:app`. Standard build/run commands
are documented in `readme.md` ("Project Setup"); the notes below only cover non-obvious,
cloud-environment-specific caveats.

### Toolchain (already provisioned in the environment/snapshot)
- **JDK 17** is the system default (`java`/`javac` via `update-alternatives`), matching CI
  (`.github/workflows/build.yml`). Do not rely on JDK 21 — use 17.
- **Android SDK** lives at `~/Android/sdk` (`platform-tools`, `platforms;android-36`,
  `build-tools;36.0.0`, plus `emulator` + `system-images;android-34;google_apis;x86_64`).
- `local.properties` (git-ignored) must contain `sdk.dir=/home/ubuntu/Android/sdk`. The
  startup update script recreates it, so you normally don't need to touch it.

### Build / test / lint (module `:app`)
- Build debug APK: `./gradlew assembleDebug` (output: `app/build/outputs/apk/debug/app-debug.apk`).
- Unit tests: `./gradlew testDebugUnitTest`.
- Lint: `./gradlew lintDebug` (HTML report: `app/build/reports/lint-results-debug.html`).
- Many Kotlin/Compose **deprecation warnings** are printed during compilation — they are
  expected and do not fail the build.
- The debug build type uses `applicationIdSuffix = .dev`, so the installed package is
  `com.example.guitartuner.dev` (launcher activity `com.example.guitartuner.ui.MainActivity`).

### Running the app on an emulator (important gotchas)
- **Nested KVM is broken on this host.** Attempting a hardware-accelerated boot triggers a
  host `kernel BUG at arch/x86/kvm/x86.c:702` (`kvm_spurious_fault`) when QEMU creates a
  vCPU, and the guest never boots. Do **not** rely on `/dev/kvm` (even though
  `emulator -accel-check` reports it "usable").
- Launch the emulator with **software emulation** instead: add `-no-accel`, e.g.
  `emulator -avd tuner_test -no-window -no-audio -no-boot-anim -gpu swiftshader_indirect -no-accel -no-snapshot`.
  This works but is **slow** (~8 min to `sys.boot_completed=1`).
- Because it is headless (`-no-window`) and slow, drive it via `adb`: `adb exec-out screencap -p`
  for screenshots and `adb shell input tap X Y` / `adb shell monkey ...` to interact.
- The slowness frequently pops **"System UI isn't responding" / "Process system isn't
  responding" ANR dialogs**. Tap **Wait** (do not Close app) and give it time; these are an
  emulator-performance artifact, not an app bug.
- Grant runtime permissions non-interactively to avoid fighting the ANRs, e.g.
  `adb shell pm grant com.example.guitartuner.dev android.permission.RECORD_AUDIO`
  (the tuner needs the microphone).
- An AVD named `tuner_test` (pixel_5, android-34 google_apis x86_64) is already created.
