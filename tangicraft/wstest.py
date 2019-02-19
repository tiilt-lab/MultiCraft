from gevent import monkey; monkey.patch_all()
from ws4py.websocket import WebSocket
from ws4py.server.geventserver import WSGIServer
from ws4py.server.wsgiutils import WebSocketWSGIApplication

class WS(WebSocket):
	def received_message(self, message):
		print message.data

server = WSGIServer(('localhost', 5051), WebSocketWSGIApplication(handler_cls=WS))
server.serve_forever()