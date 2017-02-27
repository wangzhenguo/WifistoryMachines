package com.chomp.wifistorymachine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Response;

public class IOStreamUtils
{
  public static String formatResponse(Response response)
  {
    InputStream inputStream = response.body().byteStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    String result = null;
    String line = null;
    try {
      line = reader.readLine();
      result = line;
      while ((line = reader.readLine()) != null)
        result = result + line;
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }
}