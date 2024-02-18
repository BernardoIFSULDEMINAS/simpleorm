// SPDX-FileCopyrightText: 2023 Bernardo Gomes Negri<bernardo.negri@alunos.ifsuldeminas.edu.br>
//
// SPDX-License-Identifier: Apache-2.0

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
        private Friendship friendshipExemplo() {
            Usuario u1 = usuarioExemplo();
            u1.setCodigo(1);
            Usuario u2 = usuarioExemplo2();
            u2.setCodigo(2);
            Friendship f = new Friendship();
            f.setUser1(u1);
            f.setUser2(u2);
            return f;
        }
        private FriendshipName friendshipNameExemplo() {
            FriendshipName fsn = new FriendshipName();
            fsn.setFs(friendshipExemplo());
            fsn.setN("ola");
            return fsn;
        }
	public Usuario usuarioExemplo() {
		Usuario u = new Usuario();
		u.setNome("joão");
		u.setCodigo(1);
		return u;
	}
        
        public Usuario usuarioExemplo2() {
            Usuario u = new Usuario();
            u.setNome("joão2");
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
	public void testePrincipal() throws SQLException {
		// Rode mvn test -e -Dtest=AppTest#testePrincipal
		// Depois de cada teste, no SQL:
		// delete from friendship; delete from post; alter table post auto_increment = 1; delete from user; alter table user auto_increment = 1;
		salvarSimples();
		salvarRelacionado();
                salvarUsuario2();
                salvarAmizade();
                salvarFsn();
		mudarSimples();
		mudarRelacionado();
                apagarFsn();
		apagar();
	}
    
        
        @Test
        public void salvarUsuario2() throws SQLException {
            DAO<Usuario> dao_user = new DAO<>(Usuario.class, new ConectorMySql());
            assertTrue(dao_user.salvar(usuarioExemplo2()));
        }
    @Test
    public void salvarSimples() throws SQLException {
		try {
			DAO<Usuario> dao = new DAO<>(Usuario.class, new ConectorMySql());
            Usuario u = usuarioExemplo();
            u.setCodigo(null);
			assertTrue(dao.salvar(u));
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
            assertTrue(dao.salvar(p));
            p.setId(1);
            assertEquals(p, dao.localizar(1));
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}
        @Test
        public void salvarAmizade() throws SQLException {
            Friendship f = friendshipExemplo();
            DAO<Friendship> dao = new DAO(Friendship.class, new ConectorMySql());
            assertTrue(dao.salvar(f));
            assertEquals(f, dao.localizar(1,2));
        }
        
        @Test
        public void salvarFsn() throws SQLException {
            DAO<FriendshipName> dao = new DAO(FriendshipName.class, new ConectorMySql());
            assertTrue(dao.salvar(friendshipNameExemplo()));
        }
        
        @Test
        public void mudarSimples() throws SQLException {
            try {
                DAO<Usuario> dao = new DAO<>(Usuario.class, new ConectorMySql());
                Usuario u = usuarioExemplo();
                u.setNome("joão_mudado");
                assertTrue(dao.salvar(u));
                assertEquals(u, dao.localizar(1));
            } catch(RuntimeException e) {
                e.printStackTrace();
                throw e;
            }
        }
        
        @Test
        public void mudarRelacionado() throws SQLException {
            try {
                DAO<Post> dao = new DAO<>(Post.class, new ConectorMySql());
                Usuario nu = usuarioExemplo2();
                nu.setCodigo(2);
				DAO<Usuario> dao_user = new DAO<>(Usuario.class, new ConectorMySql());
				dao_user.salvar(usuarioExemplo2());
                Post p = postExemplo();
                p.setId(1);
                p.setTexto("texto_mudado");
                p.setAuthor(nu);
                assertTrue(dao.salvar(p));
            } catch(RuntimeException e) {
                e.printStackTrace();
                throw e;
            }
        }
        
        @Test
        public void apagarFsn() throws SQLException {
            DAO<FriendshipName> dao = new DAO(FriendshipName.class, new ConectorMySql());
            assertTrue(dao.apagar(friendshipNameExemplo()));
        }
        
        @Test
        public void apagar() throws SQLException {
            try {
                DAO<Post> dao = new DAO<>(Post.class, new ConectorMySql());
                Post p = postExemplo();
                p.setId(1);
                assertTrue(dao.apagar(p));
            } catch(RuntimeException e) {
                e.printStackTrace();
                throw e;
            }
        }
}

