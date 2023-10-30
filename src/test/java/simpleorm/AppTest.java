package simpleorm;

import static org.junit.Assert.*;

import org.junit.Test;
import java.sql.SQLException;
import org.junit.Assume;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    private static boolean criouPost = false;
    private static boolean criouUser = false;
    private static boolean criouUser2 = false;
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
        
        public Usuario usuarioExemplo2() {
            Usuario u = new Usuario();
            u.setNome("joão");
            return u;
        }
        
        private Post postExemplo() {
            Post p = new Post();
            p.setTexto("olá");
            Usuario u = usuarioExemplo();
            u.setCodigo(1);
            p.setAuthor(u);
            return p;
        }
	
    @Test
    public void salvarSimples() throws SQLException {
		try {
			DAO<Usuario> dao = new DAO<>(Usuario.class, new ConectorMySql());
                        criouUser = true;
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
			Post p = postExemplo();
                        criouPost = true;
			assertTrue(dao.salvar(p));
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}
        
        @Test
        public void mudarSimples() throws SQLException {
            Assume.assumeTrue(criouUser);
            try {
                DAO<Usuario> dao = new DAO<>(Usuario.class, new ConectorMySql());
                Usuario u = usuarioExemplo();
                u.setCodigo(1);
                u.setNome("joão_mudado");
                assertTrue(dao.salvar(u));
                assertEquals(dao.localizar(1), u);
            } catch(RuntimeException e) {
                e.printStackTrace();
                throw e;
            }
        }
        
        @Test
        public void mudarRelacionado() throws SQLException {
            Assume.assumeTrue(criouPost);
            try {
                DAO<Post> dao = new DAO<>(Post.class, new ConectorMySql());
                Usuario nu = usuarioExemplo2();
                nu.setCodigo(2);
                if(!criouUser2) {
                    DAO<Usuario> dao_user = new DAO<>(Usuario.class, new ConectorMySql());
                    dao_user.salvar(usuarioExemplo2());
                    criouUser2 = true;
                }
                Post p = postExemplo();
                p.setCodigo(1);
                p.setTexto("texto_mudado");
                p.setAuthor(nu);
                assertTrue(dao.salvar(p));
            } catch(RuntimeException e) {
                e.printStackTrace();
                throw e;
            }
        }
        
        @Test
        public void apagar() throws SQLException {
            Assume.assumeFalse(criouUser);
            try {
                DAO<Usuario> dao = new DAO<>(Usuario.class, new ConectorMySql());
                Usuario u = usuarioExemplo();
                u.setCodigo(5);
                assertTrue(dao.apagar(u));
            } catch(RuntimeException e) {
                e.printStackTrace();
                throw e;
            }
        }
}

