package online.talkandtravel.util.fillCountryTable;

import com.fasterxml.jackson.databind.JsonNode;
import online.talkandtravel.model.Country;

/**
 * selects required values from a any json file
 */
public interface CountryArraySelector {

  JsonNode selectCountryArray(JsonNode node);

  Country selectCountry(JsonNode node);

}
