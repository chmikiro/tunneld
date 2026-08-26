# Tunnel'd

Android app that **locally** tracks IP address changes, performs DNS leak tests, and includes JSON/CSV backup & restore. Future features include [WebRTC leak](https://github.com/chmikiro/tunneld/blob/main/educational/WEBRTCLEAK.md) testing, device identity leak detection, in-app APK/link scanning against antivirus providers, and optional AI-powered personalized reports (API keys optional). Built **without** _**bloated frameworks**_, prioritizing fully **FOSS**, **auditable**, and **reproducible** solutions.



<div align="center">

<img src="https://raw.githubusercontent.com/chmikiro/tunneld/refs/heads/main/docs/screenshots/1.png" width="200" alt="Home screen & VT lookup" />
&nbsp;
<img src="https://raw.githubusercontent.com/chmikiro/tunneld/refs/heads/main/docs/screenshots/2.png" width="200" alt="Current IP card" />

<br />

<img src="https://raw.githubusercontent.com/chmikiro/tunneld/refs/heads/main/docs/screenshots/3.png" width="200" alt="DNS leak test" />
&nbsp;
<img src="https://raw.githubusercontent.com/chmikiro/tunneld/refs/heads/main/docs/screenshots/4.png" width="200" alt="Dashboard analytics" />

<br />
</div>


## What it does

### Core IP & Tracking Features

| Feature | Description | Status |
| :--- | :--- | :--- |
| **IP & Geolocation Display** | Shows current IPv4 and IPv6 address with geolocation data (country, city, ISP, ASN org). Users can pick one of 3 providers in settings [*(related privacy policy)*](https://chmikiro.github.io/tunneld/privacy-policy.html). | Current |
| **Lookup External IP** | Look up any IP address (e.g. 8.8.8.8) from the home screen to see its country, ISP, and provider details. | Current |
| **Background Monitoring** | Periodic refresh (30 min, off by default) that sends notifications upon IP change. | Current |
| **Real-time Tracking** | Off by default (Settings > Behavior). A foreground service detects IP changes instantly and shows a persistent notification, independent of periodic checks. | Current |
| **In-App domain name/link scanning** | Scans links and, specifically, domain names reputation against antivirus providers. | Current *(via VT, more providers to come)* |
| **In-App IP Scanning** | Scans IPs against antivirus providers, with autodetect option for current IP. | Current  *(via VT, more providers to come)* |
| **Home Screen Widget (Beta)** | Optional widget showing current IP, country, city, ISP, and organization; updates automatically on IP change. | Current (Beta) |


<div align="center">

<img src="https://raw.githubusercontent.com/chmikiro/tunneld/refs/heads/main/docs/screenshots/5.png" width="200" alt="Home screen & filters" />
&nbsp;
<img src="https://raw.githubusercontent.com/chmikiro/tunneld/refs/heads/main/docs/screenshots/6.png" width="200" alt="Settings overview" />

<br />

<img src="https://raw.githubusercontent.com/chmikiro/tunneld/refs/heads/main/docs/screenshots/7.png" width="200" alt="Focus service & data source" />
&nbsp;
<img src="https://raw.githubusercontent.com/chmikiro/tunneld/refs/heads/main/docs/screenshots/8.png" width="200" alt="Focus VT API" />

</div>


### History & Data Management

| Feature | Description | Status |
| :--- | :--- | :--- |
| **Searchable History** | Keeps a fully searchable history of past IP addresses. Country filter also matches IP addresses (e.g. 192.168 or Morocco). | Current |
| **Advanced Filtering** | Filters history by IP version (v4/v6), network type (Wi-Fi/Cellular/VPN), and country/IP (partial, case-insensitive). | Current |
| **Semi-static Analytics Dashboard** | New top-bar screen to access charted IP history (Aka Data Viz): network-type chart, country counts, and time-range filters (Today / 7 days / 30 days / All time), 100% local processing. | Current |
| **Dynamic Analytics Dashboard** | Fully flexible charts with more data viz variants dynamically updating each component (a la power pivot): full dataset, custom time range, inherit main screen filters, etc. | Next release |
| **CSV Export** | Exports filtered history to CSV (includes address, version, network_type, country, country_code, city, isp, org, timezone, latitude, longitude, timestamp). | Current |
| **Import & Restore** | Import and restore previously exported history files. | Current |
| **Options for IP identification provider** | Pick from a list the provider identifying IP address (currently only [ipify.org](https://chmikiro.github.io/tunneld/privacy-policy.html)). | Next release |

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
| **In-App APK Scanning** | Scans APKs against antivirus providers. | Future Release |
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
- No false positives on [Virus Total](https://www.virustotal.com/gui/file/520367893356f9251b537987fab7f36f59025d907e7b2359c8c52203d9ad7625/detection)


## Get it

- GitHub Releases: download the signed APK from [releases](https://github.com/chmikiro/tunneld/releases)


## Build

```bash
./gradlew :opensource:composeApp:assembleRelease
```

Requires JDK 21 and Android SDK 35+.


## Acknowledgment

- [Mateusz Maksimowicz](https://github.com/maksimowiczm/find-my-ip)
- [Hossein Pira](https://github.com/code3-dev/dnsleak)


## License

GPLv3 — see [LICENSE](LICENSE).


## Security & Privacy

- **[Privacy Policy](https://chmikiro.github.io/tunneld/privacy-policy.html)**
- **[Trust Boundaries](https://chmikiro.github.io/tunneld/trust-boundaries.html)**
