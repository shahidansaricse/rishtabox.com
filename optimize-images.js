const fs = require("fs");
const path = require("path");
const sharp = require("sharp");

const root = path.join(__dirname, "Frontend");

const extensions = [".jpg", ".jpeg", ".png"];
const outputDir = path.join(__dirname, "optimized-images");

async function optimizeImages(dir) {
if (!fs.existsSync(dir)) {
console.log("Folder not found:", dir);
return;
}

const entries = fs.readdirSync(dir, { withFileTypes: true });

for (const entry of entries) {
    const fullPath = path.join(dir, entry.name);

    if (entry.isDirectory()) {
        await optimizeImages(fullPath);
        continue;
    }

    if (!extensions.includes(path.extname(entry.name).toLowerCase())) {
        continue;
    }

    try {
        const relativePath = path.relative(root, fullPath);
        const outputPath = path.join(
            outputDir,
            relativePath.replace(/\.(jpg|jpeg|png)$/i, ".webp")
        );

        fs.mkdirSync(path.dirname(outputPath), { recursive: true });

        await sharp(fullPath)
            .rotate()
            .resize({
                width: 1200,
                height: 1200,
                fit: "inside",
                withoutEnlargement: true
            })
            .webp({ quality: 78 })
            .toFile(outputPath);

        console.log("Optimized:", relativePath);
    } catch (error) {
        console.log("Skipped:", fullPath, "-", error.message);
    }
}

}

optimizeImages(root)
.then(() => console.log("Image optimization complete."))
.catch(console.error);