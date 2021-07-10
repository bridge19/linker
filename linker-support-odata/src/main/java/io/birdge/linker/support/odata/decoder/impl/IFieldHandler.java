package io.birdge.linker.support.odata.decoder.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.bridge.linker.common.exception.LinkerRuntimeException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface IFieldHandler {

  Object parse(JSONObject jsonNode);

  default Object convert(Class clz, String value) {
    if (value == null) {
      return null;
    }
    Pattern pattern = Pattern.compile("/Date\\((.*)\\)/");
    Matcher matcher = pattern.matcher(value);
    if (matcher.matches()) {
      value = matcher.group(1);
      String locale = "+0000";
      int index = value.indexOf('+');
      if (index > 0) {
        locale = value.substring(index);
        value = value.substring(0, index);
      }
      TimeZone timeZone = TimeZone.getTimeZone("UTC" + locale);
      Calendar calendar = Calendar.getInstance(timeZone);
      calendar.setTimeInMillis(Long.valueOf(value));
      Date date = calendar.getTime();
      if (clz.equals(String.class)) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
        return dateFormat.format(date);
      }
      return date;
    } else {
      if (clz.equals(Integer.class)) {
        return Integer.parseInt(value);
      } else if (clz.equals(Boolean.class)) {
        return Boolean.parseBoolean(value);
      } else if (clz.equals(Byte.class)) {
        return Byte.parseByte(value);
      } else if (clz.equals(Short.class)) {
        return Short.parseShort(value);
      } else if (clz.equals(Long.class)) {
        return Long.parseLong(value);
      } else if (clz.equals(Float.class)) {
        return Float.parseFloat(value);
      } else if (clz.equals(Double.class)) {
        return Double.parseDouble(value);
      } else if (clz.equals(String.class)) {
        return value;
      }
    }
    return null;
  }

  default JSONObject parse(String srcName, JSONObject jsonObj) {
    if (jsonObj == null) {
      return null;
    }
    if (srcName.startsWith("[") && srcName.endsWith("]")) {
      String patternValue = srcName.substring(1, srcName.length() - 1);
      JSONArray jsonArray = jsonObj.getJSONArray("results");
      if (jsonArray == null || jsonArray.size()==0) {
        return null;
      }
      if ("f".equals(patternValue)) {
        return jsonArray.getJSONObject(0);
      } else if ("l".equals(patternValue)) {
        return jsonArray.getJSONObject(jsonArray.size()-1);
      } else if (patternValue.startsWith("max#")) {
        String attrName = patternValue.substring(patternValue.indexOf('#') + 1);
        Iterator it = jsonArray.iterator();
        JSONObject maxObj = null;
        while (it.hasNext()) {
          JSONObject jObj = (JSONObject) it.next();
          if (maxObj == null) {
            maxObj = jObj;
          } else {
            Object maxObjValue = maxObj.get(attrName);
            Object toCompareValue = jObj.get(attrName);
            if (compare(maxObjValue, toCompareValue) < 0) {
              maxObj = jObj;
            }
          }
        }
        return maxObj;
      } else if (patternValue.startsWith("min#")) {
        String attrName = patternValue.substring(patternValue.indexOf('#') + 1);
        Iterator it = jsonArray.iterator();
        JSONObject minObj = null;
        while (it.hasNext()) {
          JSONObject jObj = (JSONObject) it.next();
          if (minObj == null) {
            minObj = jObj;
          } else {
            Object minObjValue = minObj.get(attrName);
            Object toCompareValue = jObj.get(attrName);
            if (compare(minObjValue, toCompareValue) > 0) {
              minObj = jObj;
            }
          }
        }
        return minObj;
      } else {
        throw new LinkerRuntimeException("unidentified partter: " + srcName);
      }
    } else if (srcName.endsWith("]")) {
      String attrName = srcName.substring(0, srcName.indexOf('['));
      String pattern = srcName.substring(srcName.indexOf('['));
      jsonObj = jsonObj.getJSONObject(attrName);
      return parse(pattern, jsonObj);
    } else {
      JSONArray jsonArray = jsonObj.getJSONArray("results");
      if(jsonArray !=null && jsonArray.size()>0){
        jsonObj = jsonArray.getJSONObject(0);
      }
      jsonObj = jsonObj.getJSONObject(srcName);
      return jsonObj;
    }
  }

  default int compare(Object obj1, Object obj2) {
    if (obj1 == null) {
      return -1;
    }
    if (obj2 == null) {
      return 1;
    }
    if (!obj1.getClass().getName().equals(obj2.getClass().getName())) {
      return 1;
    }
    if (obj1 instanceof String) {
      if(((String) obj1).matches("\\d*") && ((String) obj2).matches("\\d*")){
        return Long.valueOf((String)obj1).compareTo(Long.valueOf((String)obj2));
      }
      return ((String) obj1).compareTo((String) obj2);
    } else if (obj1 instanceof Number) {
      return ((Integer) obj1).compareTo((Integer) obj2);
    } else {
      return 1;
    }
  }
}