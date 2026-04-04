package com.bouncenet.payments.enums;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author sprakas4
 * @package com.bouncenet.payments.enums
 * @project bouncenet-payment-service
 * @since 03/04/26
 */
public enum CurrencyType {
  INR("INR"),
  DOLLAR("DOLLAR");

  private final String type;

  CurrencyType(String type){
    this.type = type;
  }

  @JsonCreator
  public static CurrencyType fromValue(String text) {
    for (CurrencyType b : CurrencyType.values()) {
      if (String.valueOf(b.type).equals(text)) {
        return b;
      }
    }
    return null;
  }

  @JsonValue
  public String getName(){
    return String.valueOf(type);
  }

  public static Map<String, String> toMap(){
    Map<String , String > map = new LinkedHashMap<>();
    for (CurrencyType entityType : CurrencyType.values()){
      if (StringUtils.isNotBlank(entityType.name())){
        map.put(entityType.name(), entityType.type);
      }
    }
    return map;
  }
}
