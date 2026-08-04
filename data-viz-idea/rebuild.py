#!/usr/bin/env python3
"""Rebuild dashboard from tunneld_dashboard.html with all fixes applied properly."""
import re, json

# 1. Read the working base
with open('tunneld_dashboard.html') as f:
    base = f.read()

# 2. Extract ALL array, add ASN field
m = re.search(r'const ALL = (\[\[.*?\]\]);', base, re.DOTALL)
arr = json.loads(m.group(1))
fixed_arr = []
for row in arr:
    # Insert empty ASN between index 4 (cc) and 5 (type)
    new_row = row[:5] + [""] + row[5:]
    fixed_arr.append(new_row)
arr_json = json.dumps(fixed_arr)
print(f'Fixed {len(fixed_arr)} rows, {len(fixed_arr[0])} cols each')

# 3. Build cleaned HTML
html = '''<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1.0">
<title>Tunnel'd — Interactive VPN Analytics</title>
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<style>
*{margin:0;padding:0;box-sizing:border-box}
body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;background:#0d1117;color:#c9d1d9;padding:20px}
h1{font-size:1.4rem;margin-bottom:4px}
.meta{color:#8b949e;font-size:.78rem;margin-bottom:16px}
.breadcrumb{display:flex;align-items:center;gap:6px;margin-bottom:14px;flex-wrap:wrap;font-size:.82rem}
.breadcrumb span{color:#58a6ff;cursor:pointer}
.breadcrumb span:hover{text-decoration:underline}
.breadcrumb .sep{color:#484f58;cursor:default}
.breadcrumb .clear{color:#f85149;cursor:pointer;margin-left:8px;font-size:.75rem}
.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(130px,1fr));gap:8px;margin-bottom:16px}
.card{background:#161b22;border:1px solid#30363d;border-radius:8px;padding:12px;text-align:center;cursor:pointer;transition:border-color .15s}
.card:hover{border-color:#58a6ff}
.card .v{font-size:1.6rem;font-weight:700;color:#58a6ff}
.card .v.g{color:#3fb950}.card .v.r{color:#f85149}
.card .l{font-size:.68rem;color:#8b949e;text-transform:uppercase;letter-spacing:.04em;margin-top:2px}
.row{display:grid;grid-template-columns:1fr 1fr;gap:14px;margin-bottom:14px}
@media(max-width:768px){.row{grid-template-columns:1fr}}
.box{background:#161b22;border:1px solid#30363d;border-radius:8px;padding:14px}
.box h2{font-size:.9rem;color:#8b949e;margin-bottom:10px;font-weight:500}
canvas{max-height:280px;cursor:pointer}
.info{font-size:.78rem;color:#8b949e;margin-top:4px;text-align:center;font-style:italic}
input{width:100%;padding:8px 12px;background:#161b22;border:1px solid#30363d;border-radius:6px;color:#c9d1d9;font-size:.8rem;margin-bottom:8px}
input:focus{outline:none;border-color:#58a6ff}
.tbl{max-height:45vh;overflow:auto;border:1px solid#30363d;border-radius:6px}
.tbl table{min-width:650px}
table{width:100%;border-collapse:collapse;font-size:.76rem}
th{position:sticky;top:0;background:#1c2128;color:#8b949e;padding:7px 10px;text-align:left;font-weight:500;border-bottom:2px solid#30363d}
th:hover{color:#f0f6fc}
td{padding:5px 10px;border-bottom:1px solid#21262d}
tr:hover{background:#161b22}
</style>
</head>
<body>
<h1>🔐 Tunnel'd — Interactive VPN Analytics</h1>
<div class="meta" id="metaLine"></div>

<input type="file" id="fileInput" accept=".csv" style="display:none" onchange="loadFile(this.files[0])">
<button id="uploadBtn" onclick="document.getElementById('fileInput').click()" style="background:#161b22;border:1px solid #30363d;color:#8b949e;padding:8px 16px;border-radius:6px;cursor:pointer;font-size:.78rem;margin-bottom:12px">📁 Load CSV export</button>

<div class="breadcrumb" id="bc"><span onclick="clearFilter()">🌐 All</span></div>
<div class="grid" id="cards"></div>
<div class="row">
  <div class="box"><h2 id="ctitle">Top Countries</h2><canvas id="countryChart"></canvas><div class="info">Click a bar to drill down</div></div>
  <div class="box"><h2 id="dtitle">Daily new IP addresses</h2><canvas id="dailyChart"></canvas><div class="info">Click a point to see that day</div></div>
</div>
<div class="row">
  <div class="box"><h2 id="otitle">Top Providers</h2><canvas id="orgChart"></canvas><div class="info">Click a bar to drill down</div></div>
  <div class="box"><h2>Network type overview</h2><canvas id="typeChart" style="max-height:220px"></canvas><div class="info" id="typeInfo"></div></div>
</div>
<div class="row">
  <div class="box" style="grid-column:1/-1"><h2>Details</h2><input id="filt" placeholder="Filter IP, country, provider, city, ASN..." oninput="renderTable()"><div class="tbl"><table><thead><tr><th>IP</th><th>Country</th><th>Provider</th><th>City</th><th>ASN</th><th>Type</th><th>Time</th></tr></thead><tbody id="tb"></tbody></table></div></div>
</div>
<script>
const ALL = ''' + arr_json + ''';

// ── CSV parser ──
function parseCSV(text){
  const lines=text.trim().split(/\\r?\\n/);
  if(lines.length<2)return[];
  let start=lines[0].startsWith('address')?1:0;
  const rows=[];
  for(let i=start;i<lines.length;i++){
    const f=parseLine(lines[i]);
    if(!f||f.length<7)continue;
    // 0=address,1=version,2=network_type,3=country,4=cc,5=city,6=isp,7=org,8=asn,9=tz,10=lat,11=lon,12=ts
    rows.push([
      f[0]||'',
      f[3]||'Unknown',
      f[6]||f[7]||f[5]||'Unknown',
      f[5]||'',
      f[4]||'',
      f[2]||'Unknown',
      f[8]||'',
      f[12]||''
    ]);
  }
  return rows;
}
function parseLine(line){
  const f=[];let c='',q=false;
  for(let ch of line){
    if(ch==='"'&&!q){q=true;continue}
    if(ch==='"'&&q){q=false;continue}
    if(ch===','&&!q){f.push(c);c='';continue}
    c+=ch;
  }
  f.push(c);return f;
}

// ── File loading ──
function loadFile(file){
  if(!file)return;
  const r=new FileReader();
  r.onload=function(e){
    const rows=parseCSV(e.target.result);
    if(!rows.length){document.getElementById('metaLine').textContent='⚠ No valid rows found';return}
    ALL=rows;clearFilter();
    document.getElementById('uploadBtn').style.display='none';
  };
  r.readAsText(file);
}

// WebView JS bridge
window.loadTunneldCSV=function(csv){
  const rows=parseCSV(csv);
  if(!rows.length)return;
  ALL=rows;clearFilter();
  document.getElementById('uploadBtn').style.display='none';
};

// ── Filtering ──
let filter={type:null,value:null};
let filtered=ALL;

function applyFilter(){
  if(!filter.type){filtered=ALL;return}
  filtered=ALL.filter(r=>{
    if(filter.type=='country')return r[1]===filter.value;
    if(filter.type=='org')return r[2]===filter.value;
    if(filter.type=='day')return(r[7]||'').startsWith(filter.value);
    return true;
  });
}

function clearFilter(){filter={type:null,value:null};applyFilter();updateAll()}
function setFilter(t,v){
  filter={type:t,value:v};applyFilter();updateAll();
  document.getElementById('bc').innerHTML='<span onclick="clearFilter()">🌐 All</span> <span class="sep">→</span> <span>'+v+'</span> <span class="clear" onclick="clearFilter()">✕ clear</span>';
}

// ── Counters ──
function Counter(){let m=new Map();return{add:function(k){m.set(k,(m.get(k)||0)+1)},entries:function(){return[...m.entries()]},get:function(k){return m.get(k)||0},mostCommon:function(n){return[...m.entries()].sort((a,b)=>b[1]-a[1]).slice(0,n)}}}

function filteredCounts(){
  let c=Counter(),o=Counter(),d=Counter(),nt=Counter();
  filtered.forEach(r=>{
    c.add(r[1]);o.add(r[2]);nt.add(r[5]);
    let dd=(r[7]||'').slice(0,10);if(dd)d.add(dd);
  });
  return {countries:c,orgs:o,daily:d,networkTypes:nt};
}

function daysSpan(){
  let dates=ALL.map(r=>(r[7]||'').slice(0,10)).filter(d=>d).sort();
  if(!dates.length)return 0;
  return Math.round((new Date(dates[dates.length-1])-new Date(dates[0]))/86400000);
}

// ── Charts ──
let cChart=null,dChart=null,oChart=null,tChart=null;

function updateAll(){
  let fc=filteredCounts();
  let loaded=document.getElementById('uploadBtn').style.display==='none'||ALL.length>0;

  // Meta
  document.getElementById('metaLine').textContent=filter.type
    ?'Filtered: '+filtered.length+' / '+ALL.length+' connections'
    :loaded
      ?ALL.length+' connections · '+fc.countries.entries().length+' countries · '+fc.orgs.entries().length+' providers · '+daysSpan()+' day span'
      :'Demo data loaded — use 📁 Load CSV to analyze your own';

  // Country chart
  let topC=fc.countries.mostCommon(filter.type?10:15);
  let clabels=topC.map(c=>c[0]),cdata=topC.map(c=>c[1]);
  let pctFn=filtered.length||1;
  if(cChart)cChart.destroy();
  cChart=new Chart(document.getElementById('countryChart'),{type:'bar',data:{labels:clabels,
    datasets:[{data:cdata,backgroundColor:clabels.map((_,i)=>'hsl('+(200+i*22)+',30%,35%)'),borderRadius:3}]},
    options:{indexAxis:'y',responsive:true,plugins:{legend:{display:false},tooltip:{callbacks:{label:function(ctx){return ctx.raw+' ('+(ctx.raw/pctFn*100).toFixed(1)+'%)'}}}},
    onClick:function(evt,els){if(els.length&&filter.type!=='country'){let idx=els[0].index;setFilter('country',clabels[idx])}},
    scales:{x:{grid:{color:'#21262d'},ticks:{color:'#8b949e'}},y:{grid:{display:false},ticks:{color:'#8b949e',font:{size:10}}}}}});

  // Daily chart
  let dall=fc.daily.entries().sort((a,b)=>a[0].localeCompare(b[0]));
  let dlabs=dall.map(d=>d[0].slice(5)),ddat=dall.map(d=>d[1]);
  if(dChart)dChart.destroy();
  dChart=new Chart(document.getElementById('dailyChart'),{type:'line',data:{labels:dlabs,
    datasets:[{data:ddat,borderColor:'#58a6ff',backgroundColor:'#58a6ff33',fill:true,borderWidth:1.5,pointRadius:ddat.length>30?2:3,tension:.3}]},
    options:{responsive:true,plugins:{legend:{display:false}},
    onClick:function(evt,els){if(els.length&&filter.type!=='day'){let idx=els[0].index;setFilter('day',dall[idx][0])}},
    scales:{x:{grid:{color:'#21262d'},ticks:{color:'#8b949e',maxTicksLimit:25,font:{size:8}}},y:{grid:{color:'#21262d'},ticks:{color:'#8b949e'}}}}}); 

  // Org chart
  let topO=fc.orgs.mostCommon(filter.type?8:10);
  let olabs=topO.map(o=>o[0]),odat=topO.map(o=>o[1]);
  let oColors=['#3fb950','#58a6ff','#a371f7','#d2991d','#f85149','#7ee787','#db6d28','#8b949e','#f778ba','#56d4dd','#e55354','#4da6ff'];
  if(oChart)oChart.destroy();
  oChart=new Chart(document.getElementById('orgChart'),{type:'bar',data:{labels:olabs,
    datasets:[{data:odat,backgroundColor:olabs.map((_,i)=>oColors[i%oColors.length]),borderRadius:3}]},
    options:{indexAxis:'y',responsive:true,plugins:{legend:{display:false},tooltip:{callbacks:{label:function(ctx){return ctx.raw+' ('+(ctx.raw/pctFn*100).toFixed(1)+'%)'}}}},
    onClick:function(evt,els){if(els.length&&filter.type!=='org'){let idx=els[0].index;setFilter('org',olabs[idx])}},
    scales:{x:{grid:{color:'#21262d'},ticks:{color:'#8b949e'}},y:{grid:{display:false},ticks:{color:'#8b949e',font:{size:10}}}}}});

  // Network type donut
  let vpn=fc.networkTypes.get('VPN')||0,wifi=fc.networkTypes.get('WiFi')||0;
  let total=vpn+wifi||1;
  if(tChart)tChart.destroy();
  tChart=new Chart(document.getElementById('typeChart'),{type:'doughnut',data:{
    labels:['VPN','WiFi'],
    datasets:[{data:[vpn,wifi],backgroundColor:['#3fb950','#58a6ff'],borderColor:'#161b22',borderWidth:2}]},
    options:{responsive:true,cutout:'60%',plugins:{legend:{position:'bottom',labels:{color:'#8b949e',padding:16,font:{size:11}}}}}});
  document.getElementById('typeInfo').textContent='VPN: '+vpn+' ('+(vpn/total*100).toFixed(1)+'%) · WiFi: '+wifi+' ('+(wifi/total*100).toFixed(1)+'%)';

  // Cards
  document.getElementById('cards').innerHTML=
    '<div class="card" onclick="clearFilter()"><div class="v">'+fc.countries.entries().length+'</div><div class="l">Countries</div></div>'+
    '<div class="card"><div class="v">'+fc.orgs.entries().length+'</div><div class="l">Providers</div></div>'+
    '<div class="card"><div class="v g">'+vpn+'</div><div class="l">VPN ('+(vpn/total*100).toFixed(1)+'%)</div></div>'+
    '<div class="card"><div class="v r">'+wifi+'</div><div class="l">WiFi ('+(wifi/total*100).toFixed(1)+'%)</div></div>'+
    '<div class="card"><div class="v">'+daysSpan()+'d</div><div class="l">Day span</div></div>'+
    '<div class="card"><div class="v">'+filtered.length+'</div><div class="l">Connections</div></div>';

  // Titles
  document.getElementById('ctitle').textContent=filter.type==='country'?'Countries → '+filter.value:filter.type==='org'?'Provider: '+filter.value+' — Countries':'Top Countries';
  document.getElementById('otitle').textContent=filter.type==='org'?'Providers → '+filter.value:filter.type==='country'?'Country: '+filter.value+' — Providers':'Top Providers';
  document.getElementById('dtitle').textContent=filter.type==='day'?'Day: '+filter.value:'Daily new IP addresses';

  renderTable();
}

function renderTable(){
  let q=document.getElementById('filt').value.toLowerCase();
  let d=q?filtered.filter(r=>r.some(c=>String(c).toLowerCase().includes(q))):filtered;
  document.getElementById('tb').innerHTML=d.slice(0,300).map(r=>
    '<tr><td style="font-family:monospace;font-size:.72rem">'+r[0]+'</td><td>'+r[1]+'</td><td>'+r[2]+'</td><td>'+r[3]+'</td><td style="font-size:.7rem;color:#8b949e">'+(r[6]||'')+'</td><td style="color:'+(r[5]=='VPN'?'#3fb950':'#58a6ff')+'">'+r[5]+'</td><td style="font-size:.7rem;color:#8b949e">'+(r[7]||'').replace('T',' ')+'</td></tr>'
  ).join('');
}

updateAll();
</script>
</body></html>'''

with open('dashboard.html', 'w') as f:
    f.write(html)

print(f'Wrote {len(html)} bytes')
