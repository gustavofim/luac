import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import checker.SemanticChecker;
import parser.LuaLexer;
import parser.LuaParser;
//import checker.SemanticChecker;
import table.IdentifierTable;

public class Main {

	public static void main(String[] args) throws IOException {
		// Cria um CharStream que lê os caracteres de um arquivo.
		// O livro do ANTLR fala para criar um ANTLRInputStream,
		// mas a partir da versão 4.7 essa classe foi deprecada.
		// Esta é a forma atual para criação do stream.
		CharStream input = CharStreams.fromFileName(args[0]);

		// Cria um lexer que consome a entrada do CharStream.
		LuaLexer lexer = new LuaLexer(input);

		// Cria um buffer de tokens vindos do lexer.
		CommonTokenStream tokens = new CommonTokenStream(lexer);

		// Cria um parser que consome os tokens do buffer.
		LuaParser parser = new LuaParser(tokens);

		// Começa o processo de parsing na regra 'program'.
		ParseTree tree = parser.chunk();

		if (parser.getNumberOfSyntaxErrors() != 0) {
			// Houve algum erro sintático. Termina a compilação aqui.
			return;
		}

		// Cria o analisador semântico e visita a ParseTree para
		// fazer a análise.
		SemanticChecker checker = new SemanticChecker();
		checker.visit(tree);

		System.out.println("PARSE SUCCESSFUL!");

		IdentifierTable idTab = new IdentifierTable();
		idTab.add("somevar", 1);
		idTab.add("somevar.thing", 2);
		idTab.add("thing", 4);
		System.out.println(idTab);

		checker.printAST();
	}
}
