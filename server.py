"""
BorrowHub - Local Web Server Runner
Runs on port 8000 (or first available port) and automatically opens BorrowHub in your browser.
"""

import http.server
import socketserver
import webbrowser
import os
import sys

PORT = 8000
DIRECTORY = os.path.dirname(os.path.abspath(__file__))

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def log_message(self, format, *args):
        # Clean logging
        print(f"[BorrowHub Server] {self.address_string()} - {format%args}")

def start_server():
    os.chdir(DIRECTORY)
    server_address = ("", PORT)
    try:
        with socketserver.TCPServer(server_address, Handler) as httpd:
            url = f"http://localhost:{PORT}/index.html"
            print("=" * 65)
            print("🚀 BorrowHub Campus Resource Sharing Platform is Running!")
            print(f"🔗 Local Web URL: {url}")
            print("=" * 65)
            try:
                webbrowser.open(url)
            except Exception:
                pass
            httpd.serve_forever()
    except OSError as e:
        if "address already in use" in str(e).lower() or e.errno == 98 or e.errno == 10048:
            alt_port = 8080
            print(f"Port {PORT} busy, starting on port {alt_port}...")
            with socketserver.TCPServer(("", alt_port), Handler) as httpd:
                url = f"http://localhost:{alt_port}/index.html"
                print(f"🔗 Local Web URL: {url}")
                try:
                    webbrowser.open(url)
                except Exception:
                    pass
                httpd.serve_forever()
        else:
            raise

if __name__ == "__main__":
    start_server()
