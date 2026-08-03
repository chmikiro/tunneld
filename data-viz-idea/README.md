# Tunnel'd Analytics — Data Visualization Feature Idea

**Status:** Idea / Prototype  
**Created:** 2026-08-03  
**Dashboard:** `dashboard.html` (self-contained, open in any browser)  
**Tunnel'd:** [github.com/chmikiro/tunneld](https://github.com/chmikiro/tunneld) — public, v0.2.1

## Origin

On August 3, 2026, I exported my Tunnel'd connection history (1,802 entries over 65 days) and asked Hermes to "prepare a visualization of this data." The goal was simple: see my VPN routing patterns at a glance.

After a few iterations — starting with a plain CSV table, then adding Chart.js charts, then making it interactive with click-to-drill-down — I realized this isn't just a one-off script. This is a feature Tunnel'd should ship.

What started as "visualize my export" became "every Tunnel'd user should have this."

A concrete example: the dashboard revealed only 4 WiFi connections out of 1,802 — moments of inattention (two of which I was napping). Without the viz, these would be buried in a flat list. With it, you instantly see the story your data is telling.

That's why Tunnel'd exists. A true audit trail — it catches what you'd otherwise never know about.

## What

An interactive analytics dashboard that visualizes a Tunnel'd user's connection history. Uses the existing CSV export format as input — zero backend needed.

## Why

Tunnel'd already logs every connection (IP, country, provider, network type, ASN, timestamp). That data is currently only browsable as a flat list. This dashboard turns it into insights:

- **Country distribution** — where does your traffic actually route through?
- **Provider breakdown** — which VPN providers handle most of your connections?
- **Daily timeline** — when are you most active? Any gaps?
- **VPN vs WiFi ratio** — how much of your traffic is actually tunneled?
- **Drill-down** — click any country to see its providers, any provider to see its countries, any day to inspect that day's connections

## Charting Library

**Decision: Chart.js.** Already implemented, mature, and the 200KB gzipped size is invisible on a 4.6MB APK. Alternatives considered but not worth the rewrite effort:

| | Chart.js | uPlot | Custom Canvas |
|---|---|---|---|
| Size (gzipped) | 200KB | 45KB | 0KB |
| APK impact | +200KB (4.6→4.8MB) | +45KB | 0 |
| Effort to wire | 0 (done) | ~3h rewrite | ~5h rewrite |
| Chart types | Bar, line, doughnut, pie | Time series only | All manual |
| Click-to-drill | Built-in plugin | Manually wire | Manually wire |

The 155KB difference between Chart.js and uPlot would require rewriting all four chart types with a time-series-only library. Custom Canvas is "pure" but duplicates mature tooling for no user-facing benefit.

## Standalone Dashboard

The `dashboard.html` is fully self-contained:

- **Drag-and-drop CSV** — drop a Tunnel'd CSV export directly onto the page
- **File picker** — click to browse
- **Dark theme** — matches Tunnel'd aesthetic (#0d1117)
- **Chart.js from CDN** — loads once, cached by browser thereafter
- **ASN column** — in the detail table since v0.2.2
- **JS bridge** — `window.loadTunneldCSV(csv)` exposed for WebView injection

No data leaves your browser. Nothing is uploaded.

## Staged Deployment

This could be rolled out incrementally:

### Stage 1 — In-App WebView
Embed a WebView tab in Tunnel'd that loads the dashboard directly from the app's internal CSV. No export step needed. Pass the CSV path via JavaScript bridge using `window.loadTunneldCSV(csv)`.

> **Static export (bundling dashboard.html as an asset)** is only worth it if it's truly a couple hours of work. If it takes meaningful effort, skip it — put that time directly into WebView dev instead. Don't waste cycles on an intermediate step.

### Stage 3 — Live Interactive
Replace the static HTML approach with native Compose charts. Click events trigger SQL queries against the Room database. Full native performance, offline, no WebView.

## Known Issues

- **CSV file picker:** drag-and-drop works correctly in most browsers, but the click-to-browse file picker may not render data on first load in some environments. If the file picker shows no data after selection, drag the same file onto the drop zone instead. This is a browser security quirk (file:// origin + FileReader timing) and will be irrelevant once the dashboard loads via WebView (Stage 1), where the CSV is injected via JS bridge.

| File | Purpose |
|------|---------|
| `dashboard.html` | Self-contained analytics dashboard with CSV drag-and-drop |
| `README.md` | This document |

## Screenshot (mental)

```
┌──────────────────────────────────────────────────────┐
│ 🔐 Tunnel'd — VPN Connection Analytics               │
│ 1802 connections · 53 countries · 65 days            │
│ ┌──────┐ ┌──────┐ ┌──────────┐ ┌──────┐ ┌──────┐   │
│ │  53  │ │  31  │ │1798 99.8%│ │ 4 (.2)│ │ 65d │   │
│ │Countries│Providers│  VPN   │ │ WiFi │ │ Days│   │
│ └──────┘ └──────┘ └──────────┘ └──────┘ └──────┘   │
│                                                      │
│ Top Countries          Daily Connections             │
│ Germany ██████████ 568 ██▄▃█▂▁█▅▃▁▁▁▂▇▅▄▂▁         │
│ Switzer ██████ 291     May──Jun──Jul──Aug            │
│ France  ███ 145                                      │
│                                                      │
│ Top Providers         ╭──── VPN vs WiFi ────╮        │
│ 31173 S █████████ 744 │    ● VPN  99.8%    │        │
│ DataCamp████ 315      │    ◉ WiFi  0.2%    │        │
│ M247 Eu ████ 259      ╰────────────────────╯        │
│                                                      │
│ Connection Log [Filter...                   ]        │
│ IP            Country    Provider        Time        │
│ 193.32.127..  Switzer..  31173 Servic..  18:19      │
└──────────────────────────────────────────────────────┘
```
