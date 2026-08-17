# Tunnel'd

Android app that **locally** tracks IP address changes, performs DNS leak tests, and includes JSON/CSV backup & restore. Future features include [WebRTC leak](https://github.com/chmikiro/tunneld/blob/main/educational/WEBRTCLEAK.md) testing, device identity leak detection, in-app APK/link scanning against antivirus providers, and optional AI-powered personalized reports (API keys optional). Built **without** _**bloated frameworks**_, prioritizing fully **FOSS**, **auditable**, and **reproducible** solutions.



<div align="center">

<img src="https://github.com/user-attachments/assets/0dd183a1-a193-4d93-a66d-c28b6cbcaf4f" width="200" alt="Home screen" />
&nbsp;
<img src="https://github.com/user-attachments/assets/3a0b2b96-ccd7-4c01-85a4-96c620d913ae"" width="200" alt="Lookup external IP" />

<br />

<img src="https://github.com/user-attachments/assets/8300de80-efda-41c4-a6da-3a78d3fff6f6" width="200" alt="DNS leak test" />
&nbsp;
<img src="https://github.com/user-attachments/assets/efec6129-a001-47d2-a989-48aef4c13eed" width="200" alt="Dashboard analytics" />

<br />

<img src="https://github.com/user-attachments/assets/9ec56919-d20c-495d-a913-7762fdd6c7e0" width="200" alt="Settings overview" />
&nbsp;
<img src="https://github.com/user-attachments/assets/474689c7-5032-4a77-9a97-a9d85070089e" width="200" alt="Focus service & data source" />

</div>


## What it does

### Core IP & Tracking Features

| Feature | Description | Status |
| :--- | :--- | :--- |
| **IP & Geolocation Display** | Shows current IPv4 and IPv6 address with geolocation data (country, city, ISP, ASN org). Users can pick one of 3 providers in settings [*(related privacy policy)*](https://github.com/chmikiro/tunneld/blob/895bf85802c570b9d27c6ad635ceafbe4495db2f/docs/privacy-policy.md). | Current |
| **Lookup External IP** | Look up any IP address (e.g. 8.8.8.8) from the home screen to see its country, ISP, and provider details. | Current |
| **Background Monitoring** | Periodic refresh (30 min, off by default) that sends notifications upon IP change. | Current |
| **Real-time Tracking** | Off by default (Settings > Behavior). A foreground service detects IP changes instantly and shows a persistent notification, independent of periodic checks. | Current |
| **Home Screen Widget (Beta)** | Optional widget showing current IP, country, city, ISP, and organization; updates automatically on IP change. | Current (Beta) |
| **Lightweight Architecture** | Designed to remain lightweight on both the client and server side. | Current |

### History & Data Management

| Feature | Description | Status |
| :--- | :--- | :--- |
| **Searchable History** | Keeps a fully searchable history of past IP addresses. Country filter also matches IP addresses (e.g. 192.168 or Morocco). | Current |
| **Advanced Filtering** | Filters history by IP version (v4/v6), network type (Wi-Fi/Cellular/VPN), and country/IP (partial, case-insensitive). | Current |
| **Semi-static Analytics Dashboard** | New top-bar screen to access charted IP history (Aka Data Viz): network-type chart, country counts, and time-range filters (Today / 7 days / 30 days / All time), 100% local processing. | Current |
| **Dynamic Analytics Dashboard** | Fully flexible charts with more data viz variants dynamically updating each component (a la power pivot): full dataset, custom time range, inherit main screen filters, etc. | Future release |
| **CSV Export** | Exports filtered history to CSV (includes address, version, network_type, country, country_code, city, isp, org, timezone, latitude, longitude, timestamp). | Current |
| **Import & Restore** | Import and restore previously exported history files. | Current |
| **Options for IP identification provider** | Pick from a list the provider identifying IP address (currently only [ipify.org](https://github.com/chmikiro/tunneld/blob/895bf85802c570b9d27c6ad635ceafbe4495db2f/docs/privacy-policy.md)). | Next release |

### Security & Leak Tests

| Feature | Description | Status |
| :--- | :--- | :--- |
| **DNS Leak Test** | Checks which DNS servers your device is actually using. Delivered natively by us (web version available on ipdia.li). | Current |
| **WebRTC Leak Test** | Conducts WebRTC leak tests without delegation. Highly useful for users of VPNs, proxies, etc. | Future Release |

## Upcoming Server Features

| Feature | Description | Status |
| :--- | :--- | :--- |
| **Native IP Logging** | Option to use our servers for IP Logging, including GeoIP and IP identification. | Future Release |
| **Device Identity Leak Detection** | Detects device-level identity leaks. | Future Release |
| **In-App APK/Link Scanning** | Scans APKs and links against antivirus providers. | Future Release *(next release for links and IPs)* |
| **AI-Powered Personalized Reports** | Optional AI-generated reports *(optional, with your API keys)*. | Future Release |

## Why Tunnel'd
- Beyond bundling all of the above in non-bloated shell ..
  .. The app is efficient by design. Tunnel'd runs continuously in the background (conditioned by user opt-in), monitoring IP changes and DNS leaks.
- Less is more philosophy.
- The app uses from 20 to 55 MB of memory *(depending on chosen working mode)*, with no leaked UI components and healthy heap usage. Lightweight enough to run all day without draining your device.
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
