# Tunnel’d Android App – Codebase and Architecture Review



## Overview

- Tunnel’d is an Android app that locally tracks IPv4/IPv6 address changes, performs DNS leak tests, and offers IP/domain reputation checks, history, analytics, and CSV backup/restore.
- The project is fully FOSS under GPLv3, with an explicit focus on auditable, reproducible builds, no trackers, and a "less is more" resource footprint.


## Module and project structure

- The repo is organized into shared Kotlin Multiplatform modules (`shared/core`, `shared/feature`, `shared/common`) plus the `opensource/composeApp` module that hosts the Android app entry point and platform-specific wiring.
- `shared/core` contains domain models (IP, history, network), application use cases, and infrastructure abstractions/implementations; `shared/feature` holds feature-specific ViewModels and UI logic (home, DNS leak, dashboard, settings, notifications).
- Source sets are split into `commonMain` and `androidMain`, following KMP conventions to separate platform-independent logic from Android-specific implementations (network stack, WorkManager/foreground services, notifications, widgets).


## Domain layer: IP, network, and history

- IP addresses are modeled via a sealed hierarchy: `IpAddress` with concrete `Ip4Address` and `Ip6Address` types backed by `UByteArray`, enforcing correct lengths (4 bytes for IPv4, 16 for IPv6) and providing `stringRepresentation()` helpers.
- Historical data uses the `AddressHistory` sealed interface with `Ipv4` and `Ipv6` data classes that carry an `id`, `domain`, `LocalDateTime` timestamp, `NetworkType`, and optional `IpInfo` (country, city, ISP, ASN, timezone, coordinates). A `copyWithId` helper supports persistence/migration while preserving payload.
- Supporting domain types include `NetworkType` (Wi-Fi/Cellular/VPN), `InternetProtocolVersion`, `IpInfo`, `NotificationPreferences`, `ThemeMode`, and domain events such as `IpAddressChangedEvent`, giving a strongly typed model of networking and UI themes.


## Application layer: use cases

- Use cases in `shared/core/application/usecase` encapsulate business logic: observing current IP, refreshing addresses, saving history, clearing history, exporting/importing CSV, looking up external IPs, and managing notification preferences.
- `RefreshAddressUseCaseImpl` orchestrates the refresh pipeline: obtaining current time (`DateProvider`), determining `NetworkType` (`NetworkTypeObserver`), fetching IP from `IpAddressRemoteDataSource`, performing reverse DNS via `DnsService`, enriching with `GeoIpDataSource.getIpInfo`, updating `CurrentAddressLocalDataSource`, persisting via `SaveAddressHistoryUseCase`, and triggering widget updates.
- Errors are caught at the use-case level, logged via `Logger`, and propagated as domain `Result.Err(RefreshAddressError)` values, while success paths wrap `Result.Ok(Unit)`. This centralizes failure handling and keeps exceptions from leaking into UI code.


## Infrastructure: current IP detection (ipify)

- `IpifyAddressDataSource` encapsulates calls to ipify, a simple public IP address API: it hits IPv4 and IPv6 endpoints from `IpifyConfig` (e.g., `https://api.ipify.org` / `https://api64.ipify.org`) using a Ktor `HttpClient`.
- Responses are read as raw strings and converted into `Ip4Address`/`Ip6Address` via `StringToAddressMapper`, with wrapper functions providing `IpAddressRemoteDataSource` instances used by higher-level use cases.
- This design cleanly separates HTTP details from the domain model but relies on ipify’s stability; if ipify changes response format or adds metadata, the string-to-IP mapper must remain robust to whitespace and minor formatting variations.


## Infrastructure: Geo/IP enrichment (ip2location)

- Geo/IP enrichment is implemented in `IpApiDataSource`, which calls ip2location’s API endpoint with a user-provided API key to fetch JSON containing country, city, ISP, ASN, timezone, and coordinates.
- Instead of using a JSON library, the code parses responses with regular expressions to extract fields like `country_name`, `country_code`, `city_name`, `isp`, `region_name`, `as`, `asn`, `time_zone`, `latitude`, and `longitude`, and returns an `IpInfo` object on success.
- This approach avoids heavier dependencies and matches the "non-bloated" philosophy but is inherently fragile: changes in ip2location’s JSON schema, spacing, or quoting can break regex matches and silently degrade metadata quality.


## Infrastructure: VirusTotal IP/domain scanning

- IP/domain reputation checking is handled by `VtIpLookup` and a Ktor-backed implementation that calls VirusTotal’s v3 endpoints for IP and domain reports using a user-supplied `x-apikey` header.
- Responses are parsed via regex to extract counts for verdict categories such as `harmless`, `malicious`, `suspicious`, and `undetected`, which populate a `VtIpReport` domain object with convenience properties for totals and "isClean" status.
- The lookup returns `null` on errors or when an `error` field is detected in the JSON, minimizing risk of misusing partial or invalid data but at the cost of limited visibility into rate limits, quota exhaustion, or misconfigured keys.


## Infrastructure: storage, preferences, and transactions

- Local persistence uses Room/SQLite, with entities like `AddressHistoryEntity`, enums for `NetworkType` and `AddressVersion`, and DAOs such as `AddressHistoryDao` managing queries, inserts, and migrations.
- `RoomAddressHistoryDataSource` implements domain access to history, while `RoomTransactionScope` and `TransactionProvider` abstract transactions, enabling atomic multi-step operations (e.g., updating current address and appending history) without exposing Room details to use cases.
- User preferences and settings (notification options, GeoIP provider preference, VirusTotal API key, external link behavior) are stored via DataStore-backed data sources such as `DataStoreNotificationPreferencesDataSource` and `UserPreferencesDataSource`, keeping configuration state off the database.


## Infrastructure: events, result types, logging

- The app defines an `EventBus` abstraction with a `SharedFlowEventBus` implementation that pushes domain events like `IpAddressChangedEvent` through Kotlin Flows to listeners, decoupling producers (use cases) from consumers (UI, background services).
- A custom `Result` type models success and error states explicitly, helping distinguish recoverable failures (e.g., transient network issues) from logic errors, and making error handling more declarative.
- Logging is centralized via a `Logger` interface and implementations that tie into platform logging, allowing use cases and infrastructure components to report issues without leaking platform specifics.


## Background monitoring and scheduling

- Background monitoring is abstracted by `RealTimeNetworkMonitor` and `PeriodicWorkManager` interfaces, exposing `start()`, `stop()`, and an `isRunning` `Flow<Boolean>`.
- Real-time monitoring corresponds to the foreground service and persistent notification mode described in the README, designed to react instantly to IP changes and update widgets and history in near real time.
- Periodic work matches the 30-minute background refresh option, giving coarse-grained monitoring without keeping a foreground service active.


## Feature layer: Home, DNS leak, Dashboard, Settings, Notifications

- The Home feature includes UI components like `HomeScreen`, `HomeRoute`, `HomeTopBar`, `HomeSearchBar`, filters and lookup modals (`FiltersModal`, `LookupExternalIpModal`, `VtLookupModal`), and a `HomeViewModel` coordinating current IP, history filters, exports, and lookups.
- The DNS leak feature has a dedicated module (`DnsLeakModule`) and `DnsLeakViewModel`/`DnsLeakScreen` components, anchoring in-app DNS leak tests separate from general IP history flows.
- The Dashboard feature provides charted IP history: network-type distributions, country counts, and time-range filters.
- Settings and Notifications modules (`SharedFeatureSettingsModule`, `SharedFeatureNotificationsModule`) handle GeoIP provider selection, VirusTotal API key storage, notification behavior, and routes like `SettingsRoute` and `NotificationsRoute` for UI navigation.


## HomeViewModel: filters, refresh, export

- `HomeViewModel` is central to the home experience: it subscribes to `ObserveCurrentIpAddressUseCase` for IPv4 and IPv6 streams and maps `AddressStatus<IpAddress>` into UI models with appropriate loading/error/success states.
- Filtering logic uses a `Filter` domain object carrying query text, selected protocol versions, and network types; changes to filters drive `flatMapLatest` on history observations, giving live-updated lists that feed Paging adapters.
- The refresh function guards against concurrent runs using an `_isRefreshing` flag, fires both IPv4 and IPv6 refresh use cases with a configurable delay, awaits both via `awaitAll`, and resets state. This reduces duplicate work and ensures cohesive updates for both protocols.


## CSV export implementation

- CSV export builds a header `address,version,network_type,country,country_code,city,isp,org,asn,timezone,latitude,longitude,timestamp` and iterates over filtered `AddressHistory` items.
- Each value is converted to a string, double-quoted, and internal quotes are escaped by replacing `"` with `""`, matching common CSV conventions; numeric fields (latitude, longitude) and timestamps are formatted in a straightforward way for external tools.
- The resulting CSV string is exposed to the UI, where it can be saved to a file or shared.


## DNS leak testing and networking context

- The DNS leak test feature relies on a `DnsService` abstraction for reverse lookups and possibly additional resolvers to detect which DNS servers are actually handling queries, aligning with external research on Android DNS leaks outside VPN tunnels.
- The security documentation emphasizes that Tunnel’d can only report what it observes through Android’s networking APIs and configured services; it cannot fully guarantee how the OS or VPN apps route traffic, especially given known Android VPN/IP leak behaviors.
- This positioning is consistent with a measurement-and-monitoring tool: the app surfaces potential leaks and mismatches between expected and actual DNS/IP behavior rather than trying to enforce system-wide fixes.


## Security and privacy design in code

- External API calls (ipify, ip2location, VirusTotal) require configuration and, for VT and ip2location, user-provided API keys; no hard-coded shared secrets are present in the open-source code, reducing risk of key leakage and bulk tracking.
- IP history, metadata, and analytics are stored locally in Room and accessed by dashboards and CSV exports; upcoming server-side logging is explicitly described as optional, preserving a default local-first privacy posture.
- The security policy and README note that Tunnel’d does not install VPNs or alter low-level routing; instead, it focuses on transparency around IP changes, DNS usage, and reputation scores, helping users audit other components in their stack.


## Testing and migrations

- Existing tests include at least one Room migration test (`FindMyIP5MigrationTest`), validating schema changes for `FindMyIpDatabase` and ensuring history data remains consistent across version bumps.
- Test coverage appears focused on persistence integrity rather than broader networking, parsing, or UI flows, which is pragmatic but leaves room for expanding tests around regex parsers, use-case error handling, and CSV formatting.
- Migration schemas for Room are stored under `opensource/composeApp/schemas/...`.


## Performance and resource usage

- README benchmarks cite 20–55 MB memory usage across working modes (background monitoring, real-time tracking, dashboards), with attention to avoiding leaked UI components and maintaining healthy heap usage.
- The layered architecture, limited dependencies, and careful use of KMP/Compose suggest a relatively lean runtime compared with heavier VPN or analytics apps.
- Background services and periodic work are abstracted in a way that allows tuning (e.g., disabling 30-minute checks by default, opting into foreground monitoring only when needed) to balance visibility and battery/resource consumption.


## Strengths of the codebase

- Clear layered architecture (domain, application/use cases, infrastructure, features/UI) with KMP separation between common logic and Android-specific implementations.
- Strong domain modeling for IP, history, network types, events, and preferences, enabling type-safe reasoning about networking and privacy-relevant data.
- Use cases encapsulate business logic, with explicit error handling, logging, and domain `Result` types; side effects and infrastructure dependencies are injected rather than hard-coded.
- CSV export, history, and analytics implementations are well designed for offline analysis and long-term monitoring.


## Limitations and improvement opportunities

- JSON parsing via regex in GeoIP and VirusTotal integrations is fragile and could be replaced with lightweight serialization helpers or more robust parsers without significantly increasing dependencies.
- Error reporting for VirusTotal and GeoIP could be richer (distinguishing connectivity, quota, and key issues) and surfaced in the UI so advanced users can debug failures.
- Test coverage beyond Room migrations appears limited; adding unit tests around parsers, use-case orchestration, background monitoring, and CSV formatting would increase confidence and protect against regressions.
- Android-specific implementations of background monitoring, notifications, and widgets are critical to behavior but are less visible in shared code; documenting these and expanding platform-specific tests would further strengthen reliability.


## Summary

- Tunnel’d’s codebase is well-structured, modern, and aligned with its privacy-first, lightweight philosophy, using strong domain modeling and clear use-case and infrastructure layers.
- The main technical risks lie in fragile JSON parsing and relatively narrow test coverage, which can be mitigated with targeted refactors and expanded tests while preserving the app’s low-bloat character.
