# Tunnel'd

Android app that logs your IP address changes, tests for DNS leaks, and exports your connection history.

Inspired by [Find My IP](https://github.com/maksimowiczm/find-my-ip) by Mateusz Maksimowicz.


<img width="3808" height="3808" alt="Cleaned_2026_4tunneld
https://github.com/chmikiro/tunneld/tree/main865_Privacy" src="https://github.com/user-attachments/assets/291d29ff-2355-4d91-9f55-f5e10a4f653c" />

<img width="3808" height="3808" alt="Cleaned_2026_1096_Privacy" src="https://github.com/user-attachments/assets/a8ac1f6d-56d7-400d-bfa5-80384f6d6886" />


## What it does

- Shows your current IPv4 and IPv6 address with geolocation (country, city, ISP, ASN org) – Pick one of the 3 providers in settings
- Keeps a searchable history of past IP addresses
- Runs in the background with periodic refresh and notifications on IP change
- DNS leak test: checks which DNS servers your device is actually using – Please note that this service is delivered by us, you can find the web version on [ipdia.li](https://ipdia.li)
- Filters history by IP version (v4/v6), network type (Wi-Fi/Cellular/VPN), and country (partial, case-insensitive)
- Exports filtered history to CSV `(address, version, network_type, country, country_code, city, isp, org, timezone, latitude, longitude, timestamp)`
- Import/restore previously exported history
- Visualize your activity through dynamic analytics leveraging key fields, as listed above (expected for next release. 100% local)
- WebRTC leak test without delegation. This is useful for users of VPN, proxy, etc. (future release)
- Option to use our servers for IP Logging including, respectively GeoIP and IP identification (future release)
- And much more, with always the intention to keep this lightweight on both client and server side: read more about our [DNS Leak Test architecture](https://leak.ipdia.li)


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
- No false positives on [Virus Total](https://[www.virustotal.com/gui/file/a12d3244a59634b4ce0c1314affe917c658573590b8c1ab052dd88d070be3610/detection](https://www.virustotal.com/gui/file/c0a8ffb532ebb7d1c67f9f289a63e10efaf4f9859817b58588ed109a80970502/detection))


## Get it

- GitHub Releases: download the signed APK from [releases](https://github.com/chmikiro/tunneld/releases)


## Build

```bash
./gradlew :opensource:composeApp:assembleRelease
```

Requires JDK 21 and Android SDK 35+.

Signing: drop your keystore at the project root as `findmyip-release.keystore` (alias/key/password: `findmyip`) or update `opensource/composeApp/build.gradle.kts`.


## License

GPLv3 — see [LICENSE](LICENSE).
