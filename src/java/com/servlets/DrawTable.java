package com.servlets;

import com.database.DBUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author albano
 */
public class DrawTable extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        Connection conn = null;
        
        String cadena = "";
        String initSelect = " <select name='id_dep'> ";
        String options = "";
        String finSelect = " </select> ";
        String depsQuery = "select department_id, department_name from departments;";
        String query = "SELECT "
                + "employees.employee_id, "
                + "employees.first_name, "
                + "departments.department_name, "
                + "employees.last_name, "
                + "departments.department_id, "
                + "employees.salary "
                + "FROM employees, departments "
                + "WHERE employees.first_name LIKE 'A%' "
                + "AND employees.department_id = departments.department_id ";

        try (PrintWriter out = response.getWriter()) {
            try {
                conn = DBUtil.getProxoolConexion();
                Statement sentencia = conn.createStatement();
                Statement sentDeps = conn.createStatement();
                ResultSet resultado = sentencia.executeQuery(query);
                ResultSet resDeps = sentDeps.executeQuery(depsQuery);
                
                String[] row = new String[2];
                ArrayList<String[]> queryData = new ArrayList<>();
                
                if (resDeps.next()) {
                    do {               
                        row[0] = resDeps.getString("department_id");
                        row[1] = resDeps.getString("department_name");
                        queryData.add(row);
                        row = new String[2];
                        
                    } while (resDeps.next());
                }
                
                if (resultado.next()) {
                    do {
                        
                        for (String[] dep : queryData) {
                            if (resultado.getString("department_id").equals(dep[0])) {
                                
                                options += "<option value='" +
                                dep[0] + "' selected>" +
                                dep[1] +
                                "</option>";
                            }
                            else{
                                options += "<option value='" +
                                dep[0] + "'>" +
                                dep[1] +
                                "</option>";
                            }
                        }
                        cadena += "<tr>"
                                + "<td><input type='text' name='employee_id_"+resultado.getString("employee_id")+"' value='" + resultado.getString("employee_id") + "' readonly></td>"
                                + "<td>" + resultado.getString("first_name") + " " + resultado.getString("last_name") + "</td>"
                                + "<td><select name='idDep_"+resultado.getString("employee_id")+"'>" + options + finSelect + "</td>"
                                + "<td><input type='number' name='ajuste_"+resultado.getString("employee_id")+"' value='0' min='-100' max='100'></td> "
                                + "<td><input type='number' name='salario_"+resultado.getString("employee_id")+"' value='"+resultado.getString("salary")+"' readonly>"  + "</td> "
                                + "<td><input type='number' name='newsalary_'"+resultado.getString("employee_id")+" readonly></td> "
                                + "</tr>";
                    } while (resultado.next());
                }
                
                
                
                
                
//                for (String[] temp : queryData) {
//                     cadena += "<tr>"
//                                + "<td><input type='text' name='employee_id' value='" + temp[0] + "' readonly></td>"
//                                + "<td>" + temp[1] + "</td>"
//                                + "<td><input type='text' name='department_name' value='" + "hola" + "'></td>"
//                                + "<td><input type='number' name='ajuste'></td> "
//                                + "<td><input type='text' name='salary' value='" + temp[2] + "'></td> "
//                                + "<td><input type='newsalary' name='ajuste'></td> "
//                                + "</tr>";
//		}

            } catch (Exception e) {
                System.out.println(e.toString());
            } finally {
                DBUtil.cierraConexion(conn);
            }

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<style>\n"
                    + "table {\n"
                    + "font-family: arial, sans-serif;\n"
                    + "border-collapse: collapse;\n"
                    + "width: 100%;\n"
                    + "}\n"
                    + "\n"
                    + "td, th {\n"
                    + "border: 1px solid #dddddd;\n"
                    + "text-align: left;\n"
                    + "padding: 8px;\n"
                    + "}\n"
                    + "\n"
                    + "tr:nth-child(even) {\n"
                    + "background-color: #dddddd;\n"
                    + "}\n"
                    + "</style>");
            out.println("<title>Servlet PruebaConexion</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>Lista Clientes:</h2><br>");
            for(char alphabet = 'A'; alphabet <= 'Z';alphabet++) {
                
                out.println("<a href='http://localhost:8084/PracticaJDBC/PaginaLetra?letra="+alphabet+"'>" + alphabet + "</a>");
                
                if (alphabet != 'Z') { 
                    out.println("-");
                }
                
            }

            out.println("<form action='ActualizarClienteServlet' method='POST' >");

            out.println("<table class='table'>\n"
                    + "<tr>"
                    + "<th>ID EMPLEADO</th>"
                    + "<th>NOMBRE</th>"
                    + "<th>DEPARTAMENTO"
                    + "<th>% DE AJUSTE</th>"
                    + "<th>SALARIO</th>"
                    + "<th>SALARIO AJUSTADO</th>"
                    + "</tr>");
            out.println(cadena);
            out.println("</table><br>");

            out.println("<input type='submit' value='Aplicar Ajuste'>");
            out.println("</form>");
            out.println("<br>");

            out.println("<form action='InsertaClienteServlet' method='POST'>");
            //out.println("<br><br>");
            out.println("<input type='submit' value='Simular salario'>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        Connection conn = null;
        String cadena = "";
        String query = "SELECT"
                + "employees.employee_id, "
                + "employees.first_name, "
                + "employees.last_name, "
                + "departments.department_name, "
                + "employees.salary "
                + "FROM employees, departments "
                + "WHERE employees.department_id = departments.department_id "
                + "ORDER BY employees.first_name;";

        try (PrintWriter out = response.getWriter()) {
            try {
                conn = DBUtil.getProxoolConexion();
                Statement sentencia = conn.createStatement();
                ResultSet resultado = sentencia.executeQuery(query);

                if (resultado.next()) {
                    do {
                        cadena += "<tr>"
                                + "<td><input type='text' name='employee_id' value='" + resultado.getString("employee_id") + "' readonly></td>"
                                + "<td><input type='text' name='first_name' value='" + resultado.getString("first_name") + "'></td>"
                                + "<td><input type='text' name='last_name' value='" + resultado.getString("last_name") + "'></td>"
                                + "<td><input type='text' name='department_name' value='" + resultado.getString("department_name") + "'></td>"
                                + "<td><input type='text' name='salary' value='" + resultado.getString("salary") + "'></td> "
                                + "<td>" + "<a href='http://localhost:8084/DrawTable/BorrarClienteServlet?idcliente=" + resultado.getString("employee_id") + "'>Borrar</a>" + "</td>"
                                + "</tr>";
                    } while (resultado.next());
                }

            } catch (Exception e) {
                System.out.println(e.toString());
            } finally {
                DBUtil.cierraConexion(conn);
            }

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<style>\n"
                    + "table {\n"
                    + "font-family: arial, sans-serif;\n"
                    + "border-collapse: collapse;\n"
                    + "width: 100%;\n"
                    + "}\n"
                    + "\n"
                    + "td, th {\n"
                    + "border: 1px solid #dddddd;\n"
                    + "text-align: left;\n"
                    + "padding: 8px;\n"
                    + "}\n"
                    + "\n"
                    + "tr:nth-child(even) {\n"
                    + "background-color: #dddddd;\n"
                    + "}\n"
                    + "</style>");
            out.println("<title>Servlet PruebaConexion</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h2>Lista Clientes:</h2><br>");

            out.println("<form action='ActualizarClienteServlet' method='POST' >");

            out.println("<table class='table'>\n"
                    + "<tr>"
                    + "<th>ID EMPLEADO</th>"
                    + "<th>NOMBRE</th>"
                    + "<th>DEPARTAMENTO"
                    + "<th>% DE AJUSTE</th>"
                    + "<th>SALARIO</th>"
                    + "<th>SALARIO AJUSTADO</th>"
                    + "<th></th>"
                    + "</tr>");
            out.println(cadena);
            out.println("</table><br>");

            out.println("<input type='submit' value='Actualizar Datos'>");
            out.println("</form>");
            out.println("<br>");

            out.println("<form action='InsertaClienteServlet' method='POST'>");
            out.println("Nombre: <input type='text' name='nombre'><br><br>");
            out.println("Apellido Paterno: <input type='text' name='appat'><br><br>");
            out.println("Apellido Materno: <input type='text' name='apmat'><br><br>");
            out.println("Fecha de Nacimiento: <input type='text' name='fecnac'><br><br>");
            out.println("Email: <input type='text' name='email'><br><br>");
            out.println("<input type='submit' value='Insertar Cliente'>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
    }

}
