package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.helper.ConnectionProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;








@WebServlet("/BuyServlet")
public class BuyServlet extends HttpServlet {
 private static final long serialVersionUID = 1L;

 protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
     String itemName = null;
     String itemcode = null;
     double itemPrice = 0;

     response.setContentType("text/html");
     PrintWriter out = response.getWriter();

     out.println("Item code: <input type='text' name='Itemcode'><br>");
     out.println("Item Quantity: <input type='number' name='itemQuantity'><br>");
     out.println("<input type='button' value='Add Item' onclick='addItem()'>");

     itemcode = request.getParameter("Itemcode");
     String itemQuantity = request.getParameter("itemQuantity");

     Connection con = null;
     try {
         con = ConnectionProvider.getConnection();
     } catch (ClassNotFoundException | SQLException e) {
         e.printStackTrace();
     }
     PreparedStatement statement = null;
     ResultSet resultSet = null;

     try {
         String sql = "SELECT item_name, item_price FROM items WHERE item_code = ?";
         statement = con.prepareStatement(sql);
         statement.setString(1, itemcode);
         resultSet = statement.executeQuery();

         out.println("<!DOCTYPE html>");
         out.println("<html>");
         out.println("<head>");
         out.println("<title>Buy Items</title>");
         out.println("<script>");
         out.println("function addItem() {");
         out.println("  var table = document.getElementById('itemTable');");
         out.println("  var row = table.insertRow(table.rows.length);");
         out.println("  var cell1 = row.insertCell(0);");
         out.println("  var cell2 = row.insertCell(1);");
         out.println("  var cell3 = row.insertCell(2);");

         if (resultSet.next()) {
             itemName = resultSet.getString("item_name");
             itemPrice = resultSet.getDouble("item_price");

             out.println("  cell1.innerHTML = '" + itemName + "';");
             out.println("  cell2.innerHTML = '" + itemPrice + "';");
             out.println("  cell3.innerHTML = '<input type=\"number\" name=\"totalPrice\" step=\"0.01\" required>';");
         }

         out.println("}");
         out.println("</script>");
         out.println("</head>");
         out.println("<body>");

         out.println("<h1>Customer Bill</h1>");
         out.println("<form action='GenerateBillServlet' method='post' id='buyForm'>");
         out.println("<table border='1' id='itemTable'>");
         out.println("<tr>");
         out.println("<th>Item Name</th>");
         out.println("<th>Item Price</th>");
         out.println("<th>Total Price</th>");
         out.println("</tr>");

         out.println("</table>");
         out.println("<input type='hidden' name='customerName' value='" + itemName + "'>");
         out.println("<input type='hidden' name='itemCode' value='" + itemcode + "'>");
         out.println("<input type='hidden' name='itemPrice' value='" + itemPrice + "'>");
         out.println("<input type='submit' value='Submit'>");
         out.println("</form>");

         out.println("</body>");
         out.println("</html>");

     } catch (SQLException e) {
         e.printStackTrace();
     } finally {
         try {
             if (resultSet != null)
                 resultSet.close();
             if (statement != null)
                 statement.close();
             if (con != null)
                 con.close();
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
 }
}

