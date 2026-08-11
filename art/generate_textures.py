"""Generate Omnium stage textures from vanilla netherite shapes.

Source of truth is the vanilla client jar: netherite silhouettes and shading
are extracted and their luminance mapped through a per-stage color ramp, so
value structure (what actually reads at 16px) is preserved exactly while the
hue family changes per stage.

Preservation rules (learned the hard way):
- Sprite outlines (opaque pixels touching transparency) always stay vanilla;
  the dark border is what anchors an item in the vanilla art style.
- Warm saturated pixels (hue ~340-30) are nether-wood handles and leather
  straps on tools/armor - keep those vanilla. But the same hue family appears
  as METAL shading on the sword blade (rows < 11), the ingot's sheen band,
  and the template's entire frame - those must ramp. Policy is per-item.

Stages: crude (ember amber), attuned (pale cyan), omnium (deep violet).
The material items of attuned/omnium are animated (vertical frame strips +
mcmeta): the stage ramp brightens and dims so the metal reads as alive.

Usage:  python art/generate_textures.py
"""

import colorsys
import json
import os
import zipfile
from io import BytesIO
from pathlib import Path

from PIL import Image

JAR = Path(os.environ["APPDATA"]) / "PrismLauncher/libraries/com/mojang/minecraft/1.21.1/minecraft-1.21.1-client.jar"
RESOURCES = Path(__file__).resolve().parent.parent / "src/main/resources/assets/slopcraft/textures"

# stage -> ((dark, mid, light) ramp anchors, material item name, animation)
# animation: None, or (frames, frametime, amplitude)
STAGES = {
    "crude":   (((0x3B, 0x24, 0x0E), (0xD9, 0x8F, 0x2D), (0xFF, 0xD8, 0x8A)), "crude_omnium", None),
    "attuned": (((0x14, 0x3C, 0x40), (0x4E, 0xC8, 0xBE), (0xC8, 0xFF, 0xF4)), "attuned_omnium", (4, 12, 0.08)),
    "omnium":  (((0x12, 0x08, 0x26), (0x66, 0x3A, 0xC8), (0xAE, 0x83, 0xF5)), "omnium", (8, 6, 0.14)),
}

# Omnium alone carries diagonal energy veins: a structural mark survives a
# 16px slot where a hue shift alone can blur (attuned vs omnium metric).
VEIN_STAGES = {"omnium": (0xE2, 0xC4, 0xFF)}
_UNUSED = {
}

# vanilla texture -> our suffix; warm policy: 0 = keep all warm (handles,
# straps), 11 = keep warm only from row 11 down (sword hilt), None = ramp all
# warm (pure-material sprites: the ingot sheen and template frame must ramp).
ITEMS = {
    "netherite_ingot": ("MATERIAL", None),
    "netherite_upgrade_smithing_template": ("{s}_upgrade_smithing_template", None),
    "netherite_sword": ("{s}_sword", 11),
    "netherite_pickaxe": ("{s}_pickaxe", 0),
    "netherite_axe": ("{s}_axe", 0),
    "netherite_shovel": ("{s}_shovel", 0),
    "netherite_hoe": ("{s}_hoe", 0),
    "netherite_helmet": ("{s}_helmet", 0),
    "netherite_chestplate": ("{s}_chestplate", 0),
    "netherite_leggings": ("{s}_leggings", 0),
    "netherite_boots": ("{s}_boots", 0),
}


def ramp(lum: float, anchors) -> tuple[int, int, int]:
    """Map luminance 0..1 through a 3-stop gradient (dark, mid, light)."""
    dark, mid, light = anchors
    if lum < 0.5:
        t, a, b = lum * 2, dark, mid
    else:
        t, a, b = (lum - 0.5) * 2, mid, light
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(3))


def brighten(anchors, factor: float):
    return tuple(tuple(min(255, round(c * factor)) for c in stop) for stop in anchors)


def recolor(img: Image.Image, anchors, warm_rows_from: int | None = 0, keep_border: bool = True, vein=None) -> Image.Image:
    img = img.convert("RGBA")
    out = Image.new("RGBA", img.size)

    def alpha(x: int, y: int) -> int:
        if x < 0 or y < 0 or x >= img.width or y >= img.height:
            return 0
        return img.getpixel((x, y))[3]

    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = img.getpixel((x, y))
            if a == 0:
                out.putpixel((x, y), (0, 0, 0, 0))
                continue
            if keep_border and any(alpha(x + dx, y + dy) == 0
                                   for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                out.putpixel((x, y), (r, g, b, a))
                continue
            h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
            hue_deg = h * 360
            warm = s >= 0.20 and (hue_deg >= 340 or hue_deg <= 30)
            if warm and warm_rows_from is not None and y >= warm_rows_from:
                out.putpixel((x, y), (r, g, b, a))
                continue
            lum = (0.299 * r + 0.587 * g + 0.114 * b) / 255
            if vein is not None and lum > 0.32 and (x + y) % 4 == 0:
                out.putpixel((x, y), (*vein, a))
            else:
                out.putpixel((x, y), (*ramp(lum, anchors), a))
    return out


def save_animated(base: Image.Image, anchors, warm_policy, dest: Path, frames: int, frametime: int, amplitude: float, vein=None) -> None:
    """Vertical strip whose ramp brightness breathes; loops cleanly."""
    import math
    strip = Image.new("RGBA", (base.width, base.height * frames))
    for i in range(frames):
        factor = 1.0 + amplitude * math.sin(2 * math.pi * i / frames)
        frame = recolor(base, brighten(anchors, factor), warm_rows_from=warm_policy, vein=vein)
        strip.paste(frame, (0, base.height * i))
    strip.save(dest)
    with open(str(dest) + ".mcmeta", "w") as f:
        json.dump({"animation": {"frametime": frametime, "interpolate": True}}, f)


def main() -> None:
    with zipfile.ZipFile(JAR) as jar:
        def load(path: str) -> Image.Image:
            return Image.open(BytesIO(jar.read(path)))

        item_dir = RESOURCES / "item"
        item_dir.mkdir(parents=True, exist_ok=True)
        armor_dir = RESOURCES / "models/armor"
        armor_dir.mkdir(parents=True, exist_ok=True)

        for stage, (anchors, material_name, anim) in STAGES.items():
            for src, (dst, warm_policy) in ITEMS.items():
                img = load(f"assets/minecraft/textures/item/{src}.png")
                name = material_name if dst == "MATERIAL" else dst.format(s=stage)
                out = item_dir / f"{name}.png"
                vein = VEIN_STAGES.get(stage)
                if dst == "MATERIAL" and anim is not None:
                    save_animated(img, anchors, warm_policy, out, *anim, vein=vein)
                else:
                    recolor(img, anchors, warm_rows_from=warm_policy, vein=vein).save(out)
                    mcmeta = Path(str(out) + ".mcmeta")
                    if mcmeta.exists():
                        mcmeta.unlink()

            for layer in (1, 2):
                img = load(f"assets/minecraft/textures/models/armor/netherite_layer_{layer}.png")
                recolor(img, anchors, keep_border=False, vein=VEIN_STAGES.get(stage)).save(armor_dir / f"{material_name}_layer_{layer}.png")

            print(f"stage '{stage}': {len(ITEMS)} items + 2 armor layers" + (" (animated material)" if anim else ""))


def void_block() -> None:
    """Near-black void wall: flat darkness with sparse, barely-there depth
    specks so large surfaces don't band. Kept procedural - no vanilla base."""
    import random
    rng = random.Random(93)
    img = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), (1, 1, 2, 255))
    for _ in range(9):
        x, y = rng.randrange(16), rng.randrange(16)
        img.putpixel((x, y), (7, 6, 12, 255))
    block_dir = RESOURCES / "block"
    block_dir.mkdir(parents=True, exist_ok=True)
    img.save(block_dir / "void_block.png")
    print("void_block")


if __name__ == "__main__":
    main()
    void_block()
