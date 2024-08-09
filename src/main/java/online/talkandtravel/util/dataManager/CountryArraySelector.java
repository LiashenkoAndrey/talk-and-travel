package online.talkandtravel.util.dataManager;

import com.fasterxml.jackson.databind.JsonNode;
import online.talkandtravel.model.entity.Country;

/**
 * Interface for selecting and mapping country data from a JSON structure.
 *
 * <p>This interface defines methods for extracting and transforming country information from a JSON
 * node. It is designed to work with a JSON file containing country data, where specific details
 * need to be extracted and converted into {@link Country} objects.
 *
 * <p>Key methods include:
 *
 * <ul>
 *   <li>{@link #selectCountryArray(JsonNode)} - Extracts the JSON node representing the array of
 *       countries from the given JSON root node. This method is used to retrieve the relevant
 *       subset of the JSON structure containing the country data.
 *   <li>{@link #selectCountry(JsonNode)} - Converts a JSON node representing an individual country
 *       into a {@link Country} object. This method extracts necessary details such as country name
 *       and flag code from the JSON node.
 * </ul>
 *
 * <p>Implementations of this interface should provide concrete logic to handle the specific
 * structure of the JSON file being used.
 */
public interface CountryArraySelector {

  JsonNode selectCountryArray(JsonNode node);

  Country selectCountry(JsonNode node);
}
