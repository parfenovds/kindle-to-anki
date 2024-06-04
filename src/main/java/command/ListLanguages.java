package command;

import picocli.CommandLine.Command;

@Command(name = "list-languages", description = "Lists available languages")
public class ListLanguages implements Runnable {

  @Override
  public void run() {
    System.out.println("Available languages: English, Spanish, German, French, etc.");
  }
}
