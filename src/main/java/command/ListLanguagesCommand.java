package command;

import dto.LanguageDTO;
import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.LanguageService;
import util.PathsHandler;

@Command(name = "list-languages", description = "Lists available languages")
public class ListLanguagesCommand implements Runnable {
  private final LanguageService languageService = LanguageService.INSTANCE;

  @Option(names = {"-s", "--source-language"}, description = "The source language you want to check (ISO 639-1 format, like en, de and so on)")
  private String sourceLanguage;

  @Option(names = {"-l", "--libre-address"}, description = "Address to libretranslate including http or https, ex.: https://libretranslate.de")
  private String libretranslateAddress;

  @Override
  public void run() {
    PathsHandler.setLibreAddressHolder(libretranslateAddress);
    if (sourceLanguage == null) {

      List<LanguageDTO> languages = languageService.getLanguages();
      int maxNameLength = languages.stream().map(l -> l.getName().length()).max(Integer::compareTo).get();
      System.out.println("Name" + " ".repeat(maxNameLength - 3)
          + "Code" + " ".repeat(2) + "Language pairs");
      languages.forEach(languageDTO -> {
        System.out.println((languageDTO.getName()
            + " ".repeat(maxNameLength - languageDTO.getName().length() + 1)
            + languageDTO.getCode() +
            " ".repeat(4)
            + languageDTO.getTargets()));
      });
    } else {
      List<List<String>> collect = languageService.getLanguages().stream()
          .filter(languageDTO -> languageDTO.getCode().equals(sourceLanguage))
          .map(LanguageDTO::getTargets)
          .sorted()
          .toList();
      System.out.println("Available language pairs for " + sourceLanguage + ": " + collect.get(0));
    }
  }
}
