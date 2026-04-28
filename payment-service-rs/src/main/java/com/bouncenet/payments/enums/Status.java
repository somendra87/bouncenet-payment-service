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
public enum Status {
  CREATED("CREATED"), PAID("PAID"), FAILED("FAILED");

  private final String type;

  Status(String type){
    this.type = type;
  }

  @JsonCreator
  public static Status fromValue(String text) {
    for (Status b : Status.values()) {
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
    for (Status entityType : Status.values()){
      if (StringUtils.isNotBlank(entityType.name())){
        map.put(entityType.name(), entityType.type);
      }
    }
    return map;
  }
}
