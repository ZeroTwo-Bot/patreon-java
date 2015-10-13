package com.patreon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class OAuth {
  private final String _clientID;
  private final String _clientSecret;

  public OAuth(String clientID, String clientSecret) {
    _clientID = clientID;
    _clientSecret = clientSecret;
  }

  public JSONObject getTokens(String code, String redirectURI) {
    final Map<String, String> params = new HashMap<String, String>();
    params.put("grant_type", "authorization_code");
    params.put("code", code);
    params.put("client_id", _clientID);
    params.put("client_secret", _clientSecret);
    params.put("redirect_uri", redirectURI);
    return this.updateToken(params);
  }

  public JSONObject refreshToken(String refreshToken) {
    final Map<String, String> params = new HashMap<String, String>();
    params.put("grant_type", "refresh_token");
    params.put("refresh_token", refreshToken);
    params.put("client_id", _clientID);
    params.put("client_secret", _clientSecret);
    return this.updateToken(params);
  }

  private JSONObject updateToken(Map<String, String> params) {
    try {
      final URL url = new URL("https://api.patreon.com/oauth2/token");
      final HttpURLConnection connection = (HttpURLConnection)url.openConnection();

      connection.setDoOutput(true);
      final OutputStream os = connection.getOutputStream();
      final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
      writer.write(this.createQueryStringForParameters(params));
      writer.flush();
      writer.close();
      os.close();

      final BufferedReader stream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      final StringBuilder result = new StringBuilder(); String line;
      while ((line = stream.readLine()) != null) {
         result.append(line);
      }
      stream.close();
      return new JSONObject(result.toString());
    } catch (MalformedURLException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    return null;
  }

  private static String createQueryStringForParameters(Map<String, String> parameters) {
    final StringBuilder parametersAsQueryString = new StringBuilder();
    if (parameters != null) {
      boolean firstParameter = true;
      for (final String parameterName : parameters.keySet()) {
        try {
          parametersAsQueryString
            .append(firstParameter ? "" : "&")
            .append(parameterName)
            .append("=")
            .append(URLEncoder.encode(parameters.get(parameterName), "UTF-8"));
          firstParameter = false;
        } catch (UnsupportedEncodingException e) {
          System.out.println(e.getMessage());
        }
      }
    }
    return parametersAsQueryString.toString();
  }
}