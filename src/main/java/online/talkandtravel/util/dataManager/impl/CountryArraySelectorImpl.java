package online.talkandtravel.util.dataManager.impl;

import com.fasterxml.jackson.databind.JsonNode;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.util.dataManager.CountryArraySelector;
import org.springframework.stereotype.Component;

@Component
public class CountryArraySelectorImpl implements CountryArraySelector {

  @Override
  public JsonNode selectCountryArray(JsonNode node) {
    return node.get("features");
  }

  @Override
  public Country selectCountry(JsonNode node) {
    JsonNode properties = node.get("properties");
    String name = properties.get("ADMIN").asText();
    String flagCode = properties.get("code").asText();
    return new Country(name, flagCode);
  }
}
