"""Generate Omnium stage textures from vanilla netherite shapes.

Source of truth is the vanilla client jar: netherite silhouettes and shading
are extracted and their luminance mapped through a per-stage color ramp, so
value structure (what actually reads at 16px) is preserved exactly while the
hue family changes per stage.

Stages planned: crude (ember amber), attuned (pale cyan), omnium (deep violet).
Only crude is generated for now; add stages to STAGES when they ship.

Usage:  python art/generate_textures.py
"""

import os
import zipfile
from io import BytesIO
from pathlib import Path

from PIL import Image

JAR = Path(os.environ["APPDATA"]) / "PrismLauncher/libraries/com/mojang/minecraft/1.21.1/minecraft-1.21.1-client.jar"
RESOURCES = Path(__file__).resolve().parent.parent / "src/main/resources/assets/slopcraft/textures"

# stage -> (dark, mid, light) luminance ramp anchors
STAGES = {
    "crude": ((0x3B, 0x24, 0x0E), (0xD9, 0x8F, 0x2D), (0xFF, 0xD8, 0x8A)),
}

# vanilla item texture -> our item name (per stage, {s} = stage prefix)
ITEMS = {
    "netherite_ingot": "{s}_omnium",
    "netherite_upgrade_smithing_template": "{s}_upgrade_smithing_template",
    "netherite_sword": "{s}_sword",
    "netherite_pickaxe": "{s}_pickaxe",
    "netherite_axe": "{s}_axe",
    "netherite_shovel": "{s}_shovel",
    "netherite_hoe": "{s}_hoe",
    "netherite_helmet": "{s}_helmet",
    "netherite_chestplate": "{s}_chestplate",
    "netherite_leggings": "{s}_leggings",
    "netherite_boots": "{s}_boots",
}


def ramp(lum: float, anchors) -> tuple[int, int, int]:
    """Map luminance 0..1 through a 3-stop gradient (dark, mid, light)."""
    dark, mid, light = anchors
    if lum < 0.5:
        t, a, b = lum * 2, dark, mid
    else:
        t, a, b = (lum - 0.5) * 2, mid, light
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(3))


def recolor(img: Image.Image, anchors) -> Image.Image:
    """Ramp the metal, leave wood and leather alone.

    Netherite metal is the low-saturation purple-grey family (hue ~300); handles and straps are saturated dark crimson (hue ~340-30, nether wood). Pixels that read as wood/leather
    (warm hue, real saturation) keep their vanilla colors so tools do not look
    like solid slabs of the stage metal.
    """
    import colorsys

    img = img.convert("RGBA")
    out = Image.new("RGBA", img.size)
    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = img.getpixel((x, y))
            if a == 0:
                out.putpixel((x, y), (0, 0, 0, 0))
                continue
            h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
            hue_deg = h * 360
            if s >= 0.20 and (hue_deg >= 340 or hue_deg <= 30):
                out.putpixel((x, y), (r, g, b, a))  # wood / leather: keep
                continue
            lum = (0.299 * r + 0.587 * g + 0.114 * b) / 255
            out.putpixel((x, y), (*ramp(lum, anchors), a))
    return out


def main() -> None:
    with zipfile.ZipFile(JAR) as jar:
        def load(path: str) -> Image.Image:
            return Image.open(BytesIO(jar.read(path)))

        for stage, anchors in STAGES.items():
            item_dir = RESOURCES / "item"
            item_dir.mkdir(parents=True, exist_ok=True)
            for src, dst in ITEMS.items():
                img = load(f"assets/minecraft/textures/item/{src}.png")
                recolor(img, anchors).save(item_dir / f"{dst.format(s=stage)}.png")

            armor_dir = RESOURCES / "models/armor"
            armor_dir.mkdir(parents=True, exist_ok=True)
            for layer in (1, 2):
                img = load(f"assets/minecraft/textures/models/armor/netherite_layer_{layer}.png")
                recolor(img, anchors).save(armor_dir / f"{stage}_omnium_layer_{layer}.png")

            print(f"stage '{stage}': {len(ITEMS)} items + 2 armor layers")


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