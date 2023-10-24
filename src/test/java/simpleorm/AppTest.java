package simpleorm;

import static org.junit.Assert.*;

import org.junit.Test;
import java.sql.SQLException;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
    
	public Usuario usuarioExemplo() {
		Usuario u = new Usuario();
		u.setNome("joão");
		return u;
	}
	
    @Test
    public void salvarSimples() throws SQLException {
		try {
			DAO<Usuario> dao = new DAO<>(Usuario.class, new ConectorMySql());
			assertTrue(dao.salvar(usuarioExemplo()));
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
    }
	
	@Test
	public void salvarRelacionado() throws SQLException {
		try {
			DAO<Post> dao = new DAO<>(Post.class, new ConectorMySql());
			Post p = new Post();
			p.setTexto("olá");
			p.setAuthor(usuarioExemplo());
			assertTrue(dao.salvar(p));
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
