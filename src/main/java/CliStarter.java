import command.GenerateCSV;
import command.ListLanguages;
import picocli.CommandLine;
import picocli.CommandLine.Command;


@Command(
    name = "KindleToAnki",
    version = "KindleToAnki 1.0",
    mixinStandardHelpOptions = true,
    subcommands = {GenerateCSV.class, ListLanguages.class}
)
public class CliStarter implements Runnable {
  public static void main(String[] args) {
    int exitCode = new CommandLine(new CliStarter()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public void run() {
    CommandLine.usage(this, System.out);
  }
}
