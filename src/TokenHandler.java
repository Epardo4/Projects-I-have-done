import java.util.LinkedList;
import java.util.Optional;
public class TokenHandler {
	public LinkedList<Token> tokens;
	public TokenHandler(LinkedList<Token> t) {
		tokens = t;
	}
	public Optional<Token> peek(int j){
		if(j + 1 > tokens.size()) return Optional.empty();
		return Optional.of(tokens.get(j));
	}
	public boolean moreTokens() {
		if(tokens.isEmpty()) return false;
		return true;
	}
	public Optional<Token> matchAndRemove(Token.TokenType t){
		if(moreTokens() && tokens.getFirst().getType().equals(t)) {
			Token toReturn = tokens.getFirst();
			tokens.removeFirst();
			return Optional.of(toReturn);
		}
		return Optional.empty();
	}
}