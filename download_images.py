import urllib.request
import os

DEST_DIR = os.path.join(os.path.dirname(__file__), "assets", "images")
os.makedirs(DEST_DIR, exist_ok=True)

# Unsplash photos with User-Agent header
IMAGES = [
    # Scientific calculator on student desk
    ("calculator.jpg", "https://images.unsplash.com/photo-1511377107391-116a9d5d20b5?w=800&auto=format&fit=crop&q=85"),
    # DSA / Programming textbook
    ("dsa-book.jpg", "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&auto=format&fit=crop&q=85"),
    # Chemistry lab coat / laboratory setup
    ("lab-coat.jpg", "https://images.unsplash.com/photo-1584017911766-d451b3d0e843?w=800&auto=format&fit=crop&q=85"),
    # Badminton rackets and shuttles
    ("badminton.jpg", "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=800&auto=format&fit=crop&q=85"),
    # DSLR Camera
    ("camera.jpg", "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800&auto=format&fit=crop&q=85"),
    # Engineering drawing instruments
    ("drafter.jpg", "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=800&auto=format&fit=crop&q=85"),
    # Arduino / Electronics kit
    ("arduino.jpg", "https://images.unsplash.com/photo-1553406830-ef2513450d76?w=800&auto=format&fit=crop&q=85"),
    # Molecular model kit / chemistry
    ("molecular-kit.jpg", "https://images.unsplash.com/photo-1532094349884-543bc11b234d?w=800&auto=format&fit=crop&q=85")
]

print("Downloading authentic campus item photos...")
headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)"}

for filename, url in IMAGES:
    target_path = os.path.join(DEST_DIR, filename)
    try:
        req = urllib.request.Request(url, headers=headers)
        with urllib.request.urlopen(req) as resp, open(target_path, "wb") as f:
            f.write(resp.read())
        size_kb = os.path.getsize(target_path) / 1024
        print(f"Downloaded {filename} ({size_kb:.1f} KB)")
    except Exception as e:
        print(f"Failed to download {filename}: {e}")

print("Completed image download.")
