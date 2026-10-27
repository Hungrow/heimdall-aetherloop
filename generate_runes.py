import os
import math
from PIL import Image, ImageDraw, ImageFilter

OUTPUT_DIR = r"app\src\main\res\drawable"
os.makedirs(OUTPUT_DIR, exist_ok=True)

def create_radial_glow(size, color, max_alpha=180):
    img = Image.new("RGBA", size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = size[0] // 2, size[1] // 2
    r_max = min(cx, cy)
    for r in range(r_max, 0, -2):
        alpha = int(max_alpha * (1.0 - (r / r_max)) ** 1.8)
        c = (color[0], color[1], color[2], alpha)
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=c)
    return img

def generate_rune_stone(filename, rune_char, primary_color, symbol_drawer):
    W, H = 64, 64
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Glow
    glow = create_radial_glow((W, H), primary_color, max_alpha=140)
    img.paste(glow, (0, 0), glow)

    # Carved Stone Tablet (Hexagonal / Beveled Rune)
    stone_pts = [(16, 8), (48, 8), (56, 32), (48, 56), (16, 56), (8, 32)]
    draw.polygon(stone_pts, fill=(28, 36, 48, 255), outline=primary_color, width=2)
    # Inner border
    inner_pts = [(18, 12), (46, 12), (52, 32), (46, 52), (18, 52), (12, 32)]
    draw.polygon(inner_pts, outline=(primary_color[0], primary_color[1], primary_color[2], 120), width=1)

    # Draw the specific glyph
    symbol_drawer(draw, 32, 32, primary_color)

    img.save(os.path.join(OUTPUT_DIR, filename))
    print(f"Generated {filename}")

def draw_mjolnir(draw, cx, cy, col):
    # Thor's Hammer
    draw.line([(cx, cy + 12), (cx, cy - 2)], fill=(255, 255, 255), width=3) # Shaft
    draw.polygon([(cx - 10, cy - 10), (cx + 10, cy - 10), (cx + 8, cy - 2), (cx - 8, cy - 2)], fill=(255, 255, 255))
    # Lightning spark
    draw.line([(cx - 2, cy - 12), (cx + 2, cy - 8)], fill=col, width=2)

def draw_multiball(draw, cx, cy, col):
    # 3 radiant spheres in triangle
    for ox, oy in [(-7, 5), (7, 5), (0, -7)]:
        draw.ellipse([cx + ox - 4, cy + oy - 4, cx + ox + 4, cy + oy + 4], fill=(255, 255, 255), outline=col)

def draw_fire(draw, cx, cy, col):
    # Flame tongue
    flame_pts = [(cx, cy - 12), (cx + 8, cy + 4), (cx + 4, cy + 10), (cx - 4, cy + 10), (cx - 8, cy + 4)]
    draw.polygon(flame_pts, fill=(255, 200, 50), outline=(255, 80, 0))
    draw.polygon([(cx, cy - 6), (cx + 4, cy + 4), (cx - 4, cy + 4)], fill=(255, 255, 255))

def draw_ballistas(draw, cx, cy, col):
    # Crossbow / Ballista bolts
    draw.line([(cx - 6, cy + 8), (cx - 6, cy - 8)], fill=(255, 255, 255), width=2)
    draw.line([(cx + 6, cy + 8), (cx + 6, cy - 8)], fill=(255, 255, 255), width=2)
    draw.line([(cx - 10, cy - 2), (cx - 2, cy - 2)], fill=col, width=2)
    draw.line([(cx + 2, cy - 2), (cx + 10, cy - 2)], fill=col, width=2)

def draw_shield(draw, cx, cy, col):
    # Expanded Aegis Shield
    draw.arc([cx - 10, cy - 8, cx + 10, cy + 10], start=0, end=180, fill=(255, 255, 255), width=3)
    draw.line([(cx - 10, cy - 4), (cx + 10, cy - 4)], fill=(255, 255, 255), width=3)

def draw_valkyrie(draw, cx, cy, col):
    # Valkyrie Heart / Wings
    draw.ellipse([cx - 8, cy - 6, cx, cy + 2], fill=(255, 60, 100))
    draw.ellipse([cx, cy - 6, cx + 8, cy + 2], fill=(255, 60, 100))
    draw.polygon([(cx - 8, cy - 2), (cx + 8, cy - 2), (cx, cy + 8)], fill=(255, 60, 100))

if __name__ == "__main__":
    generate_rune_stone("rune_mjolnir.png", "ᚦ", (255, 215, 0), draw_mjolnir)
    generate_rune_stone("rune_multiball.png", "ᛟ", (0, 229, 255), draw_multiball)
    generate_rune_stone("rune_fire.png", "ᚲ", (255, 87, 34), draw_fire)
    generate_rune_stone("rune_ballistas.png", "ᛏ", (213, 0, 249), draw_ballistas)
    generate_rune_stone("rune_shield.png", "ᛉ", (0, 230, 118), draw_shield)
    generate_rune_stone("rune_valkyrie.png", "ᛒ", (255, 64, 129), draw_valkyrie)
    print("ALL RUNES GENERATED!")
