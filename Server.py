#!/usr/bin/env python
 
from http.server import BaseHTTPRequestHandler, HTTPServer
 
# HTTPRequestHandler class
class testHTTPServer_RequestHandler(BaseHTTPRequestHandler):

  i=0
  # GET
  def do_GET(self):
        # Send response status code
        self.send_response(200)
 
        # Send headers
        self.send_header('Content-type','text/html')
        self.end_headers()

         # Send message back to client        
        message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DATA><STATUS>1</STATUS><ALERT>EARTHQUAKE</ALERT>\
        <TIP>Drop down onto your hands and knees\nCover your head and neck with your arms\nHold on to any sturdy covering</TIP>\
        <ADDRESS>Fire Station\n5988 NSU University Ave\nDhaka</ADDRESS></DATA>"
        
        message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DATA><STATUS>0</STATUS><ALERT>SAFE</ALERT>\
        <TIP>You are safe\n</TIP><ADDRESS>.</ADDRESS></DATA>"
        
        # Write content as utf-8 data
        self.wfile.write(bytes(message, "utf8"))
        return
 
def run():
  print('starting server...')
 
  # Server settings
  # Choose port 8080, for port 80, which is normally used for a http server, you need root access
  server_address = ('192.168.0.8', 8081)
  httpd = HTTPServer(server_address, testHTTPServer_RequestHandler)
  print('running server...')
  httpd.serve_forever()
 
 
run()

#REFERENCE - https://daanlenaerts.com/blog/2015/06/03/create-a-simple-http-server-with-python-3/