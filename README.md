# Tunnel'd

Android app that **locally** tracks IP address changes, performs DNS leak tests, and includes JSON/CSV backup & restore. Future features include dynamic in-app analytics, WebRTC leak testing, device identity leak detection, in-app APK/link scanning against antivirus providers, and optional AI-powered personalized reports (API keys optional). Built **without** _**bloated frameworks**_, prioritizing fully **FOSS**, **auditable**, and **reproducible** solutions.


<img width="3808" height="3808" alt="Cleaned_2026_4tunneld
https://github.com/chmikiro/tunneld/tree/main865_Privacy" src="https://github.com/user-attachments/assets/291d29ff-2355-4d91-9f55-f5e10a4f653c" />

<img width="3808" height="3808" alt="Cleaned_2026_1096_Privacy" src="https://github.com/user-attachments/assets/a8ac1f6d-56d7-400d-bfa5-80384f6d6886" />


## What it does

### Core IP & Tracking Features

| Feature | Description | Status |
| :--- | :--- | :--- |
| **IP & Geolocation Display** | Shows current IPv4 and IPv6 address with geolocation data (country, city, ISP, ASN org). Users can pick one of 3 providers in settings. | Current |
| **Background Monitoring** | Runs in the background with periodic refresh and sends notifications upon IP change. | Current |
| **Lightweight Architecture** | Designed to remain lightweight on both the client and server side. | Current |

### History & Data Management

| Feature | Description | Status |
| :--- | :--- | :--- |
| **Searchable History** | Keeps a fully searchable history of past IP addresses. | Current |
| **Advanced Filtering** | Filters history by IP version (v4/v6), network type (Wi-Fi/Cellular/VPN), and country (partial, case-insensitive). | Current |
| **CSV Export** | Exports filtered history to CSV (includes address, version, network_type, country, country_code, city, isp, org, timezone, latitude, longitude, timestamp). | Current |
| **Import & Restore** | Import and restore previously exported history files. | Current |

### Security & Leak Tests

| Feature | Description | Status |
| :--- | :--- | :--- |
| **DNS Leak Test** | Checks which DNS servers your device is actually using. Delivered natively by us (web version available on ipdia.li). | Current |
| **WebRTC Leak Test** | Conducts WebRTC leak tests without delegation. Highly useful for users of VPNs, proxies, etc. | Future Release |

## Upcoming Analytics & Server Features

| Feature | Description | Status |
| :--- | :--- | :--- |
| **Dynamic Analytics** | Visualize your activity through dynamic analytics leveraging key data fields (100% local processing). | Next Release |
| **Native IP Logging** | Option to use our servers for IP Logging, including GeoIP and IP identification. | Future Release |


## Why Tunnel'd
- Beyond bundling all of the above in non-bloated shell ..
  .. The app is efficient by design. Tunnel'd runs continuously in the background (conditioned by user opt-in), monitoring IP changes and DNS leaks .
The app uses around 20-25 MB of memory, with no leaked UI components and healthy heap usage. Lightweight enough to run all day without draining your device.
`*Benchmark includes Samsung S25 (Android 16) and Samsung A32 (Android 13)`

## Tech

- Kotlin Multiplatform (KMP) + Compose Multiplatform
- Room (SQLite) for local history storage
- Koin for DI
- Material 3 Expressive UI
- Targets Android (iOS target present but not the focus)


## VT Scan Report
- No false positives on [Virus Total](https://www.virustotal.com/gui/file-analysis/YTkwZGUwNGFiYzhiMGM2MzBkZmI2OWY5MjM1MmQ1ZDM6MTc4Njc5NzQ0MQ==/detection)


## Get it

- GitHub Releases: download the signed APK from [releases](https://github.com/chmikiro/tunneld/releases)


## Build

```bash
./gradlew :opensource:composeApp:assembleRelease
```

Requires JDK 21 and Android SDK 35+.

Signing: drop your keystore at the project root as `findmyip-release.keystore` (alias/key/password: `findmyip`) or update `opensource/composeApp/build.gradle.kts`.


## Acknowledgment

- [Mateusz Maksimowicz](https://github.com/maksimowiczm/find-my-ip)


## License

GPLv3 — see [LICENSE](LICENSE).
