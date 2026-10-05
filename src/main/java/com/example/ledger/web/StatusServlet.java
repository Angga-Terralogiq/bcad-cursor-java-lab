package com.example.ledger.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Plain servlet used by the load balancer. Reports whether the database answers.
 */
public class StatusServlet extends HttpServlet {

    private final transient DataSource dataSource;

    public StatusServlet(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean dbUp;
        try (Connection connection = dataSource.getConnection()) {
            dbUp = connection.isValid(2);
        } catch (SQLException e) {
            dbUp = false;
        }
        resp.setStatus(dbUp ? HttpServletResponse.SC_OK : HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        resp.setContentType("text/plain");
        resp.getWriter().write(dbUp ? "UP" : "DOWN");
    }
}
