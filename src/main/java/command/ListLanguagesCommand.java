package command;

import dto.LanguageDTO;
import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.LanguageService;
import util.PathsHandler;

// Define a CLI command to list available languages
@Command(name = "list-languages", description = "Lists available languages", mixinStandardHelpOptions = true)
public class ListLanguagesCommand implements Runnable {
  private final LanguageService languageService = LanguageService.INSTANCE;

  // Option for specifying the source language to check available language pairs
  @Option(names = {"-s", "--source-language"}, description = "The source language you want to check (ISO 639-1 format, like en, de and so on)")
  private String sourceLanguage;

  // Option for specifying the address of the LibreTranslate server
  @Option(names = {"-l", "--libre-address"}, description = "Address to libretranslate including http or https, ex.: https://libretranslate.de")
  private String libretranslateAddress;

  @Override
  public void run() {
    // Set the LibreTranslate address
    PathsHandler.setLibreAddressHolder(libretranslateAddress);
    if (sourceLanguage == null) {
      // Get all available languages
      List<LanguageDTO> languages = languageService.getLanguages();
      int maxNameLength = languages.stream().map(l -> l.getName().length()).max(Integer::compareTo).get();
      // Print headers "Name", "Code", and "Language pairs"
      System.out.println("Name" + " ".repeat(maxNameLength - 3)
          + "Code" + " ".repeat(2) + "Language pairs");
      // Print each language's name, code, and language pairs, aligned by the maximum name length
      languages.forEach(languageDTO -> {
        System.out.println((languageDTO.getName()
            + " ".repeat(maxNameLength - languageDTO.getName().length() + 1)
            + languageDTO.getCode() +
            " ".repeat(4)
            + languageDTO.getTargets()));
      });
    } else {
      // Get the available language pairs for the specified source language
      List<List<String>> collect = languageService.getLanguages().stream()
          .filter(languageDTO -> languageDTO.getCode().equals(sourceLanguage))
          .map(LanguageDTO::getTargets)
          .sorted()
          .toList();
      // Print the available language pairs for the source language
      System.out.println("Available language pairs for " + sourceLanguage + ": " + collect.get(0));
    }
  }
}
