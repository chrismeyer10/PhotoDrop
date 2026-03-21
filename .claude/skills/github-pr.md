---
name: github-pr
description: >
  Erstellt einen GitHub Pull Request für den aktuellen Branch und mergt ihn direkt.
  Verwendet die GitHub REST API via Node.js und den gespeicherten Git-Credential-Token.
  Aufrufen nach git push, wenn ein PR erstellt und gemergt werden soll.
---

# GitHub PR erstellen und mergen

Erstellt automatisch einen PR und mergt ihn via GitHub API.

## Voraussetzungen

- Branch ist bereits gepusht (`git push -u origin <branch>`)
- Git Credentials sind im Windows Credential Manager gespeichert

## Token holen

```bash
git credential fill <<'EOF'
protocol=https
host=github.com
EOF
```

## PR erstellen und mergen

```javascript
// Node.js — in einem Schritt
const https = require('https');

// Token aus git credential fill
const TOKEN = '<token>';
const REPO  = 'chrismeyer10/PhotoDrop';

function apiCall(path, method, body) {
  return new Promise((resolve, reject) => {
    const data = JSON.stringify(body);
    const req = https.request({
      hostname: 'api.github.com',
      path: `/repos/${REPO}${path}`,
      method,
      headers: {
        'Authorization': `token ${TOKEN}`,
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
    head: '<branch>',
    base: 'main',
    body: '<Beschreibung>'
  });
  console.log('PR:', pr.html_url);

  // 2. Mergen
  const merge = await apiCall(`/pulls/${pr.number}/merge`, 'PUT', {
    merge_method: 'squash'
  });
  console.log(merge.merged ? 'Gemergt ✅' : merge.message);
})();
```

## Hinweis

Nach dem Merge: `git checkout main && git pull` ausführen.
