package mapper;

import dto.CardLibretranslateDTO;
import entity.Card;

public enum CardLibretranslateDTOMapper implements Mapper<Card, CardLibretranslateDTO> {
  INSTANCE;

  @Override
  public CardLibretranslateDTO mapFrom(Card source) {
    return CardLibretranslateDTO.builder()
        .q(source.getOriginalSentence())
        .source(source.getSourceLanguage())
        .target(source.getTargetLanguage())
        .build();
  }
}
