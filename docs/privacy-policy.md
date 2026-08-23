# Privacy Policy — Tunnel'd

_Last updated: August 16, 2026_

Initially crafted to fill personal needs, Tunnel'd is now made public as a **free, open-source software**, with no commercial purpose, no monetization, and no ads.

Tunnel'd does not collect, store, or share any personal or sensitive user data externally. All data stays on your device unless you explicitly export it.


## Data Processing — User Controlled Third-Party Services

Tunnel'd contacts the following services to function. Each request inherently includes your device's IP address as part of the TCP connection:

- **ipify** ([ipify.org](https://www.ipify.org/)) — used to determine your public IP address itself (IP identification, not enrichment). This is currently the only IP-identification provider; a future release will let you choose your own. ipify states they do not store or track request data. [Privacy Policy](https://geo.ipify.org/privacy-policy)
- **ip2location** ([ip2location.io](https://www.ip2location.io/)) — enrichment provider: adds geolocation data (country, city, ISP, ASN, coordinates, timezone) on top of the IP address already identified. [Privacy Policy](https://www.ip2location.io/privacy-policy)
- **ipapi.co** ([ipapi.co](https://ipapi.co/)) — alternative enrichment provider. [Privacy Policy](https://ipapi.co/privacy/)
- **ipinfo.io** ([ipinfo.io](https://ipinfo.io/)) — alternative enrichment provider. [Privacy Policy](https://ipinfo.io/privacy-policy)
- **ipdia.li** ([ipdia.li](https://leak.ipdia.li/)) — DNS leak test. Runs on self-hosted infrastructure with no third-party analytics or tracking.

**IP identification** *(via ipify)* and **enrichment** *(via ip2location, ipapi.co, or ipinfo.io)* are separate steps. Which enrichment provider is used depends on your selection in `Settings > Geo IP Data Source`. You may disable enrichment entirely and only identify your IP.

By default, Tunnel'd is set to work without any external network access. The app will still display and locally log your IP address and network type as detected by Android's system APIs.

For visibility, an explicit "Cut internet access" toggle will be added in future release.


## Local Data Storage & Retention

Tunnel'd stores your IP address history locally on the device using an on-device database. This data is retained indefinitely until you explicitly clear it `Settings > Clear History` or uninstall the app.
Data is yours, by design. So it naturally never leaves your device unless you explicitly choose to export it (CSV export to a location of your choice).


## No Analytics, No Telemetry

Tunnel'd contains no advertising SDKs, no analytics frameworks, no crash reporters, and no telemetry of any kind. It is a standalone, non-commercial app ; no account required, no backend to phone home to.

The only way to report bugs and/or to suggest features is by opening an [issue](https://github.com/chmikiro/tunneld/issues) on GitHub.


## Consent

Consent is granular and tied to your choices in Settings:

- Skipping IP identification and enrichment entirely (fully offline mode) means no data is sent to any third party.
- Enabling IP identification (ipify) sends your IP address to that service only.
- Enabling enrichment additionally sends your IP address to the specific provider you selected in `Settings > Geo IP Data Source`.

By enabling any of the above, you consent to your IP address being sent to the corresponding third-party service solely for the purpose stated. You can change or revoke these choices at any time in Settings.


## Contact

For questions or concerns, open an issue at [github.com/chmikiro/tunneld](https://github.com/chmikiro/tunneld).

For privacy-specific concerns, you can also send an email to [chmikiro.tunneld@couldyoutellwho.im](mailto:chmikiro.tunneld@couldyoutellwho.im).
