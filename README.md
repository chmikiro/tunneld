# Tunnel'd

Android app that logs your IP address changes, tests for DNS leaks, and exports your connection history.

Inspired by [Find My IP](https://github.com/maksimowiczm/find-my-ip) by Mateusz Maksimowicz.

<img width="3808" height="3808" alt="Cleaned_2026_9312_Privacy" src="https://github.com/user-attachments/assets/52612837-dda5-42bc-bc4d-a13024eaa55c" />


## What it does

- Shows your current IPv4 and IPv6 address with geolocation (country, city, ISP, ASN org) – Pick one of the 3 providers in settings
- Keeps a searchable history of past IP addresses
- Runs in the background with periodic refresh and notifications on IP change
- DNS leak test: checks which DNS servers your device is actually using – Please note that this service is delivered by us, you can find the web version on [ipdia.li](https://ipdia.li)
- Filters history by IP version (v4/v6), network type (Wi-Fi/Cellular/VPN), and country (partial, case-insensitive)
- Exports filtered history to CSV `(address, version, network_type, country, country_code, city, isp, org, timezone, latitude, longitude, timestamp)`


## Tech

- Kotlin Multiplatform (KMP) + Compose Multiplatform
- Room (SQLite) for local history storage
- Koin for DI
- Material 3 Expressive UI
- Targets Android (iOS target present but not the focus)


## VT Scan Report
- No false positives on [Virus Total](https://www.virustotal.com/gui/file/a12d3244a59634b4ce0c1314affe917c658573590b8c1ab052dd88d070be3610/detection)


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
