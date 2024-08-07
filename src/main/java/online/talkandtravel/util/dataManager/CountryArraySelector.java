package online.talkandtravel.util.dataManager;

import com.fasterxml.jackson.databind.JsonNode;
import online.talkandtravel.model.entity.Country;

/**
 * selects required values from a any json file
 */
public interface CountryArraySelector {

  JsonNode selectCountryArray(JsonNode node);

  Country selectCountry(JsonNode node);

}
