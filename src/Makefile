# Comando do compilador Java
JAVAC=javac
# Comando da JVM
JAVA=java
# ROOT é a raiz dos diretórios com todos os roteiros de laboratórios
ROOT=/home/gustavo/luac
# Caminho para o JAR do ANTLR em labs/tools
ANTLR_PATH=$(ROOT)/tools/antlr-4.11.1-complete.jar
# Opção de configuração do CLASSPATH para o ambiente Java
CLASS_PATH_OPTION=-cp .:$(ANTLR_PATH)
# Configuração do comando de compilação do ANTLR
ANTLR4=$(JAVA) -jar $(ANTLR_PATH)
# Configuração do ambiente de teste do ANTLR
GRUN=$(JAVA) $(CLASS_PATH_OPTION) org.antlr.v4.gui.TestRig
# Nome da gramática
GRAMMAR_NAME=Lua
LEXER=$(GRAMMAR_NAME)Lexer
PARSER=$(GRAMMAR_NAME)Parser
# Diretório para aonde vão os arquivos gerados
GEN_PATH=parser
# Diretório para os arquivos .class
BIN_PATH=bin

# Executa o ANTLR e o compilador Java
all: antlr javac
	@echo "Done."
# Executa o ANTLR para compilar a gramática
antlr: $(LEXER).g $(PARSER).g
	$(ANTLR4) -no-listener -visitor -o $(GEN_PATH) $(PARSER).g $(LEXER).g
# Executa o javac para compilar os arquivos gerados
javac:
	rm -rf $(BIN_PATH)
	mkdir $(BIN_PATH)
	$(JAVAC) $(CLASS_PATH_OPTION) -d $(BIN_PATH) */*.java Main.java
# Executa o front-end. Comando: $ make run FILE=arquivo_de_teste
# 'Main' é a classe com a função principal
# @java -jar jasmin.jar out/$(FILE).j > /dev/null
# @java -jar jasmin.jar out/Func*.j > /dev/null
run: 
	@rm -f out/*
	@rm -f ./*.class
	@echo "Running: $(FILE).lua"
	@mkdir -p out/
	@$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) Main $(ROOT)/io/$(FILE).lua 2> tree.dot > out/$(FILE).j
	@dot -Tpdf tree.dot -o tree.pdf
	@java -jar jasmin.jar out/*.j > /dev/null
	@java -jar jasmin.jar lualib/*.j > /dev/null
	@java -cp "luaruntime.jar:." Program
test:
	@rm -f out/*
	@rm -f ./*.class
	@rm -rf TEMP
	@echo "Running: $(FILE).lua"
	@mkdir -p out/
	@mkdir TEMP/
	@$(JAVA) $(CLASS_PATH_OPTION):$(BIN_PATH) Main $(ROOT)/io/$(FILE).lua 2> tree.dot > out/$(FILE).j
	@java -jar jasmin.jar out/*.j > /dev/null
	@java -jar jasmin.jar lualib/*.j > /dev/null
	@java -cp "luaruntime.jar:." Program > TEMP/$(FILE)_out
	@lua $(ROOT)/io/$(FILE).lua > TEMP/$(FILE)_lua_out
	@diff TEMP/$(FILE)_out TEMP/$(FILE)_lua_out || true
debug:
	cd $(BIN_PATH) && $(GRUN) $(GEN_PATH).$(GRAMMAR_NAME) chunk -gui $(ROOT)/io/$(FILE).lua
# Remove os arquivos gerados pelo ANTLR
clean:
	rm -rf $(GEN_PATH) $(BIN_PATH) out/
	rm luaruntime.jar tree.*
runtime: lua/*.java
	@echo "Recompiling lua runtime..."
	@javac lua/*.java
	@jar -cf luaruntime.jar lua/*.class
	@rm lua/*.class