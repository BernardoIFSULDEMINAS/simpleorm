// SPDX-FileCopyrightText: 2023 Bernardo Gomes Negri<bernardo.negri@alunos.ifsuldeminas.edu.br>
//
// SPDX-License-Identifier: Apache-2.0

package simpleorm;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public interface DBConnection {
	PreparedStatement getStatement(String sql) throws SQLException;
}
