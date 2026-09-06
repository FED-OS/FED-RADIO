#!/usr/bin/env python3
"""Generate FED-Radio PWA icons (192 / 512) in the Print Edition palette.

Zero third-party: drawn locally with PIL, saved into the repo root next to
manifest.webmanifest. Design: cream paper square, ink masthead word FED-RADIO
as a broadcast-plate monogram, red ON-AIR dot, hairline border.
"""
from PIL import Image, ImageDraw, ImageFont
import os

PAPER = (244, 239, 230)      # --paper
PAPER_DEEP = (231, 223, 204) # --paper-deep
INK = (26, 23, 18)           # --ink
INK_SOFT = (107, 98, 80)     # --ink-soft
HAIRLINE = (201, 191, 169)   # --hairline
RED = (179, 39, 30)          # --accent-red
AMBER = (199, 123, 31)       # --dial-amber

OUT_DIR = os.path.dirname(os.path.abspath(__file__)) + "/.."

def find_font(size):
    """Pick the best available serif; fall back to default bitmap font."""
    candidates = [
        '/usr/share/fonts/truetype/dejavu/DejaVuSerif-Bold.ttf',
        '/usr/share/fonts/truetype/dejavu/DejaVuSerif.ttf',
        '/usr/share/fonts/truetype/liberation/LiberationSerif-Bold.ttf',
        '/usr/share/fonts/truetype/freefont/FreeSerifBold.ttf',
    ]
    for path in candidates:
        if os.path.exists(path):
            return ImageFont.truetype(path, size)
    return ImageFont.load_default()

def make_icon(size):
    img = Image.new('RGB', (size, size), PAPER)
    d = ImageDraw.Draw(img)
    s = size / 512.0  # design scale

    # hairline double border (broadsheet plate)
    d.rectangle([10*s, 10*s, size-10*s, size-10*s], outline=INK, width=max(2, int(3*s)))
    d.rectangle([20*s, 20*s, size-20*s, size-20*s], outline=HAIRLINE, width=max(1, int(2*s)))

    # top rule row: EST. + ON AIR dot
    d.ellipse([size*0.5-9*s, 40*s, size*0.5+9*s, 58*s], fill=RED)

    # masthead FED (red) -RADIO (ink) — auto-fit to the plate
    fed = 'FED'
    radio = '-RADIO'
    max_w = size - 140*s          # keep inside the hairline border
    f_big = find_font(int(150*s))
    w_fed = d.textlength(fed, font=f_big)
    w_radio = d.textlength(radio, font=f_big)
    total = w_fed + w_radio
    if total > max_w:             # shrink font until the wordmark fits
        f_big = find_font(max(1, int(150*s * max_w / total)))
        w_fed = d.textlength(fed, font=f_big)
        w_radio = d.textlength(radio, font=f_big)
        total = w_fed + w_radio
    x0 = (size - total) / 2
    y0 = size*0.28
    d.text((x0, y0), fed, font=f_big, fill=RED)
    d.text((x0 + w_fed, y0), radio, font=f_big, fill=INK)

    # editorial rules
    d.line([70*s, size*0.66, size-70*s, size*0.66], fill=INK, width=max(2, int(3*s)))
    d.line([70*s, size*0.70, size-70*s, size*0.70], fill=HAIRLINE, width=max(1, int(2*s)))

    # frequency plate: 87.5 (amber) MHz (ink soft)
    f_mid = find_font(int(72*s))
    num = '87.5'
    unit = ' MHz'
    w_num = d.textlength(num, font=f_mid)
    w_unit = d.textlength(unit, font=f_mid)
    x1 = (size - (w_num + w_unit)) / 2
    d.text((x1, size*0.745), num, font=f_mid, fill=AMBER)
    d.text((x1 + w_num, size*0.745), unit, font=f_mid, fill=INK_SOFT)

    # kicker
    f_small = find_font(int(30*s))
    kick = 'THE BROADCAST JOURNAL'
    w_kick = d.textlength(kick, font=f_small)
    d.text(((size - w_kick)/2, size*0.90), kick, font=f_small, fill=INK_SOFT)

    return img

if __name__ == '__main__':
    for sz in (192, 512):
        icon = make_icon(sz)
        out = os.path.abspath(os.path.join(OUT_DIR, f'icon-{sz}.png'))
        icon.save(out, 'PNG')
        print('wrote', out, icon.size)
