# About WebRTC Leakage

WebRTC is a browser technology built for real-time peer-to-peer communication — voice, video, file transfers. To establish a direct connection it must discover your real network interfaces, including your local LAN IP and your public IP.

It does this using ICE candidates, and it does it outside your proxy or VPN tunnel. A malicious or curious website can silently fire a small JS snippet — no permissions required, no user prompt — to request those candidates and read your real public IP, even while all your traffic routes through a VPN.

Your VPN hides your IP at the HTTP header layer. WebRTC bypasses that entirely by talking directly to STUN servers at the OS network level. If your browser has WebRTC enabled and your VPN does not block it at the kernel level, your real IP is exposed to any page that asks.

Read more [here](https://ipdia.li).

# VPN WebRTC Empirical Leak Test

Quick note: Non-Leakers ≠ Trusted. Many non-leakers actually track and log your activity.
This is absolutely not an advice: I personally only -relatively- trust Mullvad and Proton.

## Leaking pseudo-VPN Providers

Astrill, Betternet Addons (Browser Plugin), BlackVPN, ChillGlobal,
CyrenVPN, Glype, hide-me.org, Hola!VPN, Hola!VPN Chrome Extension,
Hoxx VPN (Firefox Addon), HTTP PROXY in browser that supports WebRTC,
IBVPN Browser Addon, PHP Proxy, phx.piratebayproxy.co, psiphon3,
SmartHide Proxy, SOCKS PROXY on browsers with WebRTC enabled,
SumRando Web Proxy, TOR as PROXY on browsers with WebRTC enabled

## Non-Leakers

AirVPN, AnonVPN, Anonymizer, AutoVPN, Avast Secureline,
Avira Phantom VPN, AzireVPN, BeeVPN, Betternet, Blockless, BolehVPN,
Boxpn, BTGuard, CactusVPN, Celo, CryptoStorm, CyberGhost,
Disconnect.me, EarthVPN, Encrypt.me, ExpressVPN, FinchVPN,
flter.me VPN, FlyVPN, Freedome, FrootVPN, GetFlix, Hide My IP,
Hide.me, HideALLIP, HideIPVPN, hideman.net, HideMyAss, HideMy.name,
Hotspot Shield, IBVPN, IntroVPN, IPinator, IPredator, IPVanish,
Ironsocket, Ivacy, IVPN, LiquidVPN, Mullvad, My Private Network,
NordVPN, Opera (Browser) VPN, OverPlay, oVPN.com, Perfect Privacy,
Private Internet Access, PrivateTunnel, PrivateVPN, ProtonVPN,
ProXPN, Proxy.sh, PureVPN, Qnap NAS, RA4W VPN, SaferVPN,
SecureVPN.com, SecureVPN.to, SecurityKISS, Seed4.Me, ShadeYou,
SlickVPN, SmartHide, Steganos, StrongVPN, SumRando VPN,
Surfeasy, Surfeasy Addons, SwitchVPN, Synology NAS, Tails, TigerVPN,
TopVPN, Torguard, TorVPN, Trust.Zone, TunnelBear, TunnelBear Addons,
Tunnelr, Unblock VPN, VPN Gate, VPN Unlimited, VPN.ac, VPNBook,
VPNJack, VPNSecure, vpnstaticip.com, VPNTunnel, VyprVPN!, WASEL Pro,
Windscribe VPN
