#!/usr/bin/env python3
"""Deterministically generate the GitHub Pages web twin from the app brain.

Usage (from the repo root):   python3 tools/make_web_twin.py

Reads   app/src/main/assets/index.html   (THE BRAIN)
Writes  index.html                       (web twin at the repo root)

Transformations (exact, idempotent):
  1. Insert Pages/social meta + PWA manifest/icon links after <title>.
  2. Swap AUDIO_BASE from the in-app asset URL to the repo-relative path.
  3. Retitle the header comment block (brain -> web twin of the brain).
  4. Defensively strip any injected third-party <script src="http..."> tags
     so the twin can never drift or carry foreign code.

The twin is never hand-edited: change the brain, re-run this script.
Zero third-party: plain Python 3 standard library only.
"""
import os
import re
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
BRAIN = os.path.join(HERE, '..', 'app', 'src', 'main', 'assets', 'index.html')
TWIN  = os.path.join(HERE, '..', 'index.html')

APP_ASSET_BASE = "var AUDIO_BASE = 'file:///android_asset/audio/';"
WEB_ASSET_BASE = "var AUDIO_BASE = 'app/src/main/assets/audio/';"

META_BLOCK = (
    '    <meta name="description" content="FED-Radio — an in-house, '
    'scratch-built broadcast journal: tech radio on desktop, phone, tablet, '
    'and your car\'s Android Auto dashboard.">\n'
    '    <meta property="og:title" content="FED-Radio">\n'
    '    <meta property="og:description" content="The Broadcast Journal · '
    'In-house tech radio · Zero third-party core.">\n'
    '    <meta property="og:image" content="social-image.png">\n'
    '    <meta name="theme-color" content="#F4EFE6">\n'
    '    <link rel="manifest" href="manifest.webmanifest">\n'
    '    <link rel="icon" type="image/png" sizes="192x192" href="icon-192.png">\n'
)

def main():
    with open(BRAIN, 'r', encoding='utf-8') as f:
        html = f.read()

    # 4. defensively strip injected third-party scripts
    html = re.sub(r'[ \t]*<script src="https?://[^"]*"></script>\n?', '', html)

    # 1. meta + PWA links after <title>...</title>
    html, n_title = re.subn(
        r'(<title>[^<]*</title>)\n',
        r'\1\n' + META_BLOCK,
        html, count=1)
    # 2. asset base swap
    html, n_base = re.subn(
        re.escape(APP_ASSET_BASE),
        WEB_ASSET_BASE.replace('\\', r'\\'),
        html, count=1)
    # 3. header comment retitle
    html, n_hdr = re.subn(
        r'FED-RADIO — THE BRAIN \(PRINT EDITION\)',
        'FED-RADIO — WEB BUILD / THE BROADCAST JOURNAL '
        '(GitHub Pages twin of the app brain)',
        html, count=1)

    with open(TWIN, 'w', encoding='utf-8') as f:
        f.write(html)

    problems = []
    if n_title != 1: problems.append(f'title insertion x{n_title}')
    if n_base != 1:  problems.append(f'AUDIO_BASE swap x{n_base}')
    if n_hdr != 1:   problems.append(f'header retitle x{n_hdr}')
    if 'ninja' in html.lower(): problems.append('injected script survived')
    if 'manifest.webmanifest' not in html: problems.append('manifest link missing')
    if 'keydown' not in html: problems.append('keyboard tuning missing in twin')
    if 'SWITCHING FREQUENCY' not in html: problems.append('auto-advance missing in twin')

    print('twin written:', os.path.relpath(TWIN), f'({len(html)} bytes)')
    if problems:
        print('PROBLEMS:', '; '.join(problems))
        sys.exit(1)
    print('verified: meta+manifest ok · AUDIO_BASE swapped · header retitled · '
          'auto-advance + keyboard present · no third-party scripts')

if __name__ == '__main__':
    main()
