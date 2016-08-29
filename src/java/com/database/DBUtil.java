package com.database;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author albano
 */
public class DBUtil {

    public static Connection getProxoolConexion() throws Exception {
        
        Connection conn = DriverManager.getConnection("proxool.postgresLocal");
        
        conn.setAutoCommit(false);
        
        return conn;
    }

    public static void cierraConexion(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
                conn.close();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
        }
    }
}