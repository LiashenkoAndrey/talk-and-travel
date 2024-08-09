package online.talkandtravel.util.dataManager.impl;

import com.fasterxml.jackson.databind.JsonNode;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.util.dataManager.CountryArraySelector;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link CountryArraySelector} interface that provides
 * methods to extract country information from a JSON structure.
 * <p>
 * This class is responsible for parsing a JSON node to retrieve an array of
 * countries and converting individual JSON nodes into {@link Country} objects.
 * It is designed to work with a specific JSON structure where country data is
 * nested under "features" and country properties are under "properties".
 *
 * <p>Key methods include:
 * <ul>
 *   <li>{@link #selectCountryArray(JsonNode)} - Extracts the array of country
 *       features from the given JSON node. This method is expected to retrieve
 *       the "features" array from the JSON structure which contains country data.</li>
 *   <li>{@link #selectCountry(JsonNode)} - Converts a JSON node representing
 *       a country into a {@link Country} object. It extracts the country name
 *       and flag code from the "properties" node of the JSON structure.</li>
 * </ul>
 *
 * <p>This implementation assumes a specific structure for the JSON input and should
 * be adjusted if the JSON structure changes.
 */

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
