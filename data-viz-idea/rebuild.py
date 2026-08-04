#!/usr/bin/env python3
"""Fix dashboard indices and reorganize cards."""
import re

with open('dashboard.html') as f:
    html = f.read()

# 1. Fix nt.add(r[6]) → nt.add(r[7]) — r[7] is type in 9-col layout
html = html.replace('nt.add(r[6])', 'nt.add(r[7])')

# 2. Reorganize cards block: macro first, then type breakdown
# Find the cards section
pattern = r"(document\.getElementById\('cards'\)\.innerHTML=\s*)(.*?)(\s*// Titles)"
m = re.search(pattern, html, re.DOTALL)
if m:
    new_cards = "document.getElementById('cards').innerHTML=\n" + \
        "    '<div class=\"card\" onclick=\"clearFilter()\"><div class=\"v\">'+filtered.length+'</div><div class=\"l\">Connections</div></div>'+\\n" + \
        "    '<div class=\"card\" onclick=\"clearFilter()\"><div class=\"v\">'+fc.countries.entries().length+'</div><div class=\"l\">Countries</div></div>'+\\n" + \
        "    '<div class=\"card\"><div class=\"v\">'+fc.orgs.entries().length+'</div><div class=\"l\">Providers</div></div>'+\\n" + \
        "    '<div class=\"card\"><div class=\"v\">'+daysSpan()+'d</div><div class=\"l\">Day span</div></div>'+\\n" + \
        "    '<div class=\"card\"><div class=\"v g\">'+vpn+'</div><div class=\"l\">VPN ('+(vpn/total*100).toFixed(1)+'%)</div></div>'+\\n" + \
        "    '<div class=\"card\"><div class=\"v r\">'+wifi+'</div><div class=\"l\">WiFi ('+(wifi/total*100).toFixed(1)+'%)</div></div>'+\\n" + \
        "    '<div class=\"card\"><div class=\"v\">'+cell+'</div><div class=\"l\">Cellular</div></div>'+\\n" + \
        "    '<div class=\"card\"><div class=\"v\">'+fc.ipVersions.get('IPv4')+'</div><div class=\"l\">IPv4</div></div>'+\\n" + \
        "    '<div class=\"card\"><div class=\"v\">'+fc.ipVersions.get('IPv6')+'</div><div class=\"l\">IPv6</div></div>'"
    html = re.sub(pattern, new_cards + r'\3', html, flags=re.DOTALL)
    print('Cards reorganized')

# Verify
checks = {
    'VPN counter nt.add(r[7])': 'nt.add(r[7])' in html,
    'IPv4 counter ip.add(r[6])': 'ip.add(r[6])' in html,
    'Total cards': html.count('<div class=\\"card\\"')
}
for k, v in checks.items():
    print(f'  {k}: {v}')

with open('dashboard.html', 'w') as f:
    f.write(html)
print(f'Wrote {len(html)} bytes')
