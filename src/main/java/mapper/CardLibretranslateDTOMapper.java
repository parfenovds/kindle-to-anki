package mapper;


import dto.CardLibretranslateDTO;
import entity.Card;

// Mapper for mapping Card to CardLibretranslateDTO
public enum CardLibretranslateDTOMapper implements Mapper<Card, CardLibretranslateDTO> {
  INSTANCE;  // Singleton pattern instance

  @Override
  public CardLibretranslateDTO mapFrom(Card source) {
    // Map Card to CardLibretranslateDTO
    return CardLibretranslateDTO.builder()
        .q(source.getOriginalSentence())
        .source(source.getSourceLanguage())
        .target(source.getTargetLanguage())
        .build();
  }
}
