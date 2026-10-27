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

# -------------------------------------------------------------
# 1. SCOUT SHIP (KARVE) - Light oak, red/white sail, dragon prow
# -------------------------------------------------------------
def generate_ship_scout():
    W, H = 240, 160
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Water reflection / shadow
    draw.ellipse([30, H - 35, W - 30, H - 10], fill=(5, 20, 35, 90))

    # Hull shape (curved Viking longboat)
    hull_pts = [
        (25, 110), (15, 90), (12, 72), (20, 68), (28, 76), # Dragon Bow
        (45, 105), (120, 115), (195, 105),                  # Bottom keel
        (212, 76), (220, 68), (228, 72), (225, 90), (215, 110), # Dragon Stern
        (190, 126), (120, 132), (50, 126)                   # Lower curve
    ]
    draw.polygon(hull_pts, fill=(139, 90, 43, 255), outline=(78, 48, 20, 255))

    # Inner hull shading / plank lines
    for i in range(3):
        y_offset = 112 + i * 6
        draw.arc([35, y_offset - 10, W - 35, y_offset + 10], start=10, end=170, fill=(101, 62, 28, 220), width=2)

    # Gold Dragon Eye & teeth at prow
    draw.ellipse([14, 73, 19, 78], fill=(255, 215, 0, 255), outline=(50, 25, 0, 255))
    draw.ellipse([221, 73, 226, 78], fill=(255, 215, 0, 255), outline=(50, 25, 0, 255))

    # Mast & Rigging
    draw.line([(120, 30), (120, 115)], fill=(70, 42, 18, 255), width=5)
    draw.line([(60, 42), (180, 42)], fill=(90, 55, 25, 255), width=4) # Yardarm
    draw.line([(60, 42), (35, 110)], fill=(180, 160, 130, 180), width=1) # Ropes
    draw.line([(180, 42), (205, 110)], fill=(180, 160, 130, 180), width=1)

    # Striped Red & White Viking Sail (billowing trapezoid)
    sail_pts = [(62, 44), (178, 44), (186, 92), (120, 98), (54, 92)]
    draw.polygon(sail_pts, fill=(245, 245, 245, 255), outline=(90, 50, 25, 255))
    
    # Red Stripes
    for stripe_x in [75, 105, 135, 165]:
        stripe_pts = [
            (stripe_x, 44), (stripe_x + 14, 44),
            (stripe_x + 16 + (stripe_x - 120) * 0.08, 93),
            (stripe_x + 2 + (stripe_x - 120) * 0.08, 93)
        ]
        draw.polygon(stripe_pts, fill=(210, 35, 35, 255))

    # Gunwale Round Shields (4 shields on side)
    shield_colors = [(200, 30, 30), (255, 215, 0), (200, 30, 30), (255, 215, 0)]
    for idx, sx in enumerate([65, 100, 140, 175]):
        sc = shield_colors[idx]
        draw.ellipse([sx - 10, 104, sx + 10, 124], fill=sc, outline=(40, 40, 40, 255), width=2)
        # Shield Boss (center metal dome)
        draw.ellipse([sx - 3, 111, sx + 3, 117], fill=(220, 220, 220, 255), outline=(50, 50, 50, 255))

    img.save(os.path.join(OUTPUT_DIR, "ship_scout.png"))
    print("Generated ship_scout.png")

# -------------------------------------------------------------
# 2. ARMORED DRAKKAR - Dark bog oak, dragon prow, blue/gold sail
# -------------------------------------------------------------
def generate_ship_drakkar():
    W, H = 260, 170
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Shadow
    draw.ellipse([30, H - 32, W - 30, H - 8], fill=(3, 15, 28, 120))

    # Heavy Dark Hull with Iron Plating
    hull_pts = [
        (28, 118), (14, 94), (10, 68), (22, 60), (32, 70), (40, 85), # Menacing Dragon Head
        (60, 112), (130, 124), (200, 112),
        (220, 85), (228, 70), (238, 60), (250, 68), (246, 94), (232, 118), # Tail
        (205, 138), (130, 145), (55, 138)
    ]
    draw.polygon(hull_pts, fill=(55, 40, 32, 255), outline=(25, 18, 14, 255), width=2)

    # Golden carvings along gunwale
    draw.line([(55, 112), (205, 112)], fill=(212, 175, 55, 255), width=3)
    draw.line([(60, 120), (200, 120)], fill=(75, 58, 48, 255), width=2)

    # Dragon Eyes glowing gold
    draw.ellipse([16, 66, 22, 72], fill=(255, 225, 50, 255))
    draw.ellipse([238, 66, 244, 72], fill=(255, 225, 50, 255))

    # Mast & Heavy Yardarm
    draw.line([(130, 25), (130, 120)], fill=(45, 30, 22, 255), width=6)
    draw.line([(65, 38), (195, 38)], fill=(60, 42, 30, 255), width=5)
    draw.line([(65, 38), (35, 116)], fill=(150, 140, 120, 200), width=1)
    draw.line([(195, 38), (225, 116)], fill=(150, 140, 120, 200), width=1)

    # Blue & Gold Royal Asgardian Sail with Raven Symbol
    sail_pts = [(68, 40), (192, 40), (202, 96), (130, 104), (58, 96)]
    draw.polygon(sail_pts, fill=(20, 55, 120, 255), outline=(15, 35, 75, 255), width=2)

    # Gold Chevron Stripes
    for sx in [85, 115, 145, 175]:
        draw.line([(sx, 42), (sx + (sx - 130) * 0.15, 98)], fill=(255, 215, 0, 255), width=6)

    # Raven silhouette in center
    draw.polygon([(130, 58), (118, 74), (124, 76), (130, 84), (136, 76), (142, 74)], fill=(10, 15, 30, 255))

    # Overlapping Armored Shields (6 shields)
    shield_x = [62, 88, 114, 144, 170, 196]
    for i, sx in enumerate(shield_x):
        color = (20, 80, 180) if i % 2 == 0 else (212, 175, 55)
        draw.ellipse([sx - 11, 108, sx + 11, 130], fill=color, outline=(30, 30, 30, 255), width=2)
        # Metal boss with studs
        draw.ellipse([sx - 4, 115, sx + 4, 123], fill=(210, 210, 215, 255), outline=(40, 40, 40, 255))

    img.save(os.path.join(OUTPUT_DIR, "ship_drakkar.png"))
    print("Generated ship_drakkar.png")

# -------------------------------------------------------------
# 3. FROST BRIG - Niflheim Glacial Longship (ice hull, cyan runes)
# -------------------------------------------------------------
def generate_ship_frost():
    W, H = 260, 170
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Cyan Frost Glow underneath
    glow = create_radial_glow((W, 80), (0, 229, 255), max_alpha=110)
    img.paste(glow, (0, H - 70), glow)

    # Glacial Blue Frosted Hull with Icicles
    hull_pts = [
        (26, 116), (12, 90), (8, 64), (20, 58), (30, 68), (38, 85),
        (58, 110), (130, 122), (202, 110),
        (222, 85), (230, 68), (240, 58), (252, 64), (248, 90), (234, 116),
        (208, 136), (130, 144), (52, 136)
    ]
    draw.polygon(hull_pts, fill=(50, 95, 135, 255), outline=(180, 240, 255, 255), width=2)

    # Crystalline Frost Spikes along keel
    for sp_x in range(60, 201, 20):
        draw.polygon([(sp_x, 130), (sp_x + 8, 142), (sp_x + 16, 130)], fill=(200, 245, 255, 240))

    # Luminous Cyan Hull Runes
    draw.line([(55, 110), (205, 110)], fill=(0, 229, 255, 255), width=3)

    # Frosted Mast
    draw.line([(130, 25), (130, 118)], fill=(130, 180, 210, 255), width=6)
    draw.line([(65, 38), (195, 38)], fill=(160, 210, 235, 255), width=5)

    # Glacial Frost Sail with Ice Crystals
    sail_pts = [(68, 40), (192, 40), (202, 96), (130, 104), (58, 96)]
    draw.polygon(sail_pts, fill=(15, 45, 80, 255), outline=(0, 229, 255, 255), width=2)
    
    # Cyan Ice Rune Striations
    for sx in [85, 110, 130, 150, 175]:
        draw.line([(sx, 42), (sx + (sx - 130) * 0.12, 98)], fill=(0, 229, 255, 220), width=4)
    # Giant Ice Snowflake / Algiz Rune (ᛉ) in center
    draw.line([(130, 55), (130, 85)], fill=(220, 250, 255, 255), width=3)
    draw.line([(130, 65), (118, 55)], fill=(220, 250, 255, 255), width=3)
    draw.line([(130, 65), (142, 55)], fill=(220, 250, 255, 255), width=3)

    # Frosted Silver & Cyan Shields
    shield_x = [64, 90, 116, 144, 170, 196]
    for sx in shield_x:
        draw.ellipse([sx - 10, 106, sx + 10, 126], fill=(40, 110, 160), outline=(200, 245, 255, 255), width=2)
        draw.ellipse([sx - 3, 113, sx + 3, 119], fill=(0, 229, 255, 255))

    img.save(os.path.join(OUTPUT_DIR, "ship_frost.png"))
    print("Generated ship_frost.png")

# -------------------------------------------------------------
# 4. FIRE DREADNOUGHT - Muspelheim Burning Battleship
# -------------------------------------------------------------
def generate_ship_fire():
    W, H = 270, 175
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Fire glow underneath
    glow = create_radial_glow((W, 80), (255, 87, 34), max_alpha=120)
    img.paste(glow, (0, H - 70), glow)

    # Black Charred Ironwood Hull with Molten Veins
    hull_pts = [
        (26, 118), (12, 92), (8, 62), (22, 55), (32, 65), (42, 85), # Horned Beast Prow
        (62, 112), (135, 126), (208, 112),
        (228, 85), (238, 65), (248, 55), (262, 62), (258, 92), (244, 118),
        (214, 140), (135, 148), (56, 140)
    ]
    draw.polygon(hull_pts, fill=(28, 22, 22, 255), outline=(255, 87, 34, 255), width=2)

    # Molten Lava Fissure line
    draw.line([(58, 112), (212, 112)], fill=(255, 152, 0, 255), width=3)
    draw.line([(62, 122), (208, 122)], fill=(255, 61, 0, 255), width=2)

    # Glowing Ember Eyes
    draw.ellipse([16, 62, 22, 68], fill=(255, 235, 59, 255))
    draw.ellipse([248, 62, 254, 68], fill=(255, 235, 59, 255))

    # Obsidian Mast with Iron Bands
    draw.line([(135, 25), (135, 120)], fill=(35, 25, 25, 255), width=7)
    draw.line([(68, 38), (202, 38)], fill=(50, 35, 30, 255), width=5)

    # Fiery Burning Sail (gradient effect)
    sail_pts = [(70, 40), (200, 40), (210, 96), (135, 106), (60, 96)]
    draw.polygon(sail_pts, fill=(60, 15, 10, 255), outline=(255, 110, 0, 255), width=2)

    # Flame tongues / Magma Runes on sail
    for flame_x in [90, 120, 150, 180]:
        flame_pts = [
            (flame_x - 12, 98), (flame_x, 48), (flame_x + 12, 98), (flame_x, 92)
        ]
        draw.polygon(flame_pts, fill=(255, 87, 34, 230))
        # Inner yellow hot core
        inner_pts = [
            (flame_x - 5, 96), (flame_x, 60), (flame_x + 5, 96), (flame_x, 92)
        ]
        draw.polygon(inner_pts, fill=(255, 235, 59, 255))

    # Spiked Bronze Shields
    shield_x = [66, 94, 122, 148, 176, 204]
    for sx in shield_x:
        draw.ellipse([sx - 10, 108, sx + 10, 128], fill=(160, 40, 20), outline=(255, 160, 0, 255), width=2)
        draw.ellipse([sx - 3, 115, sx + 3, 121], fill=(255, 235, 59, 255))

    img.save(os.path.join(OUTPUT_DIR, "ship_fire.png"))
    print("Generated ship_fire.png")

# -------------------------------------------------------------
# 5. NAGLFAR BOSS - The Colossal Ship of the Dead (Bones & Nails)
# -------------------------------------------------------------
def generate_ship_naglfar():
    W, H = 380, 220
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Spectral Green / Violet Underglow
    glow = create_radial_glow((W, 110), (0, 230, 118), max_alpha=120)
    img.paste(glow, (0, H - 90), glow)

    # Massive Skeletal Bone & Dark Wood Hull
    hull_pts = [
        (35, 145), (18, 115), (10, 75), (28, 62), (45, 78), (60, 105), # Colossal Ribbed Prow
        (85, 138), (190, 155), (295, 138),
        (320, 105), (335, 78), (352, 62), (370, 75), (362, 115), (345, 145),
        (300, 175), (190, 185), (80, 175)
    ]
    draw.polygon(hull_pts, fill=(35, 30, 42, 255), outline=(130, 220, 170, 255), width=3)

    # Skeletal Rib Cage along the hull
    for rx in range(85, 296, 22):
        draw.arc([rx - 10, 135, rx + 10, 172], start=20, end=160, fill=(225, 235, 225, 255), width=3)

    # Green Spirit Lanterns on Prow and Stern
    draw.ellipse([22, 70, 34, 82], fill=(0, 255, 136, 255))
    draw.ellipse([346, 70, 358, 82], fill=(0, 255, 136, 255))

    # 3 Masts (Dreadnought Flagship)
    masts = [(120, 30, 135), (190, 18, 150), (260, 30, 135)]
    for mx, my_top, my_bot in masts:
        draw.line([(mx, my_top), (mx, my_bot)], fill=(45, 40, 52, 255), width=7)
        draw.line([(mx - 35, my_top + 16), (mx + 35, my_top + 16)], fill=(65, 58, 75, 255), width=5)

    # 3 Tattered Dark Emerald / Violet Sails with Ghost Runes
    sails = [
        (85, 46, 155, 46, 162, 105, 120, 112, 78, 105),
        (150, 34, 230, 34, 238, 112, 190, 120, 142, 112),
        (225, 46, 295, 46, 302, 105, 260, 112, 218, 105)
    ]
    for pts in sails:
        p_list = [(pts[0], pts[1]), (pts[2], pts[3]), (pts[4], pts[5]), (pts[6], pts[7]), (pts[8], pts[9])]
        draw.polygon(p_list, fill=(25, 35, 35, 255), outline=(0, 230, 118, 255), width=2)

    # Giant Skull / Helm of Awe (Ægishjálmr) on central sail
    cx, cy = 190, 75
    draw.ellipse([cx - 15, cy - 14, cx + 15, cy + 14], fill=(0, 255, 136, 220), outline=(200, 255, 220, 255), width=2)
    # Eye sockets
    draw.ellipse([cx - 8, cy - 4, cx - 2, cy + 4], fill=(20, 30, 30, 255))
    draw.ellipse([cx + 2, cy - 4, cx + 8, cy + 4], fill=(20, 30, 30, 255))

    # Tattered Sail Rips
    for rx in [100, 135, 175, 205, 245, 275]:
        draw.line([(rx, 95), (rx + 4, 112)], fill=(0, 0, 0, 0), width=3)

    img.save(os.path.join(OUTPUT_DIR, "ship_naglfar.png"))
    print("Generated ship_naglfar.png")

# -------------------------------------------------------------
# 6. ASGARDIAN PADDLE FLAGSHIP (The Player's Warship Paddle)
# -------------------------------------------------------------
def generate_paddle_flagship():
    W, H = 280, 75
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Golden Bifröst Glow
    glow = create_radial_glow((W, 60), (0, 229, 255), max_alpha=120)
    img.paste(glow, (0, 10), glow)

    # Majestic Asgardian Ship Hull (Rounded sleek battleship)
    hull_pts = [
        (25, 25), (10, 15), (6, 5), (18, 2), (28, 12),     # Left Golden Dragon Prow
        (50, 20), (140, 22), (230, 20),
        (252, 12), (262, 2), (274, 5), (270, 15), (255, 25), # Right Prow
        (240, 48), (140, 56), (40, 48)                      # Bottom keel
    ]
    draw.polygon(hull_pts, fill=(18, 45, 75, 255), outline=(255, 215, 0, 255), width=3)

    # Gold Carved Inlays along deck
    draw.line([(40, 22), (240, 22)], fill=(255, 215, 0, 255), width=4)
    draw.line([(45, 30), (235, 30)], fill=(0, 229, 255, 255), width=2) # Bifrost conduit

    # Center Valkyrie Shield Emblem
    cx, cy = 140, 32
    draw.ellipse([cx - 16, cy - 16, cx + 16, cy + 16], fill=(10, 25, 45, 255), outline=(255, 215, 0, 255), width=3)
    draw.ellipse([cx - 8, cy - 8, cx + 8, cy + 8], fill=(0, 229, 255, 255))
    # Nordic Rune ᚱ (Rad)
    draw.line([(cx - 3, cy - 6), (cx - 3, cy + 6)], fill=(255, 255, 255, 255), width=2)
    draw.line([(cx - 3, cy - 6), (cx + 3, cy - 2)], fill=(255, 255, 255, 255), width=2)
    draw.line([(cx + 3, cy - 2), (cx - 3, cy + 1)], fill=(255, 255, 255, 255), width=2)
    draw.line([(cx - 3, cy + 1), (cx + 3, cy + 6)], fill=(255, 255, 255, 255), width=2)

    # 8 Golden Boss Shields along the paddle gunwale
    for sx in [52, 75, 98, 118, 162, 182, 205, 228]:
        draw.ellipse([sx - 8, 25, sx + 8, 41], fill=(212, 175, 55, 255), outline=(10, 30, 50, 255), width=2)
        draw.ellipse([sx - 3, 30, sx + 3, 36], fill=(0, 229, 255, 255))

    # Oar Ports with golden oars extending into water
    for ox in [60, 90, 190, 220]:
        draw.line([(ox, 44), (ox - 10, 62)], fill=(160, 120, 60, 255), width=3)
        draw.ellipse([ox - 13, 58, ox - 7, 65], fill=(212, 175, 55, 255))

    img.save(os.path.join(OUTPUT_DIR, "paddle_flagship.png"))
    print("Generated paddle_flagship.png")

# -------------------------------------------------------------
# 7. VIKING JARL COMMANDER (At the Helm of Flagship)
# -------------------------------------------------------------
def generate_viking_jarl():
    W, H = 80, 80
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Fur Cloak / Mantle
    draw.polygon([(20, 45), (60, 45), (68, 78), (12, 78)], fill=(110, 75, 45, 255), outline=(50, 30, 15, 255), width=2)
    # White fur trim
    draw.ellipse([22, 40, 58, 52], fill=(235, 235, 235, 255))

    # Golden Chainmail Armor
    draw.polygon([(28, 48), (52, 48), (50, 76), (30, 76)], fill=(190, 160, 60, 255), outline=(80, 65, 20, 255))

    # Face & Braided Red/Blonde Beard
    draw.ellipse([30, 20, 50, 42], fill=(245, 195, 155, 255))
    # Majestic Beard
    beard_pts = [(28, 30), (52, 30), (50, 52), (40, 62), (30, 52)]
    draw.polygon(beard_pts, fill=(215, 105, 30, 255), outline=(140, 55, 10, 255))
    # Eyes & Eyebrows
    draw.line([(33, 26), (37, 26)], fill=(50, 20, 10, 255), width=2)
    draw.line([(43, 26), (47, 26)], fill=(50, 20, 10, 255), width=2)

    # Horned Gold & Steel Jarl Helmet
    draw.arc([28, 12, 52, 36], start=180, end=360, fill=(212, 175, 55, 255), width=8)
    draw.line([(40, 12), (40, 28)], fill=(255, 225, 80, 255), width=3) # Nose bridge
    # Left Horn
    draw.line([(28, 20), (14, 10)], fill=(240, 235, 220, 255), width=4)
    # Right Horn
    draw.line([(52, 20), (66, 10)], fill=(240, 235, 220, 255), width=4)

    # Raised Battleaxe
    draw.line([(60, 48), (70, 14)], fill=(140, 100, 60, 255), width=3) # Shaft
    draw.polygon([(65, 12), (76, 8), (78, 22), (68, 26)], fill=(195, 205, 215, 255), outline=(80, 90, 100, 255))

    img.save(os.path.join(OUTPUT_DIR, "viking_jarl.png"))
    print("Generated viking_jarl.png")

# -------------------------------------------------------------
# 8. LEAPING VIKING WARRIOR (Einherjar Boarding Party)
# -------------------------------------------------------------
def generate_viking_warrior():
    W, H = 84, 84
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Motion blur / strike aura
    aura = create_radial_glow((W, H), (0, 229, 255), max_alpha=100)
    img.paste(aura, (0, 0), aura)

    # Leaping Stance Legs
    draw.line([(32, 54), (22, 76)], fill=(70, 85, 110, 255), width=5)
    draw.line([(48, 54), (58, 72)], fill=(70, 85, 110, 255), width=5)

    # Leather Tunic with Cross Belts
    draw.polygon([(26, 32), (54, 32), (50, 56), (30, 56)], fill=(120, 80, 50, 255), outline=(60, 40, 20, 255), width=2)
    draw.line([(28, 34), (50, 54)], fill=(40, 25, 15, 255), width=3)
    draw.line([(52, 34), (30, 54)], fill=(40, 25, 15, 255), width=3)

    # Head & Helmet
    draw.ellipse([32, 14, 48, 30], fill=(245, 195, 155, 255))
    # Spectacle/Spangenhelm Helmet
    draw.arc([30, 8, 50, 28], start=180, end=360, fill=(160, 175, 190, 255), width=6)
    draw.ellipse([33, 16, 39, 22], outline=(100, 115, 130, 255), width=2) # Eye guard
    draw.ellipse([41, 16, 47, 22], outline=(100, 115, 130, 255), width=2)
    # Braided Beard
    draw.polygon([(34, 24), (46, 24), (43, 38), (37, 38)], fill=(190, 90, 30, 255))

    # Left Hand with Painted Round Shield
    draw.ellipse([8, 28, 32, 52], fill=(20, 90, 180, 255), outline=(212, 175, 55, 255), width=3)
    draw.ellipse([17, 37, 23, 43], fill=(220, 220, 230, 255))

    # Right Hand Striking with Great Bearded Axe
    draw.line([(48, 36), (68, 18)], fill=(130, 90, 50, 255), width=4) # Axe shaft
    # Curved Crescent Blade
    blade_pts = [(64, 10), (78, 6), (82, 24), (72, 28), (66, 20)]
    draw.polygon(blade_pts, fill=(225, 235, 245, 255), outline=(70, 85, 100, 255), width=2)

    img.save(os.path.join(OUTPUT_DIR, "viking_warrior.png"))
    print("Generated viking_warrior.png")

# -------------------------------------------------------------
# 9. ENEMY VIKING RAIDER (Enemy Longship Crew)
# -------------------------------------------------------------
def generate_viking_enemy():
    W, H = 64, 64
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Dark Woolen Tunic
    draw.polygon([(20, 26), (44, 26), (40, 50), (24, 50)], fill=(75, 45, 45, 255), outline=(40, 20, 20, 255))
    # Head & Helmet
    draw.ellipse([26, 12, 38, 24], fill=(235, 185, 145, 255))
    draw.arc([24, 6, 40, 22], start=180, end=360, fill=(130, 130, 140, 255), width=5)
    # Dark Beard
    draw.polygon([(27, 20), (37, 20), (34, 30), (30, 30)], fill=(40, 30, 25, 255))

    # Red & Black Raider Shield
    draw.ellipse([6, 22, 26, 42], fill=(180, 30, 30, 255), outline=(30, 30, 30, 255), width=2)
    draw.ellipse([14, 30, 18, 34], fill=(200, 200, 200, 255))

    # Spear
    draw.line([(38, 38), (56, 8)], fill=(120, 90, 60, 255), width=2)
    draw.polygon([(54, 10), (58, 6), (60, 12)], fill=(210, 220, 230, 255))

    img.save(os.path.join(OUTPUT_DIR, "viking_enemy.png"))
    print("Generated viking_enemy.png")

# Run all generators
if __name__ == "__main__":
    generate_ship_scout()
    generate_ship_drakkar()
    generate_ship_frost()
    generate_ship_fire()
    generate_ship_naglfar()
    generate_paddle_flagship()
    generate_viking_jarl()
    generate_viking_warrior()
    generate_viking_enemy()
    print("ALL SPRITES GENERATED SUCCESSFULLY!")
