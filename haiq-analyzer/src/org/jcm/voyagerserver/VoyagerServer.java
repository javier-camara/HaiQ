package org.jcm.voyagerserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class VoyagerServer {

	public void Start(String scoreboardJSON) {
		System.out.println("Starting server at localhost:7000");
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(7000), 0);
	        server.createContext("/configurations", new ConfigHandler(scoreboardJSON));
	        server.setExecutor(null); // creates a default executor
	        server.start();
			System.out.println("Configuration URL: http://localhost:7000/configurations");
		} catch (IOException e) {
			System.out.println("Unable to start server. Ensure no other program is using port 7000.");
			e.printStackTrace();
		}
	}

	static class ConfigHandler implements HttpHandler {
		private String ScoreboardJSON = "";
		
		public ConfigHandler(String scoreboardJSON) {
			this.ScoreboardJSON = scoreboardJSON;
		}

		@Override
		public void handle(HttpExchange t) throws IOException {
			System.out.println("Recieved: GET /configurations");
			String response = this.ScoreboardJSON;

			Headers headers = t.getResponseHeaders();
			headers.add("Access-Control-Allow-Headers","x-prototype-version,x-requested-with");
			headers.add("Access-Control-Allow-Methods","GET,POST");
			headers.add("Access-Control-Allow-Origin","*");

			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}
}
