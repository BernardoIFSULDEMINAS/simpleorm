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
    
    @Test
    public void salvar() throws SQLException {
        DAO<Usuario> dao = new DAO<>(Usuario.class, new ConectorMySql());
        Usuario u = new Usuario();
        u.setNome("ola");
        dao.salvar(u);
    }
}
