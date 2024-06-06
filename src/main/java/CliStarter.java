// Import necessary classes for command line operations
import command.BookCommand;
import command.GenerateCSVCommand;
import command.ListLanguagesCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

// Define the main class for the CLI application
@Command(
    name = "KindleToAnki",
    version = "KindleToAnki 1.0",
    mixinStandardHelpOptions = true,
    subcommands = {GenerateCSVCommand.class, ListLanguagesCommand.class, BookCommand.class} // Define the subcommands available
)
public class CliStarter implements Runnable {
  public static void main(String[] args) {
    // Set up the command line interface and execute with given arguments
    int exitCode = new CommandLine(new CliStarter()).execute(args);
    // Exit the application with the exit code
    System.exit(exitCode);
  }

  @Override
  public void run() {
    // Print usage information if no subcommand is provided
    CommandLine.usage(this, System.out);
  }
}