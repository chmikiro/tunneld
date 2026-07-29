## Trust boundaries

Tunnel'd relies on three kinds of external visibility:

1. IP info providers.
   These providers can see the request used to determine the public IP and return metadata such as country, ISP, or organization.

2. The Tunnel'd / ipdia.li DNS leak test service.
   This service can observe resolver-related requests that reach the test infrastructure and report them back to the app.

3. The Android networking stack.
   Tunnel'd reports what it can observe and what its configured services can observe. It cannot guarantee that Android, a VPN app, or another app never uses a different resolver path outside the observed test flow.

Because of that:
- Tunnel'd should be used as a measurement tool, not as a guarantee.
- Results are strongest when repeated across different networks, apps, and transition states.
- Users with strict privacy requirements should verify behavior with independent packet capture and resolver-side testing as well.
