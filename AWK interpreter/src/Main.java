import java.nio.file.*;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

import org.junit.Assert;

public class Main {
	/**
	 * Prints out the contents of the file using the Parser class
	 */
	public static void main(String[] args) throws Exception {
		Path myPath = null;
		Path filePath = null;
		if(args.length == 2) {
			String codePath = "C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/" + args[0];
			myPath = Paths.get(codePath);
			String inputPath = "C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/" + args[1];
			filePath = Paths.get(inputPath);
		}
		else {
			String codePath = "C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/testFile.awk";
			myPath = Paths.get(codePath);
			String inputPath = "C:/Users/elip1/eclipse-workspace/CSI311_Lexer/src/testFile.txt";
			filePath = Paths.get(inputPath);
		}
		String document = new String(Files.readAllBytes (myPath));
		Lexer lexer = new Lexer(document);
		lexer.lex();
		Parser parse = new Parser(lexer.getTokens());
		ProgramNode program = parse.parse();
		Interpreter interp = new Interpreter(program, filePath);
	}
}
