package io.birdge.linker.support.odata.decoder;

import com.alibaba.fastjson.JSONObject;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import io.birdge.linker.support.odata.decoder.impl.ErrorResultHandler;
import io.birdge.linker.support.odata.decoder.impl.IResultHandler;
import io.birdge.linker.support.odata.decoder.impl.JSONResultHandler;
import io.birdge.linker.support.odata.decoder.impl.ODataJsonBodyParser;
import io.bridge.linker.feign.context.FeignRequestContextHolder;
import io.bridge.linker.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ODataResultDecoder implements Decoder {

  private static final Logger LOGGER = LoggerFactory.getLogger(ODataResultDecoder.class);

  private static Map<String, IResultHandler> jsonResultHandlerMap = new HashMap<>();
  private static IResultHandler errorResultHandler = new ErrorResultHandler();

  @Override
  public Object decode(Response response, Type type)
      throws IOException, DecodeException, FeignException {
    Object result = null;
    Iterator<String> companyHeader = response.headers().get("Company-Id").iterator();
    if (companyHeader.hasNext()) {
      FeignRequestContextHolder.putParamValue("companyId", companyHeader.next());
    }

    BufferedReader reader = null;
    StringBuilder str = new StringBuilder();
    try {
      String line = null;
      reader = new BufferedReader(response.body().asReader());
      while ((line = reader.readLine()) != null) {
        str.append(line);
      }
      result = str.toString();
    } catch (Exception e) {
      LOGGER.error("read response as string error.");
    } finally {
      reader.close();
    }
    if (type.getTypeName().equals(String.class.getName())) {
      Collection contentTypeList = response.headers().get("Content-Type");
      if (CollectionUtils.isNotEmpty(contentTypeList) && contentTypeList.contains("text/plain")) {
        return result;
      }
    } else {
      JSONObject jsonObject = JSONObject.parseObject((String) result);

      IResultHandler resultHandler = null;
      if (jsonObject.getJSONObject("error") != null) {
        errorResultHandler.parse(jsonObject);
      } else if (jsonObject.getJSONObject("d") != null) {
        resultHandler = ODataJsonBodyParser.getInstance(type);
        jsonObject = jsonObject.getJSONObject("d");
      } else {
        resultHandler = jsonResultHandlerMap.get(type);
        if (resultHandler == null) {
          resultHandler = new JSONResultHandler(type);
          jsonResultHandlerMap.put(type.getTypeName(), resultHandler);
        }
      }
      result = resultHandler.parse(jsonObject);
    }
    return result;
  }
}
