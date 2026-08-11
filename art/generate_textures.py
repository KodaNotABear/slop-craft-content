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


def recolor(img: Image.Image, anchors, warm_rows_from: int | None = 0, keep_border: bool = True) -> Image.Image:
    """Ramp the metal; keep outline, wood, and leather vanilla.

    - Border pixels (opaque, touching transparency or the sprite edge) keep
      their vanilla color: the dark outline is what makes an item read as
      sitting IN the vanilla art style, and ramping it looks out of place.
    - Warm saturated pixels (hue ~340-30: nether-wood handles, leather straps)
      keep vanilla color, but only from row `warm_rows_from` down - the sword
      blade carries warm shading that must ramp, while its hilt must not.
      None disables warm preservation entirely.
    """
    import colorsys

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
                warm_from = 11 if src == "netherite_sword" else 0
                recolor(img, anchors, warm_rows_from=warm_from).save(item_dir / f"{dst.format(s=stage)}.png")

            armor_dir = RESOURCES / "models/armor"
            armor_dir.mkdir(parents=True, exist_ok=True)
            for layer in (1, 2):
                img = load(f"assets/minecraft/textures/models/armor/netherite_layer_{layer}.png")
                recolor(img, anchors, keep_border=False).save(armor_dir / f"{stage}_omnium_layer_{layer}.png")

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