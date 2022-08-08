package com.movie;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Filters.eq;
import static java.lang.Integer.parseInt;

@WebServlet("/MovieServlet")
public class MovieServlet extends HttpServlet {

    String uri = "mongodb://localhost:27017/?maxPoolSize=20&w=majority";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        BasicDBList and = new BasicDBList();

        String paramName = request.getParameter("name");
        if(paramName != null) {
             and.add(new BasicDBObject("name", paramName));

        }
        String paramYear = request.getParameter("year");
        if(paramYear != null) {
             and.add(new BasicDBObject("year", parseInt(paramYear)));

        }

        String paramGenre = request.getParameter("genres");
        if(paramGenre != null) {
             and.add(new BasicDBObject("genres", paramGenre));
        }

        String paramAgeLimit = request.getParameter("ageLimit");
        if(paramAgeLimit != null) {
            and.add(new BasicDBObject("ageLimit", paramAgeLimit));
        }

        BasicDBObject query;

        if(and.size() > 0) {
            query = new BasicDBObject("$and", and);
        }
        else {
            query = new BasicDBObject();
        }

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("test");
            try {
                MongoCollection<Document> movies = database.getCollection("movies");
                MongoCursor<Document> cursor = movies.find(query).iterator();
                while (cursor.hasNext()) {
                    out.println("collection is " +cursor.next().toJson() );
                }

            } catch (MongoException me) {
                out.println("<h3>An error occurred while attempting to run a command: " + me + "</h3>");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
