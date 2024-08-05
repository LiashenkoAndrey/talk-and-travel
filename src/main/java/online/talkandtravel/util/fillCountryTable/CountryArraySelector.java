package online.talkandtravel.util.fillCountryTable;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * selects required values from a any json file
 */
public interface CountryArraySelector {

  JsonNode selectCountryArray(JsonNode node);

  CountryWithNameAndFlag selectCountry(JsonNode node);

}
