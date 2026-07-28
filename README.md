# Tunnel'd

Android app that logs your IP address changes, tests for DNS leaks, and exports your connection history.

Inspired by [Find My IP](https://github.com/maksimowiczm/find-my-ip) by Mateusz Maksimowicz.

## What it does

- Shows your current IPv4 and IPv6 address with geolocation (country, city, ISP, ASN org)
- Keeps a searchable history of past IP addresses
- Runs in the background with periodic refresh and notifications on IP change
- DNS leak test — checks which DNS servers your device is actually using
- Filters history by IP version (v4/v6), network type (Wi-Fi/Cellular/VPN), and country (partial, case-insensitive)
- Exports filtered history to CSV

## Tech

- Kotlin Multiplatform (KMP) + Compose Multiplatform
- Room (SQLite) for local history storage
- Koin for DI
- Material 3 Expressive UI
- Targets Android (iOS target present but not the focus)

## Get it

- F-Droid: `com.tunneld.ipdiali`
- GitHub Releases: download the signed APK from [releases](https://github.com/chmikiro/tunneld/releases)

## Build

```bash
./gradlew :opensource:composeApp:assembleRelease
```

Requires JDK 21 and Android SDK 35+.

Signing: drop your keystore at the project root as `findmyip-release.keystore` (alias/key/password: `findmyip`) or update `opensource/composeApp/build.gradle.kts`.

## License

GPLv3 — see [LICENSE](LICENSE).
