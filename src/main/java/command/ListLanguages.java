package command;

import dto.LanguageDTO;
import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import service.LanguageService;

@Command(name = "list-languages", description = "Lists available languages")
public class ListLanguages implements Runnable {
  private final LanguageService languageService = LanguageService.INSTANCE;

  @Option(names = {"-s", "--source-language"}, description = "The source language you want to check (ISO 639-1 format, like en, de and so on)")
  private String sourceLanguage;

  @Override
  public void run() {
    if(sourceLanguage == null) {
      languageService.getLanguages().forEach(languageDTO -> {
        System.out.println();
        System.out.println((languageDTO.getName() + " " + languageDTO.getCode() + " " + languageDTO.getTargets()));
      });
    } else {
      List<List<String>> collect = languageService.getLanguages().stream()
          .filter(languageDTO -> languageDTO.getCode().equals(sourceLanguage))
          .map(LanguageDTO::getTargets)
          .sorted()
          .toList();
      System.out.println(collect.get(0));
    }
  }
}
