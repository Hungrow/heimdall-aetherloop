import os
from PIL import Image, ImageDraw

OUTPUT_DIR = r"app\src\main\res\drawable"

def generate_paddle_tier1():
    # Damaged / Basic wooden barge
    W, H = 220, 65
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # Simple plain oak hull with chipped planks
    hull = [(20, 25), (10, 15), (15, 10), (35, 20), (110, 22), (185, 20), (205, 10), (210, 15), (200, 25), (180, 50), (110, 55), (40, 50)]
    draw.polygon(hull, fill=(90, 60, 35, 255), outline=(50, 30, 15, 255), width=2)
    # 4 small rusted iron shields
    for sx in [45, 85, 135, 175]:
        draw.ellipse([sx - 7, 24, sx + 7, 38], fill=(120, 100, 90, 255), outline=(30, 30, 30, 255), width=1)
    img.save(os.path.join(OUTPUT_DIR, "paddle_tier1.png"))
    print("Generated paddle_tier1.png")

def generate_paddle_tier2():
    # Reinforced Drakkar Guardian
    W, H = 250, 70
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    hull = [(25, 25), (10, 15), (8, 5), (20, 4), (30, 15), (50, 20), (125, 22), (200, 20), (220, 15), (230, 4), (242, 5), (240, 15), (225, 25), (210, 50), (125, 56), (40, 50)]
    draw.polygon(hull, fill=(45, 65, 95, 255), outline=(200, 160, 40, 255), width=2)
    # Bronze dragon head
    draw.ellipse([10, 5, 18, 13], fill=(212, 175, 55, 255))
    draw.ellipse([232, 5, 240, 13], fill=(212, 175, 55, 255))
    # 6 bronze shields
    for sx in [50, 80, 110, 140, 170, 200]:
        draw.ellipse([sx - 8, 25, sx + 8, 41], fill=(180, 140, 40, 255), outline=(30, 30, 30, 255), width=2)
    img.save(os.path.join(OUTPUT_DIR, "paddle_tier2.png"))
    print("Generated paddle_tier2.png")

def generate_paddle_tier4():
    # Valhalla Ultimate Dreadnought (with radiant wings and aura)
    W, H = 320, 85
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    # Golden wings extending outward
    draw.polygon([(40, 40), (5, 5), (25, 30), (50, 45)], fill=(255, 215, 0, 240), outline=(255, 255, 255, 255))
    draw.polygon([(280, 40), (315, 5), (295, 30), (270, 45)], fill=(255, 215, 0, 240), outline=(255, 255, 255, 255))
    
    # Grand Hull with glowing Bifrost runes
    hull = [(40, 28), (25, 16), (20, 6), (32, 4), (45, 16), (65, 22), (160, 24), (255, 22), (275, 16), (288, 4), (300, 6), (295, 16), (280, 28), (260, 58), (160, 68), (60, 58)]
    draw.polygon(hull, fill=(15, 35, 75, 255), outline=(255, 225, 50, 255), width=3)
    # Luminous cyan inlay
    draw.line([(55, 26), (265, 26)], fill=(0, 229, 255, 255), width=3)
    draw.line([(60, 34), (260, 34)], fill=(255, 215, 0, 255), width=4)
    # 8 Royal shields with star emblems
    for sx in [68, 92, 116, 140, 180, 204, 228, 252]:
        draw.ellipse([sx - 9, 26, sx + 9, 44], fill=(212, 175, 55, 255), outline=(0, 229, 255, 255), width=2)
        draw.ellipse([sx - 4, 31, sx + 4, 39], fill=(0, 229, 255, 255))
    img.save(os.path.join(OUTPUT_DIR, "paddle_tier4.png"))
    print("Generated paddle_tier4.png")

def generate_cannonball():
    W, H = 48, 48
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    cx, cy = 24, 24
    # Fiery smoke aura
    for r in range(22, 12, -2):
        alpha = int(140 * (1.0 - (r / 22.0)))
        draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=(255, 60, 0, alpha))
    # Hot molten iron core
    draw.ellipse([cx - 10, cy - 10, cx + 10, cy + 10], fill=(40, 20, 20, 255), outline=(255, 120, 0, 255), width=2)
    draw.ellipse([cx - 5, cy - 5, cx + 5, cy + 5], fill=(255, 220, 50, 255))
    img.save(os.path.join(OUTPUT_DIR, "cannonball_flaming.png"))
    print("Generated cannonball_flaming.png")

if __name__ == "__main__":
    generate_paddle_tier1()
    generate_paddle_tier2()
    generate_paddle_tier4()
    generate_cannonball()
    print("ALL TIERS GENERATED!")
