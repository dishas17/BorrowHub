import urllib.request
import os
import sys

DEST_DIR = os.path.join(os.path.dirname(__file__), "backend-java", "WebContent", "WEB-INF", "lib")
os.makedirs(DEST_DIR, exist_ok=True)

JARS = [
    ("mysql-connector-j-8.3.0.jar", "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar"),
    ("javax.servlet-api-4.0.1.jar", "https://repo1.maven.org/maven2/javax/servlet/javax.servlet-api/4.0.1/javax.servlet-api-4.0.1.jar"),
    ("jstl-1.2.jar", "https://repo1.maven.org/maven2/javax/servlet/jstl/1.2/jstl-1.2.jar"),
    ("javax.servlet.jsp-api-2.3.3.jar", "https://repo1.maven.org/maven2/javax/servlet/jsp/javax.servlet.jsp-api/2.3.3/javax.servlet.jsp-api-2.3.3.jar")
]

print("Starting download of BorrowHub Java dependencies...")
for filename, url in JARS:
    dest_path = os.path.join(DEST_DIR, filename)
    print(f"Downloading {filename}...")
    try:
        urllib.request.urlretrieve(url, dest_path)
        size_kb = os.path.getsize(dest_path) / 1024
        print(f"Successfully downloaded {filename} ({size_kb:.1f} KB)")
    except Exception as e:
        print(f"Failed to download {filename}: {e}", file=sys.stderr)

print("All dependencies downloaded to:", DEST_DIR)
