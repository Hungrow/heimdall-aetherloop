import os
import math
from PIL import Image, ImageDraw, ImageFilter

OUTPUT_DIR = r"app\src\main\res\drawable"
os.makedirs(OUTPUT_DIR, exist_ok=True)

def create_circular_portrait(size, painter_func):
    """Creates a high-res artistic circular portrait frame with shading and border."""
    W, H = size
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Outer circular mask
    cx, cy, r = W // 2, H // 2, W // 2 - 4
    
    # Artistic portrait painting
    painter_func(draw, W, H, cx, cy)
    
    # Circular Crop mask
    mask = Image.new("L", (W, H), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=255)
    
    # Composite into masked image
    output = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    output.paste(img, (0, 0), mask)
    
    # Draw ornate Norse golden border ring
    out_draw = ImageDraw.Draw(output)
    out_draw.ellipse([cx - r, cy - r, cx + r, cy + r], outline=(212, 175, 55, 255), width=5)
    out_draw.ellipse([cx - r + 3, cy - r + 3, cx + r - 3, cy + r - 3], outline=(255, 225, 80, 200), width=2)
    out_draw.ellipse([cx - r + 5, cy - r + 5, cx + r - 5, cy + r - 5], outline=(40, 25, 10, 255), width=1)
    
    return output

# 1. AVATAR: RAGNAR (Viking Jarl)
def draw_ragnar(draw, W, H, cx, cy):
    # Background: Dramatic twilight sky
    for y in range(H):
        ratio = y / H
        r = int(15 * (1 - ratio) + 40 * ratio)
        g = int(25 * (1 - ratio) + 20 * ratio)
        b = int(50 * (1 - ratio) + 15 * ratio)
        draw.line([(0, y), (W, y)], fill=(r, g, b, 255))
        
    # Fur mantle
    draw.polygon([(20, 150), (cx, 130), (W - 20, 150), (W, H), (0, H)], fill=(90, 60, 35, 255))
    draw.ellipse([30, 135, W - 30, 175], fill=(210, 195, 180, 255)) # fur trim
    
    # Neck and face
    draw.polygon([(cx - 25, 110), (cx + 25, 110), (cx + 35, 145), (cx - 35, 145)], fill=(225, 175, 135, 255))
    draw.ellipse([cx - 42, 45, cx + 42, 125], fill=(235, 185, 145, 255))
    
    # Rugged red-blonde braided beard
    beard_pts = [(cx - 40, 90), (cx + 40, 90), (cx + 35, 140), (cx + 15, 170), (cx, 175), (cx - 15, 170), (cx - 35, 140)]
    draw.polygon(beard_pts, fill=(195, 95, 25, 255), outline=(130, 50, 10, 255), width=2)
    # Braids
    draw.line([(cx, 110), (cx, 170)], fill=(150, 65, 15, 255), width=4)
    draw.line([(cx - 15, 105), (cx - 15, 155)], fill=(150, 65, 15, 255), width=3)
    draw.line([(cx + 15, 105), (cx + 15, 155)], fill=(150, 65, 15, 255), width=3)
    
    # Piercing blue eyes & fierce brow
    draw.line([(cx - 28, 70), (cx - 10, 74)], fill=(130, 50, 10, 255), width=3)
    draw.line([(cx + 10, 74), (cx + 28, 70)], fill=(130, 50, 10, 255), width=3)
    draw.ellipse([cx - 24, 76, cx - 14, 84], fill=(0, 200, 255, 255), outline=(10, 20, 30, 255))
    draw.ellipse([cx + 14, 76, cx + 24, 84], fill=(0, 200, 255, 255), outline=(10, 20, 30, 255))
    
    # Viking Winged Steel Helmet
    draw.arc([cx - 44, 25, cx + 44, 95], start=180, end=360, fill=(160, 175, 190, 255), width=14)
    draw.line([(cx, 25), (cx, 80)], fill=(212, 175, 55, 255), width=4) # Nose guard
    # Golden Wings on helmet
    draw.polygon([(cx - 40, 50), (cx - 65, 20), (cx - 35, 32)], fill=(230, 195, 60, 255), outline=(120, 95, 20, 255))
    draw.polygon([(cx + 40, 50), (cx + 65, 20), (cx + 35, 32)], fill=(230, 195, 60, 255), outline=(120, 95, 20, 255))

# 2. AVATAR: LAGERTHA (Shieldmaiden)
def draw_lagertha(draw, W, H, cx, cy):
    # Background: Aurora teal
    for y in range(H):
        ratio = y / H
        draw.line([(0, y), (W, y)], fill=(int(5 + ratio * 20), int(45 - ratio * 20), int(65 - ratio * 30), 255))
        
    # Blue chainmail & leather corslet
    draw.polygon([(25, 150), (cx, 135), (W - 25, 150), (W, H), (0, H)], fill=(25, 50, 90, 255))
    
    # Golden Braided Hair flowing over shoulders
    draw.polygon([(cx - 55, 60), (cx - 35, 165), (cx - 65, 175)], fill=(235, 200, 90, 255))
    draw.polygon([(cx + 55, 60), (cx + 35, 165), (cx + 65, 175)], fill=(235, 200, 90, 255))
    
    # Feminine warrior face
    draw.ellipse([cx - 36, 48, cx + 36, 120], fill=(245, 195, 160, 255))
    
    # Blue Norse War Paint stripe across eyes
    draw.polygon([(cx - 36, 75), (cx + 36, 75), (cx + 34, 88), (cx - 34, 88)], fill=(0, 170, 230, 180))
    
    # Determined eyes
    draw.ellipse([cx - 22, 77, cx - 12, 85], fill=(50, 160, 220, 255), outline=(20, 30, 40, 255))
    draw.ellipse([cx + 12, 77, cx + 22, 85], fill=(50, 160, 220, 255), outline=(20, 30, 40, 255))
    
    # Lips
    draw.line([(cx - 10, 106), (cx + 10, 106)], fill=(180, 80, 80, 255), width=2)
    
    # Valkyrie Silver Circlet & braids
    draw.arc([cx - 38, 38, cx + 38, 70], start=180, end=360, fill=(200, 220, 235, 255), width=5)
    draw.polygon([(cx, 34), (cx - 8, 48), (cx + 8, 48)], fill=(0, 229, 255, 255)) # Gem

# 3. AVATAR: HEIMDALL (The All-Seeing Guardian)
def draw_heimdall(draw, W, H, cx, cy):
    # Background: Bifrost cosmic radiance
    for y in range(H):
        ratio = y / H
        draw.line([(0, y), (W, y)], fill=(int(40 - ratio * 30), int(20 + ratio * 15), int(70 - ratio * 40), 255))
        
    # Gilded Asgardian Aegis Armor
    draw.polygon([(20, 140), (cx, 125), (W - 20, 140), (W, H), (0, H)], fill=(212, 175, 55, 255), outline=(255, 235, 120, 255), width=2)
    # Bifrost glow core
    draw.ellipse([cx - 18, 145, cx + 18, 181], fill=(0, 229, 255, 255))
    
    # Face & White/Silver Beard
    draw.ellipse([cx - 38, 45, cx + 38, 122], fill=(235, 190, 150, 255))
    draw.polygon([(cx - 36, 92), (cx + 36, 92), (cx + 20, 160), (cx, 168), (cx - 20, 160)], fill=(225, 230, 235, 255), outline=(160, 170, 180, 255))
    
    # Glowing All-Seeing Golden/Cyan Eyes
    draw.ellipse([cx - 24, 74, cx - 10, 84], fill=(255, 235, 59, 255), outline=(255, 255, 255, 255), width=2)
    draw.ellipse([cx + 10, 74, cx + 24, 84], fill=(255, 235, 59, 255), outline=(255, 255, 255, 255), width=2)
    
    # Ornate Horned Golden Crown Helm
    draw.arc([cx - 42, 22, cx + 42, 90], start=180, end=360, fill=(235, 195, 60, 255), width=12)
    # Majestic Curved Horns of Asgard
    draw.polygon([(cx - 38, 48), (cx - 68, 15), (cx - 52, 10), (cx - 30, 36)], fill=(245, 240, 220, 255), outline=(160, 130, 40, 255))
    draw.polygon([(cx + 38, 48), (cx + 68, 15), (cx + 52, 10), (cx + 30, 36)], fill=(245, 240, 220, 255), outline=(160, 130, 40, 255))

# 4. AVATAR: BJÖRN (Berserker of the Bear Pelt)
def draw_bjorn(draw, W, H, cx, cy):
    # Background: Muspelheim fiery red
    for y in range(H):
        ratio = y / H
        draw.line([(0, y), (W, y)], fill=(int(65 - ratio * 40), int(15 + ratio * 10), int(10), 255))
        
    # Dark brown bear pelt hood & mantle
    draw.polygon([(15, 140), (cx, 120), (W - 15, 140), (W, H), (0, H)], fill=(50, 32, 20, 255))
    # Bear head hood over brow
    draw.ellipse([cx - 52, 20, cx + 52, 90], fill=(60, 38, 24, 255), outline=(30, 18, 10, 255), width=3)
    # Bear ears
    draw.ellipse([cx - 55, 18, cx - 35, 38], fill=(50, 30, 18, 255))
    draw.ellipse([cx + 35, 18, cx + 55, 38], fill=(50, 30, 18, 255))
    # Bear fangs framing the human face
    draw.polygon([(cx - 35, 55), (cx - 28, 70), (cx - 25, 55)], fill=(240, 235, 220, 255))
    draw.polygon([(cx + 35, 55), (cx + 28, 70), (cx + 25, 55)], fill=(240, 235, 220, 255))
    
    # Fierce roaring face
    draw.ellipse([cx - 34, 52, cx + 34, 118], fill=(225, 165, 125, 255))
    # Red tribal war paint claw marks
    for dx in [-15, 0, 15]:
        draw.line([(cx + dx - 6, 60), (cx + dx + 4, 95)], fill=(210, 30, 30, 230), width=3)
        
    # Black wild beard
    draw.polygon([(cx - 35, 94), (cx + 35, 94), (cx + 25, 155), (cx - 25, 155)], fill=(30, 25, 22, 255))

# 5. AVATAR: FREYA (Runic Seer / Volva)
def draw_freya(draw, W, H, cx, cy):
    # Background: Mystical purple mist
    for y in range(H):
        ratio = y / H
        draw.line([(0, y), (W, y)], fill=(int(35 - ratio * 20), int(10 + ratio * 10), int(60 - ratio * 30), 255))
        
    # Hooded dark violet mantle
    draw.polygon([(20, 140), (cx, 125), (W - 20, 140), (W, H), (0, H)], fill=(45, 20, 65, 255))
    draw.ellipse([cx - 50, 25, cx + 50, 130], fill=(55, 25, 78, 255))
    
    # Pale mystical face
    draw.ellipse([cx - 32, 50, cx + 32, 115], fill=(240, 205, 185, 255))
    # White hair braids
    draw.polygon([(cx - 40, 80), (cx - 25, 150), (cx - 38, 155)], fill=(230, 235, 240, 255))
    draw.polygon([(cx + 40, 80), (cx + 25, 150), (cx + 38, 155)], fill=(230, 235, 240, 255))
    
    # Luminous Violet Runic Tattoos on cheeks and forehead
    draw.line([(cx, 55), (cx, 70)], fill=(213, 0, 249, 255), width=2)
    draw.line([(cx - 8, 60), (cx + 8, 60)], fill=(213, 0, 249, 255), width=2)
    # Third eye rune
    draw.ellipse([cx - 4, 62, cx + 4, 70], fill=(213, 0, 249, 255))
    
    # Violet glowing eyes
    draw.ellipse([cx - 20, 77, cx - 10, 85], fill=(225, 100, 255, 255))
    draw.ellipse([cx + 10, 77, cx + 20, 85], fill=(225, 100, 255, 255))

# 6. AVATAR: BRUNHILD (Valkyrie of the Golden Shield)
def draw_brunhild(draw, W, H, cx, cy):
    # Background: Golden dawn over Asgard
    for y in range(H):
        ratio = y / H
        draw.line([(0, y), (W, y)], fill=(int(60 - ratio * 35), int(45 - ratio * 25), int(20 + ratio * 15), 255))
        
    # Silver & gold plate armor with winged spaulders
    draw.polygon([(20, 140), (cx, 125), (W - 20, 140), (W, H), (0, H)], fill=(200, 210, 225, 255), outline=(212, 175, 55, 255), width=2)
    
    # Golden braided hair
    draw.polygon([(cx - 45, 60), (cx - 28, 160), (cx - 52, 165)], fill=(245, 215, 95, 255))
    draw.polygon([(cx + 45, 60), (cx + 28, 160), (cx + 52, 165)], fill=(245, 215, 95, 255))
    
    # Noble serene face
    draw.ellipse([cx - 34, 48, cx + 34, 116], fill=(245, 200, 170, 255))
    
    # Emerald green radiant eyes
    draw.ellipse([cx - 20, 75, cx - 10, 83], fill=(0, 230, 118, 255), outline=(10, 40, 20, 255))
    draw.ellipse([cx + 10, 75, cx + 20, 83], fill=(0, 230, 118, 255), outline=(10, 40, 20, 255))
    
    # Silver Winged Valkyrie Helmet
    draw.arc([cx - 38, 30, cx + 38, 75], start=180, end=360, fill=(210, 225, 240, 255), width=8)
    # Broad White Feathers / Wings
    draw.polygon([(cx - 35, 45), (cx - 65, 12), (cx - 45, 20)], fill=(255, 255, 255, 255), outline=(180, 195, 210, 255))
    draw.polygon([(cx + 35, 45), (cx + 65, 12), (cx + 45, 20)], fill=(255, 255, 255, 255), outline=(180, 195, 210, 255))

# Generate All Avatars
if __name__ == "__main__":
    avatars = [
        ("avatar_ragnar.png", draw_ragnar),
        ("avatar_lagertha.png", draw_lagertha),
        ("avatar_heimdall.png", draw_heimdall),
        ("avatar_bjorn.png", draw_bjorn),
        ("avatar_freya.png", draw_freya),
        ("avatar_brunhild.png", draw_brunhild)
    ]
    for filename, painter in avatars:
        img = create_circular_portrait((180, 180), painter)
        img.save(os.path.join(OUTPUT_DIR, filename))
        print(f"Generated {filename}")
    print("ALL AVATARS GENERATED SUCCESSFULLY!")
