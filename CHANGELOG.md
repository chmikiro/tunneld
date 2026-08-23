# Changelog

## v0.4.0 (2026-08-23)

### New features

- **VirusTotal reputation lookup** — bug icon on the home screen. Enter any IP address or domain to see its security reputation (harmless / malicious / suspicious / undetected). Requires your own VirusTotal API key (Settings > Data & Backup), never embedded or proxied
- **Open results in your browser** — from a VirusTotal result or the current-IP detail, open the report/provider page in an external browser. A confirmation dialog appears first; "Don't show this message again" is remembered separately for each
- **About section** — bottom of Settings: app version, license (GPL-3.0), and links to GitHub, ipdia.li, Privacy Policy, and Security & Trust

### Changed (defaults noted)

- **Home top bar** — the "Lookup external IP" button is replaced by two icons: a globe (IP geolocation) and a bug (VirusTotal). The geolocation lookup itself is unchanged and stays IP-only
- **Settings footer** — links are shown two per row
- **Signing fingerprint** — removed from About (F-Droid re-signs the app, so an in-app fingerprint would be misleading)

## v0.3.0 (2026-08-14)

### New features

- **Real-time tracking** — off by default. When enabled (Settings > Behavior), a foreground service detects IP changes instantly and shows a persistent notification. The existing periodic check (30 min, also off by default) remains a separate, independent toggle
- **Analytics dashboard** — new screen in the top bar. Visualizes your IP history: network-type chart, country counts, time-range filters (Today / 7 days / 30 days / All time)
- **Lookup external IP** — button on the home screen. Enter any IP (e.g. 8.8.8.8) to see its country, ISP, and provider details
- **Home screen widget (beta)** — optional; add it from your launcher's widget picker. Shows current IP, country, city, ISP, and organization, and updates when the IP changes


### Changed (defaults noted)

- **Top bar** — filled with the theme background by default. A translucent top bar option is available in Settings > Appearance
- **Search filter** — the country filter now matches IP addresses too (e.g. 192.168 or Morocco)
- **Settings** — reorganized into Behavior, Data & Backup, Appearance, and Red Zone
