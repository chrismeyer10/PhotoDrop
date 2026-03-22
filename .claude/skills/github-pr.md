---
name: github-pr
description: >
  Erstellt einen GitHub Pull Request für den aktuellen Branch und mergt ihn direkt.
  Verwendet die GitHub REST API via Node.js und den gespeicherten Git-Credential-Token.
  Aufrufen nach git push, wenn ein PR erstellt und gemergt werden soll.
---

# GitHub PR erstellen und mergen

Erstellt automatisch einen PR und mergt ihn via GitHub API.
**Kein `gh` CLI nötig** — funktioniert immer über den Windows Credential Manager.

## Schritt 1: Token holen

```bash
git credential fill <<'EOF'
protocol=https
host=github.com
EOF
```
Gibt `username` und `password` (= GitHub Token) aus. Das `password` ist der Token.

## Schritt 2: Branch pushen (falls noch nicht gepusht)

```bash
git push -u origin <branch-name>
```

## Schritt 3: PR erstellen und squash-mergen

```bash
node -e "
const https = require('https');
const TOKEN = '<token-aus-schritt-1>';
const REPO  = 'chrismeyer10/PhotoDrop';

function apiCall(path, method, body) {
  return new Promise((resolve, reject) => {
    const data = JSON.stringify(body);
    const req = https.request({
      hostname: 'api.github.com',
      path: '/repos/' + REPO + path,
      method,
      headers: {
        'Authorization': 'token ' + TOKEN,
        'User-Agent': 'node',
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(data)
      }
    }, res => {
      let out = '';
      res.on('data', d => out += d);
      res.on('end', () => resolve(JSON.parse(out)));
    });
    req.on('error', reject);
    req.write(data);
    req.end();
  });
}

(async () => {
  // 1. PR erstellen
  const pr = await apiCall('/pulls', 'POST', {
    title: '<Titel>',
    head: '<branch-name>',
    base: 'main',
    body: '<Beschreibung>'
  });
  console.log('PR:', pr.html_url, 'Nr:', pr.number);
  if (!pr.number) { console.error(JSON.stringify(pr)); return; }

  // 2. Squash-Mergen
  const merge = await apiCall('/pulls/' + pr.number + '/merge', 'PUT', {
    merge_method: 'squash'
  });
  console.log(merge.merged ? 'Gemergt ✅' : merge.message);
})();
"
```

## Schritt 4: main synchronisieren

```bash
git checkout main && git pull
```

## Fehlerbehandlung

- **422 "A pull request already exists"**: PR-Nummer über `/pulls?head=chrismeyer10:<branch>` abrufen, dann direkt mergen
- **401 Unauthorized**: Token abgelaufen → `git credential reject` + neu einloggen
- **gh auth login schlägt fehl**: Irrelevant — wir nutzen kein `gh` CLI
