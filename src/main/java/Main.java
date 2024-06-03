import controller.CardController;

public class Main {
  private final static CardController cardController = CardController.INSTANCE;

  public static void main(String[] args) {
    cardController.makeCSV(
        "2015-09-07",
        "2015-09-07",
        "en",
        null,
        "ru",
        "/home/livcy/master/more/kindle_to_anki/target/classes/vocab.db",
        "/home/livcy/master/more/kindle_to_anki/target/classes/output.csv"
    );
  }
}
