package online.talkandtravel.config;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.NullValueCheckStrategy;

/**
 * Configuration class for MapStruct, a code generator that simplifies the process of mapping
 * between Java beans or data transfer objects (DTOs) and entity classes.
 *
 * <p>This configuration sets up MapStruct with specific strategies and settings:
 *
 * <ul>
 *   <li><strong>componentModel:</strong> Configured as "spring" to integrate with Spring Framework,
 *       allowing MapStruct to generate Spring beans that can be injected into other Spring-managed
 *       components.
 *   <li><strong>injectionStrategy:</strong> Set to {@link InjectionStrategy#CONSTRUCTOR} to use
 *       constructor injection for injecting dependencies into the mapper implementations. This is
 *       preferred for better immutability and ensures that dependencies are always provided when
 *       the mapper is created.
 *   <li><strong>nullValueCheckStrategy:</strong> Set to {@link NullValueCheckStrategy#ALWAYS} to
 *       ensure that null value checks are always performed, providing additional safety by
 *       preventing null pointer exceptions and handling null values gracefully.
 *   <li><strong>implementationPackage:</strong> Specifies the base package where MapStruct will
 *       generate the mapper implementations. Replace "<PACKAGE_NAME>" with the actual package name
 *       where the generated classes should be placed.
 * </ul>
 *
 * <p>This class is used as a central configuration for all MapStruct mappers in the application.
 *
 * @see InjectionStrategy
 * @see NullValueCheckStrategy
 */
@org.mapstruct.MapperConfig(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    implementationPackage = "<PACKAGE_NAME>.impl")
public class MapperConfig {}
