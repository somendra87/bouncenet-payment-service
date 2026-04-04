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
public enum EntityType {
  CAMPAIGN("Campaign"),
  DONATION("Donation"),
  SUBSCRIPTION("Subscription"),
  ORDER("Order"),
  WALLET_TOPUP("WalletTopup");

  private final String type;

  EntityType(String type){
    this.type = type;
  }

  @JsonCreator
  public static EntityType fromValue(String text) {
    for (EntityType b : EntityType.values()) {
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
    for (EntityType entityType : EntityType.values()){
      if (StringUtils.isNotBlank(entityType.name())){
        map.put(entityType.name(), entityType.type);
      }
    }
    return map;
  }
}
