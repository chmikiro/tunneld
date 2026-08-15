# Changelog

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
