package online.talkandtravel.util.fillCountryTable.impl;

import com.fasterxml.jackson.databind.JsonNode;
import online.talkandtravel.model.Country;
import online.talkandtravel.util.fillCountryTable.CountryArraySelector;
import org.springframework.stereotype.Component;

@Component("CountryArraySelectorImpl_v1")
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
