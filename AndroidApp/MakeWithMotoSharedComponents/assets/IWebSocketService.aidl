package com.makewithmoto.network;

interface IWebSocketService {
	void start();
	void stop();
	void logToSockets(String tag, String aString);
	void sendToSockets(String msg);
	void sendJsonToSockets(String type, String json);
}