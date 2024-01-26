package com.gmail.smaglenko.talkandtravel.service;

import com.gmail.smaglenko.talkandtravel.model.Country;

public interface CountryService {
    Country createOrUpdateCountryForUser(Country country, Long userID);

    Country save(Country country);

    Country findById(Long countryId);

    Long countUsersInCountry(Long countryId);
}