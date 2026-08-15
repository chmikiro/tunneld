# Changelog

## v0.3.0 (2026-08-14)

### Added
- Android home screen widget (Glance): current IP card with country, ISP, org, and refresh
- Widget auto-refreshes when IP changes via WidgetUpdateHook in the refresh pipeline
- Time range filter in Dashboard: All time / Last 30 days / Last 7 days / Today
- Settings screen reorganization: Behavior, Data & Backup, Appearance, Red Zone sections
- Translucent Top Bar toggle (Appearance section)

### Changed
- "Tunnel'd" text brand replaces globe icon in top bar — bold text adapts to theme
- Top bar now filled with theme background color by default (was transparent)
- Dashboard countries metric now counts all unique countries (was capped at 10)
- Lookup external IP label simplified to "IP address" (removed domain reference)
- Network type hidden from lookup result card (set to UNKNOWN internally)

### Fixed
- Dashboard Cellular entries included in network type donut chart
- Version display in Settings updated across releases

### Technical
- ShowNetworkType parameter added to AddressButton to conditionally render network type
- Time filtering for dashboard uses epoch-day comparison (no DAO changes)
- Widget data syncs through SharedPreferences + APPWIDGET_UPDATE broadcast